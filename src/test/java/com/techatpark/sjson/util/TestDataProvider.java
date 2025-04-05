package com.techatpark.sjson.util;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestDataProvider {

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
                    .filter(path -> !Files.isDirectory(path))
                    .collect(Collectors.toSet());
        }
    }

    /**
     * Utility to get Json Object Files from Test Resources directory.
     * @return Set of Paths
     * @throws IOException
     */
    public static Set<Path> getJSONObjectFiles() throws IOException {
        try (Stream<Path> stream = getJSONFiles().stream()) {
            return stream
                    .filter(path -> {
                        try {
                            return Files.readString(path).trim().startsWith("{");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toSet());
        }
    }

    /**
     * Utility to get Json Array Files from Test Resources directory.
     * @return Set of Paths
     * @throws IOException
     */
    public static Set<Path> getJSONArrayFiles() throws IOException {
        try (Stream<Path> stream = getJSONFiles().stream()) {
            return stream
                    .filter(path -> {
                        try {
                            return Files.readString(path).trim().startsWith("[");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toSet());
        }
    }

}
