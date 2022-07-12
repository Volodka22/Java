package info.kgeorgiy.ja.smaglii.implementor;


import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.JarImpler;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

public class Implementor implements Impler, JarImpler {

    private static final SimpleFileVisitor<Path> DELETE_VISITOR = new SimpleFileVisitor<>() {
        @Override
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
        }
    };


    public static void compileFile(final Path root, final Class<?> token) throws ImplerException {
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (Objects.isNull(compiler)) {
            throw new ImplerException("Could not find java compiler, include tools.jar to classpath");
        }
        try {
            final String classpath = root + File.pathSeparator + Path.of(token.getProtectionDomain().getCodeSource().getLocation().toURI());
            final String[] args = {
                    root.resolve(
                            Path.of(
                                    token.getPackageName().replace('.', File.separatorChar),
                                    ImplementGenerator.getClassName(token) + ".java"
                            )
                    ).toString(),
                    "-cp",
                    classpath
            };
            System.err.println(args[0] + "\n" + args[1] + "\n" + args[2]);
            final int exitCode = compiler.run(null, null, null, args);
            if (exitCode != 0) {
                throw new ImplerException("Compiler exit code " + exitCode);
            }
        } catch (URISyntaxException e) {
            throw new ImplerException("URI exception: " + e.getMessage());
        }
    }



    @Override
    public void implementJar(Class<?> token, Path jarFile) throws ImplerException {
        if (jarFile.getParent() == null) {
            return;
        }
        try {
            Files.createDirectories(jarFile.getParent());
            final Path jarPath = Files.createTempDirectory(jarFile.toAbsolutePath().getParent(), "temp");
            try {
                implement(token, jarPath);
                compileFile(jarPath, token);
                final Manifest manifest = new Manifest();
                manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
                try (final JarOutputStream jarOutputStream = new JarOutputStream(Files.newOutputStream(jarFile), manifest)) {
                    String classpath = String.format(
                            "%s/%s.class",
                            token.getPackageName().replace('.', '/'),
                            ImplementGenerator.getClassName(token)
                    );
                    jarOutputStream.putNextEntry(new ZipEntry(classpath));
                    Files.copy(Path.of(jarPath.toString(), classpath), jarOutputStream);
                } catch (IOException e) {
                    throw new ImplerException("Cannot write jar file: " + e.getMessage());
                }
            } finally {
                Files.walkFileTree(jarPath, DELETE_VISITOR);
            }
        } catch (IOException e) {
            throw new ImplerException("Cannot create directory: " + e.getMessage());
        }
    }


    @Override
    public void implement(Class<?> token, Path root) throws ImplerException {

        if (token == null) {
            throw new ImplerException("token is null");
        }

        if (root == null) {
            throw new ImplerException("root is null");
        }

        final int mod = token.getModifiers();
        if (Modifier.isPrivate(mod) ||
                Modifier.isFinal(mod) ||
                token.isPrimitive() ||
                token == Enum.class ||
                token.isArray()
        ) {
            throw new ImplerException("unsupported token " + token.getCanonicalName());
        }

        final ImplementGenerator generator = new ImplementGenerator(token);

        final Path implPath = root.resolve(token.getPackageName().replace('.', File.separatorChar))
                .resolve(ImplementGenerator.getClassName(token) + ".java");

        // :NOTE: getParent into a variable
        final Path implParent = implPath.getParent();
        if (Files.notExists(implParent)) {
            try {
                Files.createDirectories(implParent);
            } catch (IOException e) {
                // :NOTE: getCause ofter isn't the best way to get message about what happened
                // since people don't override it
                throw new ImplerException("Cannot create directory: " + e.getMessage(), e.getCause());
            }
        }

        try (final BufferedWriter writer = Files.newBufferedWriter(implPath)) {
            writer.write(unicodeRepresentation(generator.generate()));
        } catch (IOException e) {
            throw new ImplerException("Cannot write into Impl file: " + e.getMessage(), e.getCause());
        }

    }

    private String unicodeRepresentation(final String s) {
        return s.chars().mapToObj(c -> String.format("\\u%04X", c)).collect(Collectors.joining());
    }

    public static void main(String[] args) {
        if (args == null || args.length != 2) {
            System.err.println("args' size must equals 2");
            return;
        }
        try {
            new Implementor().implement(Class.forName(args[0]), Path.of(args[1]));
        } catch (ImplerException e) {
            System.err.println("ImplerException: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("ClassNotFoundException: " + e.getMessage());
        } catch (InvalidPathException e) {
            System.err.println("InvalidPathException: " + e.getMessage());
        }
    }

}
