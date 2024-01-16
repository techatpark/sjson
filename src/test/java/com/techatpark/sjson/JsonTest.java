package com.techatpark.sjson;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.github.jamm.MemoryMeter;
import org.junit.jupiter.api.Assertions;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.JsonElement;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.time.Instant;

import java.util.Map;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.json.JSONObject;

class JsonTest {


    public final ObjectMapper jackson = new ObjectMapper();
    public final Json sJson = new Json();

    @ParameterizedTest
    @MethodSource("provideJsonFilePaths")
    void testReadPerformance(Path path) throws IOException {
        MemoryMeter meter = MemoryMeter.builder().build();

        Object ourJsonObject;
        JSONObject orgJSONObject;
        JsonNode jacksonJsonNode;
        JsonElement gsonObject;

        Instant start;

        long jacksonsTime, jsonTime, gsonTime, oursTime;
        long jacksonsSize, jsonSize, gsonSize, oursSize;

        // Rest of your test logic...
    }

    @ParameterizedTest
    @MethodSource("provideIllegalJsonFilePaths")
    void testIllegal(Path path) throws IOException {
        try {
            Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
                sJson.read(new FileReader(path.toFile()));
            });

            // Additional assertions or verifications if needed
        } catch (Exception e) {
            // Handle exceptions if needed
        }
    }

    @ParameterizedTest
    @MethodSource("provideJsonFilePaths")
    void testGetJsonText(Path path) throws IOException {
        Object sJsonObject = sJson.read(new BufferedReader(new FileReader(path.toFile())));

        if (sJsonObject instanceof Map) {
            Map<String, Object> sJsonAsMap = (Map<String, Object>) sJsonObject;

            JsonNode jsonNode = jackson.readTree(path.toFile());
            JsonNode ourJsonNode = jackson.readTree(sJson.jsonText(sJsonAsMap));

            Assertions.assertEquals(jsonNode, ourJsonNode, "Json Text is wrong for " + path);
        }
    }

    // Additional parameterized tests...

    static Stream<Path> provideJsonFilePaths() throws IOException {
        String baseFolder = System.getenv("SJSON_LOCAL_DIR") == null ? "src/test/resources/samples" :
                System.getenv("SJSON_LOCAL_DIR");
        try (Stream<Path> stream = Files.list(Paths.get(baseFolder))) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .collect(Collectors.toList())
                    .stream();
        }
    }

    static Stream<Path> provideIllegalJsonFilePaths() throws IOException {
        String illegalFolderPath = "src/test/resources/illegal";
        try (Stream<Path> stream = Files.list(Paths.get(illegalFolderPath))) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .collect(Collectors.toList())
                    .stream();
        }
    }


    // Remaining utility methods...
}
