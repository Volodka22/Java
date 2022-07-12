package info.kgeorgiy.ja.smaglii.walk;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.util.HexFormat;

public class WalkFileVisitor extends SimpleFileVisitor<Path> {
    private final BufferedWriter writer;
    private final MessageDigest md;
    private final byte[] buffer = new byte[1 << 16];

    WalkFileVisitor(BufferedWriter writer, MessageDigest md) {
        this.writer = writer;
        this.md = md;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

        md.reset();

        try (final InputStream is = Files.newInputStream(file)) {
            int c;
            while ((c = is.read(buffer)) >= 0) {
                md.update(buffer, 0, c);
            }
            WalkUtils.printCode(writer, file.toString(), HexFormat.of().formatHex(md.digest()));
            return FileVisitResult.CONTINUE;
        } catch (final IOException e) {
            return visitFileFailed(file, e);
        }

    }

    @Override
    public FileVisitResult visitFileFailed(Path path, IOException exc) throws IOException {
        WalkUtils.printErrorCode(writer, path.toString());
        return FileVisitResult.CONTINUE;
    }
}
