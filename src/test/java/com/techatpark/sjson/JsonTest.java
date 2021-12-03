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
        long jacksonsSize, jsonSize, gsonSize, oursSize;

        System.out.format( "%60s%45s\n" , "Memory", "Speed");
        System.out.format( ANSI_WHITE +"%60s%45s\n"+ ANSI_RESET , "=========", "=========");

        System.out.format( "%30s%15s%15s%15s%4s%15s%15s%15s\n" , "File Name","Org Json", "Jackson","Gson","|","Org Json", "Jackson","Gson");
        System.out.format(ANSI_WHITE +"%30s%15s%15s%15s%4s%15s%15s%15s\n"+ ANSI_RESET, "----------", "----------", "----------"
                ,"----------", "|", "----------", "----------", "----------");

        for (Path path :
                getJSONFiles()) {

            // 1. SJson
            start = Instant.now();
            ourJsonObject = json
                    .read(new BufferedReader(new FileReader(path.toFile())));
            oursTime = Duration.between(start, Instant.now()).toNanos();
            oursSize = meter.measureDeep(ourJsonObject);


            // 2.  Org Json
            start = Instant.now();
            orgJSONObject = new JSONObject(new BufferedReader(new FileReader(path.toFile())));
            jsonTime = Duration.between(start, Instant.now()).toNanos();
            jsonTime = Math.round(((jsonTime - oursTime) * 100) / jsonTime);
            jsonSize = meter.measureDeep(orgJSONObject);

            // 3. Jackson
            start = Instant.now();
            jacksonJsonNode = jackson
                    .readTree(new BufferedReader(new FileReader(path.toFile())));
            jacksonsTime = Duration.between(start, Instant.now()).toNanos();
            jacksonsTime = Math.round(((jacksonsTime - oursTime) * 100) / jacksonsTime);
            jacksonsSize = meter.measureDeep(jacksonJsonNode);

            // 4. Gson
            start = Instant.now();
            gsonObject = JsonParser.parseReader(new BufferedReader(new FileReader(path.toFile())))
                    .getAsJsonObject();
            gsonTime = Duration.between(start, Instant.now()).toNanos();
            gsonTime = Math.round(((gsonTime - oursTime) * 100) / gsonTime);
            gsonSize = meter.measureDeep(gsonObject);

            // 3. SJson with Jackson
            String reversedJsonText = jackson
                    .writeValueAsString(ourJsonObject);

            Assertions.assertEquals(jacksonJsonNode,
                    jackson.readTree(new StringReader(reversedJsonText)),
                    "Reverse JSON Failed for " + path);
            System.out.format("%33s%20s%20s%20s%10s%20s%20s%20s\n",
                    ANSI_RESET + path.getFileName(),
                    getSizeDisplay(jsonSize,oursSize),
                    getSizeDisplay(jacksonsSize,oursSize),
                    getSizeDisplay(gsonSize,oursSize),
                    ANSI_WHITE +"|",
                    getTimeDisplay(jsonTime),
                    getTimeDisplay(jacksonsTime),
                    getTimeDisplay(gsonTime)
                    );
        }

    }

    private String getTimeDisplay(final long time) {
        StringBuilder builder = new StringBuilder();
        if(time < 0) {
            builder.append(ANSI_RED);
        }
        else {
            builder.append(ANSI_GREEN);
        }
        return builder.append(time).toString();
    }

    private String getSizeDisplay(final long size,final long ourSize) {
        StringBuilder builder = new StringBuilder();
        long gap = size-ourSize;
        if(gap < 0) {
            builder.append(ANSI_RED);
        }
        else {
            builder.append(ANSI_GREEN);
        }
        return builder.append(gap).toString();
    }

    @Test
    void testNumbers() throws IOException {
        Map<String,Object> numbersMap = (Map<String, Object>) new Json()
                .read(
                new BufferedReader(new FileReader(Paths.get("src/test/resources/samples/number-all-combinations.json").toFile()))
        );


        Assertions.assertEquals(Byte.class,numbersMap.get("3-digit-byte-number").getClass(),"Byte not accommodated in Byte");

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