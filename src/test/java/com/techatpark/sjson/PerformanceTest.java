package com.techatpark.sjson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.techatpark.sjson.util.TestDataProvider;
import org.github.jamm.MemoryMeter;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

/**
 * Reads Json Objects as Map<String,Object> using various parsers,
 * Compare them with Json.parse() by Memory usage and performance
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PerformanceTest {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_WHITE = "\u001B[37m";

    private final ObjectMapper jackson = new ObjectMapper();
    private final Gson gson = new Gson();

    private final  MemoryMeter meter = MemoryMeter.builder().build();

    long totalOursTime = 0;
    long totalOrgJsonTime = 0;
    long totalJacksonTime = 0;
    long totalGsonTime = 0;

    long totalOursSize = 0;
    long totalOrgJsonSize = 0;
    long totalJacksonSize = 0;
    long totalGsonSize = 0;

    int fileCount = 0;
    

    @ParameterizedTest
    @MethodSource("jsonFilesProvider")
    void testRead(Path path) throws IOException {
        fileCount++;

        // Org JSON
        long start = System.nanoTime();
        JSONTokener jsonTokener = new JSONTokener(new BufferedReader(new FileReader(path.toFile())));
        Map orgJsonObject = new JSONObject(jsonTokener).toMap();
        long orgjsonTime = System.nanoTime() - start;
        long orgjsonSize = meter.measureDeep(orgJsonObject);

        // Our Json
        start = System.nanoTime();
        Object ourJsonObject = Json.read(new BufferedReader(new FileReader(path.toFile())));
        long oursTime = System.nanoTime() - start;
        long oursSize = meter.measureDeep(ourJsonObject);

        // Jackson
        start = System.nanoTime();
        Map jacksonJson = jackson.readValue(new BufferedReader(new FileReader(path.toFile())), Map.class);
        long jacksonsTime = System.nanoTime() - start;
        long jacksonsSize = meter.measureDeep(jacksonJson);

        // Gson
        start = System.nanoTime();
        Map gsonJson = gson.fromJson(new BufferedReader(new FileReader(path.toFile())), Map.class);
        long gsonTime = System.nanoTime() - start;
        long gsonSize = meter.measureDeep(gsonJson);

        // Accumulate totals
        totalOursTime += oursTime;
        totalOrgJsonTime += orgjsonTime;
        totalJacksonTime += jacksonsTime;
        totalGsonTime += gsonTime;

        totalOursSize += oursSize;
        totalOrgJsonSize += orgjsonSize;
        totalJacksonSize += jacksonsSize;
        totalGsonSize += gsonSize;

        // Check Correctness of the parser
        Assertions.assertEquals(JsonParser.parseReader(new StringReader(jackson.writeValueAsString(gsonJson))),
                JsonParser.parseReader(new StringReader(jackson.writeValueAsString(ourJsonObject))),
                "Reverse JSON Failed for " + path);

        // Print formatted table
        System.out.format("%33s%20s%20s%20s%10s%20s%20s%20s\n",
                ANSI_RESET + path.getFileName(),
                getMemoryDisplay(orgjsonSize, oursSize),
                getMemoryDisplay(jacksonsSize, oursSize),
                getMemoryDisplay(gsonSize, oursSize),
                ANSI_WHITE + "|",
                getTimeDisplay(orgjsonTime, oursTime),
                getTimeDisplay(jacksonsTime, oursTime),
                getTimeDisplay(gsonTime, oursTime)
        );
    }

    @AfterAll
    void printAverages() {
        System.out.println(ANSI_RESET + "\nAverage Memory and Time Usage (nanoseconds and bytes):");
        System.out.format("%33s%20s%20s%20s%10s%20s%20s%20s\n",
                ANSI_RESET + "Average",
                getMemoryDisplay(totalOrgJsonSize / fileCount, totalOursSize / fileCount),
                getMemoryDisplay(totalJacksonSize / fileCount, totalOursSize / fileCount),
                getMemoryDisplay(totalGsonSize / fileCount, totalOursSize / fileCount),
                ANSI_WHITE + "|",
                getTimeDisplay(totalOrgJsonTime / fileCount, totalOursTime / fileCount),
                getTimeDisplay(totalJacksonTime / fileCount, totalOursTime / fileCount),
                getTimeDisplay(totalGsonTime / fileCount, totalOursTime / fileCount)
        );
    }

    private static Set<Path> jsonFilesProvider() throws IOException {
        return TestDataProvider.getJSONObjectFiles();
    }

    private String getTimeDisplay(final long time, final long oursTime) {
        StringBuilder builder = new StringBuilder();
        long gap = time - oursTime;
        if (gap > 0) {
            builder.append(ANSI_GREEN);
        } else {
            builder.append(ANSI_RED);
        }
        return builder.append(time).toString();
    }

    private String getMemoryDisplay(final long size, final long ourSize) {
        StringBuilder builder = new StringBuilder();
        if (size > ourSize) {
            builder.append(ANSI_GREEN);
        } else {
            builder.append(ANSI_RED);
        }
        return builder.append(size).toString();
    }
}
