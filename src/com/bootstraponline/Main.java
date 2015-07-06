package com.bootstraponline;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    private static String pathArg;
    private static Path path;
    private static PosixPermissionVisitor visitor = new PosixPermissionVisitor();

    private static void puts(String string) {
        System.out.println(string);
    }

    public static void main(String[] args) throws Exception {
        if (args.length > 0) {
            pathArg = args[0];
        } else {
            puts("Usage: java -jar fix_permissions.jar path");
            System.exit(0);
        }

        path = Paths.get(pathArg);

        // Retain execute bit on files. 755
        visitor.preserveExecuteFile = true;

        puts("Walking: " + path);
        Files.walkFileTree(path, visitor);
        puts("Walked " + visitor.dirCount + " folders and "
                + visitor.fileCount + " files");
    }
}
