package com.bootstraponline;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    private static String pathArg;
    private static Path path;
    private static PosixPermissionVisitor posixPermissionVisitor = new PosixPermissionVisitor();

    public static void main(String[] args) throws Exception {
        if (args.length > 0) {
            pathArg = args[0];
        } else {
            System.out.println("Usage: java -jar fix_permissions.jar path");
        }

        path = Paths.get(pathArg);

        Files.walkFileTree(path, posixPermissionVisitor);
    }
}
