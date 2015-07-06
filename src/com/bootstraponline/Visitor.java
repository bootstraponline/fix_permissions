package com.bootstraponline;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.HashSet;
import java.util.Set;

/*

default OS X permissions:

-rw-r--r--   1 user  wheel    1 Jul  6 12:18 ok.txt --- file   - 644
drwxr-xr-x   2 user  wheel   68 Jul  6 12:17 tmp    --- folder - 755

*/

public class Visitor extends SimpleFileVisitor<Path> {

    private static Set<PosixFilePermission> dirPermission;
    private static Set<PosixFilePermission> filePermission;

    static {

        // Directory 755
        dirPermission = new HashSet<>();
        // Owner - RWE - 7
        dirPermission.add(PosixFilePermission.OWNER_READ); // 4
        dirPermission.add(PosixFilePermission.OWNER_WRITE); // 2
        dirPermission.add(PosixFilePermission.OWNER_EXECUTE); // 1
        // Group - RE - 5
        dirPermission.add(PosixFilePermission.GROUP_READ); // 4
        dirPermission.add(PosixFilePermission.GROUP_EXECUTE); // 1
        // Other - RE - 5
        dirPermission.add(PosixFilePermission.OTHERS_READ); // 4
        dirPermission.add(PosixFilePermission.OTHERS_EXECUTE); // 1

        // File 644
        filePermission = new HashSet<>();
        // Owner - RW - 6
        filePermission.add(PosixFilePermission.OWNER_READ); // 4
        filePermission.add(PosixFilePermission.OWNER_WRITE); // 2
        // Group - R - 4
        filePermission.add(PosixFilePermission.GROUP_READ); // 4
        // Other - R - 4
        filePermission.add(PosixFilePermission.OTHERS_READ); // 4
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        Files.setPosixFilePermissions(dir, dirPermission);

        return super.preVisitDirectory(dir, attrs);
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        Files.setPosixFilePermissions(file, filePermission);

        // todo: optionally set executable permission if it's already set on the file
        // allows preserving executable binaries as executable files although sometimes
        // it's desirable to fix permissions and always set 644.

        return super.visitFile(file, attrs);
    }
}
