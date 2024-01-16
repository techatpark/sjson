package com.techatpark.sjson;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class JsonValidationTest {

    public final Json sJson = new Json();

    /**
     * Parameterized test for illegal JSON texts.
     *
     * @param filePath Path to the JSON file
     * @throws IOException if there is an issue reading the file
     */
    @ParameterizedTest
    @MethodSource("getIllegalJsonFilePaths")
    void testIllegalJson(Path filePath) throws IOException {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            sJson.read(new BufferedReader(new FileReader(filePath.toFile())));
        });
    }

    /**
     * Provides paths to illegal JSON files for parameterized tests.
     *
     * @return Stream of paths to illegal JSON files
     * @throws IOException if there is an issue listing files
     */
    static Stream<Path> getIllegalJsonFilePaths() throws IOException {
        String illegalFolderPath = "src/test/resources/illegal";
        try (Stream<Path> stream = Files.list(Paths.get(illegalFolderPath))) {
            return stream
                    .filter(path -> !Files.isDirectory(path))
                    .collect(Collectors.toList())
                    .stream();
        }
    }
}
