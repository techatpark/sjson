package com.techatpark.sjson.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestUtil {

    private static String baseFolder = System.getenv("SJSON_LOCAL_DIR") == null ? "src/test/resources" :
            System.getenv("SJSON_LOCAL_DIR");

    /**
     * Utility to get Json Files from Test Resources directory.
     * @return Set of Paths
     * @throws IOException
     */
    public static Set<Path> getJSONFiles() throws IOException {

        try (Stream<Path> stream = Files.list(new File(baseFolder, "samples").toPath())) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .collect(Collectors.toSet());
        }
    }

    /**
    * Utility to get Json Schema Files from Test Resources directory.
     * @return Set of Paths
     * @throws IOException
     */
    public static Set<File> getJSONSchemaFiles() throws IOException {

        try (Stream<Path> stream = Files.list(new File(baseFolder, "schemas").toPath())) {
            return stream
                    .filter(path -> !Files.isDirectory(path))
                    .map(Path::toFile)
                    .collect(Collectors.toSet());
        }
    }
}
