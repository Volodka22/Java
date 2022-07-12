package info.kgeorgiy.ja.smaglii.walk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.EnumSet;

public final class WalkUtils {

    private final static String ERROR_CODE = "0".repeat(40);

    public static void printCode(final BufferedWriter writer, final String path, final String code) throws IOException {
        writer.write(code + " " + path);
        writer.newLine();
    }

    public static void printErrorCode(final BufferedWriter writer, final String path) throws IOException {
        printCode(writer, path, ERROR_CODE);
    }

    public static void walk(final String input, final String output, final int maxDepth) throws WalkException {
        if (input == null || output == null) {
            throw new NullPointerException("Arguments must be not null");
        }

        final Path outPath;

        try {
            outPath = Paths.get(output);

            if (Files.isDirectory(outPath) || outPath.getParent() == null) {
                throw new WalkException("Output path must be a file but it is: " + output);
            }

            if (!Files.isDirectory(outPath) && !Files.exists(outPath.getParent())) {
                Files.createDirectories(outPath.getParent());
            }
        } catch (final InvalidPathException e) {
            throw new WalkException("Invalid output path", e);
        } catch (final IOException e) {
            throw new WalkException("Cannot create directory", e);
        }

        final MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (final NoSuchAlgorithmException e) {
            throw new WalkException("SHA-1 not found", e);
        }

        try (final BufferedReader reader = Files.newBufferedReader(Paths.get(input))) {

            try (final BufferedWriter writer = Files.newBufferedWriter(outPath)) {
                final FileVisitor<Path> walker = new WalkFileVisitor(writer, md);

                String filePath;
                while ((filePath = reader.readLine()) != null) {
                    try {
                        Files.walkFileTree(Paths.get(filePath), EnumSet.noneOf(FileVisitOption.class), maxDepth, walker);
                    } catch (final InvalidPathException e) {
                        WalkUtils.printErrorCode(writer, filePath);
                    }
                }
            } catch (final InvalidPathException e) {
                throw new WalkException("Invalid output path", e);
            } catch (final IOException e) {
                throw new WalkException("Cannot open output file", e);
            }

        } catch (final InvalidPathException e) {
            throw new WalkException("Invalid input path", e);
        } catch (final IOException e) {
            throw new WalkException("Cannot open input file", e);
        }

    }
}
