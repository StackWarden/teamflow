package org.teamflow;

import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtil {
    public static String readSQLFile(String path) {
        try {
            return new String(Files.readAllBytes(Paths.get(path)));
        } catch (Exception e) {
            throw new RuntimeException("Failed to read SQL file: " + path, e);
        }
    }
}
