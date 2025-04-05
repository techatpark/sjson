package com.techatpark.sjson.util;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
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

    /**
     * Provides Numbers to Test.
     *
     * @return Stream of Numbers
     * @throws IOException if there is an issue listing files
     */
    public static List<Number> numbers() {
        return List.of(
                Byte.MIN_VALUE,
                Byte.MAX_VALUE,
                Short.MIN_VALUE,
                Short.MAX_VALUE,
                Integer.MIN_VALUE,
                Integer.MAX_VALUE,
                Long.MIN_VALUE,
                Long.MAX_VALUE,
                BigInteger.valueOf(Long.MIN_VALUE).multiply(BigInteger.TEN),
                BigInteger.valueOf(Long.MAX_VALUE).multiply(BigInteger.TEN),
                Float.MIN_VALUE,
                Float.MAX_VALUE,
                Double.MIN_VALUE,
                123,
                -456,
                12.34,
                -0.567,
                1.23e4,
                5.67E-8,
                0.456,
                1.23e001,
                +789,
                0.4e006,
                0.4e-006,
                0.4e+006,
                4e006,
                4e-006,
                4e+006
        );
    }
}
