package com.techatpark.sjson.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestUtil {

    /**
     * Utility to get Json Files from Test Resources directory.
     * @return Set of Paths
     * @throws IOException
     */
    static Stream<Path> jsonFilePaths() throws IOException {
        String baseFolder = System.getenv("SJSON_LOCAL_DIR") == null ? "src/test/resources/samples" :
                System.getenv("SJSON_LOCAL_DIR");
        try (Stream<Path> stream = Files.list(Paths.get(baseFolder))) {
            return stream
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList())
                    .stream();
        }
    }
}
