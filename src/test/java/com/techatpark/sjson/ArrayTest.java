package com.techatpark.sjson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonParser;
import com.techatpark.sjson.util.TestDataProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class ArrayTest {
    final ObjectMapper objectMapper = new ObjectMapper();


    /**
     * Provides paths to JSON files for parameterized tests.
     *
     * @return Stream of paths to JSON files
     * @throws IOException if there is an issue listing files
     */
    private static Set<Path> jsonFilePath() throws IOException {
        return TestDataProvider.getJSONArrayFiles();
    }

    @ParameterizedTest
    @MethodSource("jsonFilePath")
    void testValid(final Path path) throws IOException {
        Assertions.assertEquals(JsonParser
                        .parseReader(
                                new StringReader(objectMapper.writeValueAsString(
                                        Json.read(
                                                new FileReader(path.toFile())
                                        )))),
                JsonParser.parseReader(new FileReader(path.toFile())),
                "Reverse JSON Failed for " + path);
    }
}