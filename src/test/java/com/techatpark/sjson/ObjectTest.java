package com.techatpark.sjson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonParser;
import com.techatpark.sjson.util.TestDataProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ObjectTest {

    final ObjectMapper objectMapper = new ObjectMapper();


    /**
     * Provides paths to JSON files for parameterized tests.
     *
     * @return Stream of paths to JSON files
     * @throws IOException if there is an issue listing files
     */
    private static List<Path> jsonFilePath() throws IOException {
        List<Path> jsonFilePath = new ArrayList<>();

        for (Path path : TestDataProvider.getJSONFiles()) {
            String jsonText = Files.readString(path).trim();
            if (jsonText.startsWith("{")) {
                jsonFilePath.add(path);
            }
        }

        return jsonFilePath;

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


    @Test
    @Disabled
    void testDuplicateKey() {
        final String json = """
                {
                    "key" : "value-1",
                    "key" : "value-2"
                }
                """;

        assertThrows(IllegalArgumentException.class,
                () -> Json.read(new StringReader(json)));
    }

}