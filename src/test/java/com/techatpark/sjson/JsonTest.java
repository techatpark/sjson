package com.techatpark.sjson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
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

        Duration jacksonsTime, jsonTime, gsonTime, oursTime;

        System.out.format("%40s%25s%25s%25s\n", "File Name", "Jackson","Org Json","Gson");
        System.out.format("%40s%25s%25s%25s\n", "----------", "----------", "----------", "----------");

        for (String jsonFile :
                getJSONFiles()) {

            // 1. Generate JSONNode directly from Jackson
            start = Instant.now();
            JsonNode jacksonJsonNode = jackson
                    .readTree(getJSONSample(jsonFile));
            jacksonsTime = Duration.between(start, Instant.now());

            // 2. Generate JSONNode directly from Org Json
            start = Instant.now();
            JSONObject jo = new JSONObject(getJSONSample(jsonFile));
            jsonTime = Duration.between(start, Instant.now());

            // 2. Generate JSONNode directly from Gson
            start = Instant.now();
            JsonObject gjo = JsonParser.parseReader(getJSONSample(jsonFile))
                    .getAsJsonObject();
            gsonTime = Duration.between(start, Instant.now());

            // 2. Generate JSONNode through our implementation
            start = Instant.now();
            Object ourJsonObject = json
                    .read(getJSONSample(jsonFile));
            oursTime = Duration.between(start, Instant.now());
            String reversedJsonText = jackson
                    .writeValueAsString(ourJsonObject);
            JsonNode ourJsonNode = jackson
                    .readTree(new StringReader(reversedJsonText));

            // 3. Compare Ours with JAckson
            Assertions.assertEquals(jacksonJsonNode,
                    ourJsonNode,
                    "Reverse JSON Failed for " + jsonFile);
            System.out.format("%40s%25s%25s%25s\n", jsonFile,
                     Math.round(((jacksonsTime.toNanos() - oursTime.toNanos()) * 100) / jacksonsTime.toNanos()),
                    Math.round(((jsonTime.toNanos() - oursTime.toNanos()) * 100) / jsonTime.toNanos()),
                    Math.round(((gsonTime.toNanos() - oursTime.toNanos()) * 100) / gsonTime.toNanos()));
        }

    }

    public Set<String> getJSONFiles() throws IOException {
        try (Stream<Path> stream = Files.list(Paths.get("src/test" +
                "/resources" +
                "/samples/"))) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toSet());
        }
    }

    private Reader getJSONSample(final String fileName) throws IOException {
        return new BufferedReader(new FileReader(Paths
                .get("src/test/resources/samples/" + fileName).toFile()));
    }
}