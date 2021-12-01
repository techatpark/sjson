package com.techatpark.sjson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONObject;

class JsonTest {

    @Test
    void testParsing() throws IOException {

        final ObjectMapper jackson = new ObjectMapper();
        final Json json = new Json();

        Instant start;

        long jacksonsTime, jsonTime, gsonTime, oursTime;

        System.out.format("%40s%25s%25s%25s\n", "File Name","Org Json", "Jackson","Gson");
        System.out.format("%40s%25s%25s%25s\n", "----------", "----------", "----------", "----------");

        for (Path path :
                getJSONFiles()) {

            // 1. SJson
            start = Instant.now();
            Object ourJsonObject = json
                    .read(new BufferedReader(new FileReader(path.toFile())));
            oursTime = Duration.between(start, Instant.now()).toNanos();

            // 2.  Org Json
            start = Instant.now();
            new JSONObject(new BufferedReader(new FileReader(path.toFile())));
            jsonTime = Duration.between(start, Instant.now()).toNanos();

            // 3. Jackson
            start = Instant.now();
            JsonNode jacksonJsonNode = jackson
                    .readTree(new BufferedReader(new FileReader(path.toFile())));
            jacksonsTime = Duration.between(start, Instant.now()).toNanos();

            // 4. Gson
            start = Instant.now();
            JsonParser.parseReader(new BufferedReader(new FileReader(path.toFile())))
                    .getAsJsonObject();
            gsonTime = Duration.between(start, Instant.now()).toNanos();

            // 3. SJson with Jackson
            String reversedJsonText = jackson
                    .writeValueAsString(ourJsonObject);
            JsonNode ourJsonNode = jackson
                    .readTree(new StringReader(reversedJsonText));
            Assertions.assertEquals(jacksonJsonNode,
                    ourJsonNode,
                    "Reverse JSON Failed for " + path);
            System.out.format("%40s%25s%25s%25s\n", path.getFileName(),
                    Math.round(((jsonTime - oursTime) * 100) / jsonTime),
                     Math.round(((jacksonsTime - oursTime) * 100) / jacksonsTime),
                    Math.round(((gsonTime - oursTime) * 100) / gsonTime));
        }

    }

    private Set<Path> getJSONFiles() throws IOException {

        String baseFolder = System.getenv("SJSON_LOCAL_DIR") == null ? "src/test/resources/samples" :
                System.getenv("SJSON_LOCAL_DIR");
        try (Stream<Path> stream = Files.list(Paths.get(baseFolder))) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .collect(Collectors.toSet());
        }
    }

}