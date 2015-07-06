package com.bootstraponline;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.*;
import java.util.Set;

import java.nio.file.attribute.PosixFileAttributes;

import static java.nio.file.attribute.PosixFilePermission.*;
/*
Default OS X permissions:


drwxr-xr-x   2 user  wheel   68 Jul  6 12:17 tmp    --- folder - 755
-rw-r--r--   1 user  wheel    1 Jul  6 12:18 ok.txt --- file   - 644
*/

public class PosixPermissionVisitor extends SimpleFileVisitor<Path> {
    private static Set<PosixFilePermission> dirPermission;
    private static Set<PosixFilePermission> filePermission;
    private static Set<PosixFilePermission> fileExecutePermission;
    private static Set<PosixFilePermission> executeSet;
    public static int fileCount = 0;
    public static int dirCount = 0;
    public static boolean preserveExecuteFile = true;

    static {
        // r = 4, w = 2, x = 1

        // Directory | rwx r-x r-x | 755 | default
        dirPermission = permissionString("rwx" + "r-x" + "r-x");

        // File | rw- r-- r-- | 644 | default
        filePermission = permissionString("rw-" + "r--" + "r--");

        // File | rwx r-- r-- | 755 | only for select files
        fileExecutePermission = dirPermission;

        executeSet = ImmutableSet.of(OWNER_EXECUTE, GROUP_EXECUTE, OTHERS_EXECUTE);
    }

    private static Set permissionString(final String string) {
        return ImmutableSet.copyOf(PosixFilePermissions.fromString(string));
    }

    private static void setPermission(Path path, Set<PosixFilePermission> permission) {
        try {
            Files.setPosixFilePermissions(path, permission);
        } catch (Exception e) {
            System.out.println("Set permission " + permission + " failed: " + path + " Exception: " + e.toString());
        }
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        if (!attrs.isSymbolicLink()) { // setPermission will error on symbolic links
            dirCount++;
            setPermission(dir, dirPermission);
        }

        return super.preVisitDirectory(dir, attrs);
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (!attrs.isSymbolicLink()) {
            fileCount++;

            Set<PosixFilePermission> permission = filePermission;

            if (preserveExecuteFile) {
                final Set<PosixFilePermission> targetPermissions = Files.readAttributes(file, PosixFileAttributes.class).permissions();
                boolean anyExecuteEnabled = !Sets.intersection(targetPermissions, executeSet).isEmpty();
                if (anyExecuteEnabled) {
                    permission = fileExecutePermission;
                }
            }

            setPermission(file, permission);
        }

        return super.visitFile(file, attrs);
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        System.out.println("Visit failed: " + file + " Exception: " + exc.toString());
        return FileVisitResult.CONTINUE;
    }
}
