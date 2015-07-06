package com.bootstraponline;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

/*
Default OS X permissions:


drwxr-xr-x   2 user  wheel   68 Jul  6 12:17 tmp    --- folder - 755
-rw-r--r--   1 user  wheel    1 Jul  6 12:18 ok.txt --- file   - 644
*/

public class PosixPermissionVisitor extends SimpleFileVisitor<Path> {
    private static Set<PosixFilePermission> dirPermission;
    private static Set<PosixFilePermission> filePermission;

    static {
        // r = 4, w = 2, x = 1

        // Directory | rwx r-x r-x | 755
        dirPermission = PosixFilePermissions.fromString("rwx" + "r-x" + "r-x");

        // File | rw- r-- r-- | 644
        filePermission = PosixFilePermissions.fromString("rw-" + "r--" + "r--");
    }

    private static void setPermission(Path path, Set<PosixFilePermission> permission) {
        try {
            Files.setPosixFilePermissions(path, dirPermission);
        } catch (Exception e) {
            System.out.println("Set permission " + permission + " failed: " + path + " Exception: " + e.getMessage());
        }
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        setPermission(dir, dirPermission);

        return super.preVisitDirectory(dir, attrs);
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        setPermission(file, filePermission);

        // todo: optionally set executable permission if it's already set on the file
        // allows preserving executable binaries as executable files although sometimes
        // it's desirable to fix permissions and always set 644.

        return super.visitFile(file, attrs);
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        System.out.println("Visit failed: " + file + " Exception: " + exc.getMessage());
        return FileVisitResult.CONTINUE;
    }
}
