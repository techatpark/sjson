package com.techatpark.sjson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.github.jamm.MemoryMeter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONObject;

class JsonTest {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    @Test
    void testParsing() throws IOException {

        final ObjectMapper jackson = new ObjectMapper();
        final Json json = new Json();

        MemoryMeter meter = MemoryMeter.builder().build();

        Object ourJsonObject;
        JSONObject orgJSONObject;
        JsonNode jacksonJsonNode;
        JsonObject gsonObject;

        Instant start;

        long jacksonsTime, jsonTime, gsonTime, oursTime;

        System.out.format( "%40s%25s%25s%25s\n" , "File Name","Org Json", "Jackson","Gson");
        System.out.format(ANSI_WHITE +"%40s%25s%25s%25s\n"+ ANSI_RESET, "----------", "----------", "----------", "----------");

        for (Path path :
                getJSONFiles()) {

            // 1. SJson
            start = Instant.now();
            ourJsonObject = json
                    .read(new BufferedReader(new FileReader(path.toFile())));
            oursTime = Duration.between(start, Instant.now()).toNanos();

            // 2.  Org Json
            start = Instant.now();
            orgJSONObject = new JSONObject(new BufferedReader(new FileReader(path.toFile())));
            jsonTime = Duration.between(start, Instant.now()).toNanos();
            jsonTime = Math.round(((jsonTime - oursTime) * 100) / jsonTime);

            // 3. Jackson
            start = Instant.now();
            jacksonJsonNode = jackson
                    .readTree(new BufferedReader(new FileReader(path.toFile())));
            jacksonsTime = Duration.between(start, Instant.now()).toNanos();
            jacksonsTime = Math.round(((jacksonsTime - oursTime) * 100) / jacksonsTime);

            // 4. Gson
            start = Instant.now();
            gsonObject = JsonParser.parseReader(new BufferedReader(new FileReader(path.toFile())))
                    .getAsJsonObject();
            gsonTime = Duration.between(start, Instant.now()).toNanos();
            gsonTime = Math.round(((gsonTime - oursTime) * 100) / gsonTime);

            // 3. SJson with Jackson
            String reversedJsonText = jackson
                    .writeValueAsString(ourJsonObject);

            Assertions.assertEquals(jacksonJsonNode,
                    jackson.readTree(new StringReader(reversedJsonText)),
                    "Reverse JSON Failed for " + path);
            System.out.format("%40s%25s%25s%25s\n",
                    path.getFileName(),
                    jsonTime,
                    jacksonsTime,
                    gsonTime);
        }

    }

    @Test
    void testNumbers() throws IOException {
        Map<String,Object> numbersMap = (Map<String, Object>) new Json()
                .read(
                new BufferedReader(new FileReader(Paths.get("src/test/resources/samples/number-all-combinations.json").toFile()))
        );
        Assertions.assertEquals(Byte.class,numbersMap.get("2-digit-number").getClass(),"Byte not accommodated in Byte");

        Assertions.assertEquals(Short.class,numbersMap.get("4-digit-number").getClass(),"Short not accommodated in Short");

        Assertions.assertEquals(Integer.class,numbersMap.get("9-digit-number").getClass(),"Integer not accommodated in Integer");

        Assertions.assertEquals(Long.class,numbersMap.get("18-digit-number").getClass(),"Long not accommodated in Long");

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