package com.techatpark.sjson.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.techatpark.sjson.core.Json;
import com.techatpark.sjson.core.util.TestUtil;
import org.github.jamm.MemoryMeter;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;


class PerformanceTest {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public final Json sJson = new Json();
    public final ObjectMapper jackson = new ObjectMapper();

    @ParameterizedTest
    @MethodSource("jsonFilesProvider")
    void testRead(Path path) throws IOException {
        MemoryMeter meter = MemoryMeter.builder().build();
        Object ourJsonObject;
        JSONObject orgJSONObject;
        JsonNode jacksonJsonNode;
        JsonElement gsonObject;

        long start;

        long jacksonsTime, jsonTime, gsonTime, oursTime;
        long jacksonsSize, jsonSize, gsonSize, oursSize;

        // Measure SJson
        start = System.nanoTime();
        ourJsonObject = sJson.read(new BufferedReader(new FileReader(path.toFile())));
        oursTime = System.nanoTime() - start;
        oursSize = meter.measureDeep(ourJsonObject);

        // Measure Org Json
        start = System.nanoTime();
        orgJSONObject = new JSONObject(new BufferedReader(new FileReader(path.toFile())));
        jsonTime = System.nanoTime() - start - oursTime;
        jsonSize = meter.measureDeep(orgJSONObject);

        // Measure Jackson
        start = System.nanoTime();
        jacksonJsonNode = jackson.readTree(new BufferedReader(new FileReader(path.toFile())));
        jacksonsTime = System.nanoTime() - start - oursTime;
        jacksonsSize = meter.measureDeep(jacksonJsonNode);

        // Measure Gson
        start = System.nanoTime();
        gsonObject = JsonParser.parseReader(new BufferedReader(new FileReader(path.toFile())));
        gsonTime = System.nanoTime() - start - oursTime;
        gsonSize = meter.measureDeep(gsonObject);

        // Check Correctness of the parser
        Assertions.assertEquals(gsonObject,
                JsonParser.parseReader(new StringReader(jackson.writeValueAsString(ourJsonObject))),
                "Reverse JSON Failed for " + path);

        System.out.format("%33s%20s%20s%20s%10s%20s%20s%20s\n",
                ANSI_RESET + path.getFileName(),
                getSizeDisplay(jsonSize, oursSize),
                getSizeDisplay(jacksonsSize, oursSize),
                getSizeDisplay(gsonSize, oursSize),
                ANSI_WHITE + "|",
                getTimeDisplay(jsonTime),
                getTimeDisplay(jacksonsTime),
                getTimeDisplay(gsonTime)
        );
    }

    /**
     * Provides paths to JSON files for parameterized tests.
     *
     * @return Stream of paths to JSON files
     * @throws IOException if there is an issue listing files
     */
    private static Set<Path> jsonFilesProvider() throws IOException {
        return TestUtil.getJSONFiles();
    }

    /**
     * Get Timing in a Color Coded Format.
     * @param time
     * @return time as text
     */
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

    /**
     * Get Size in a Color Coded Format.
     * @param size
     * @return size as text
     */
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
}
