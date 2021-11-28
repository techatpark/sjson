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

        System.out.format("%40s%25s%25s%25s\n", "File Name", "Jackson","Org Json","Gson");
        System.out.format("%40s%25s%25s%25s\n", "----------", "----------", "----------", "----------");

        for (String jsonFile :
                getJSONFiles()) {

            // 2. SJson
            start = Instant.now();
            Object ourJsonObject = json
                    .read(getJSONSample(jsonFile));
            oursTime = Duration.between(start, Instant.now()).toNanos();

            // 1. Jackson
            start = Instant.now();
            JsonNode jacksonJsonNode = jackson
                    .readTree(getJSONSample(jsonFile));
            jacksonsTime = Duration.between(start, Instant.now()).toNanos();

            // 2.  Org Json
            start = Instant.now();
            JSONObject jo = new JSONObject(getJSONSample(jsonFile));
            jsonTime = Duration.between(start, Instant.now()).toNanos();

            // 2. Gson
            start = Instant.now();
            JsonParser.parseReader(getJSONSample(jsonFile))
                    .getAsJsonObject();
            gsonTime = Duration.between(start, Instant.now()).toNanos();

            // 3. SJson with Jackson
            String reversedJsonText = jackson
                    .writeValueAsString(ourJsonObject);
            JsonNode ourJsonNode = jackson
                    .readTree(new StringReader(reversedJsonText));
            Assertions.assertEquals(jacksonJsonNode,
                    ourJsonNode,
                    "Reverse JSON Failed for " + jsonFile);
            System.out.format("%40s%25s%25s%25s\n", jsonFile,
                     Math.round(((jacksonsTime - oursTime) * 100) / jacksonsTime),
                    Math.round(((jsonTime - oursTime) * 100) / jsonTime),
                    Math.round(((gsonTime - oursTime) * 100) / gsonTime));
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