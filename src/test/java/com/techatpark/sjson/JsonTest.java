package com.techatpark.sjson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.github.jamm.MemoryMeter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONObject;

class JsonTest {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public final ObjectMapper jackson = new ObjectMapper();
    public final Json json = new Json();

    @Test
    void testParsing() throws IOException {

        MemoryMeter meter = MemoryMeter.builder().build();

        Object ourJsonObject;
        JSONObject orgJSONObject;
        JsonNode jacksonJsonNode;
        JsonObject gsonObject;

        Instant start;

        long jacksonsTime, jsonTime, gsonTime, oursTime;
        long jacksonsSize, jsonSize, gsonSize, oursSize;

        System.out.format( "%60s%45s\n" , "Memory", "Performance");
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



    /**
     * Tests whether the numbers are accomadated in proper buckets.
     *
     * We will use BigInteger and BigDecimals to place the values.
     * But when we get the JSON Object we should see
     * 1. Byte,Short,Integer,Long or BigInteger for Numbers
     * 2. Float, Double or BigDecimal for Decimal Numbers
     *
     * @throws IOException
     */
    @Test
    void testNumberBuckets() throws IOException {
        Map<String,Object> numbersMap = new HashMap<>();

        // Byte
        numbersMap.put("a-Byte",new BigInteger("1"));
        numbersMap.put("a-min-Byte",Byte.MIN_VALUE);
        numbersMap.put("a-max-Byte",Byte.MAX_VALUE);

        // Short
        numbersMap.put("a-Short",new BigInteger("1234"));
        numbersMap.put("a-min-Short",Short.MIN_VALUE);
        numbersMap.put("a-max-Short",Short.MAX_VALUE);

        // Integer
        numbersMap.put("a-Integer",Integer.valueOf(567896));
        numbersMap.put("a-min-Integer",Integer.MIN_VALUE);
        numbersMap.put("a-max-Integer",Integer.MAX_VALUE);

        // Long
        numbersMap.put("a-Long",Long.valueOf(568957854));
        numbersMap.put("a-min-Long",Long.MIN_VALUE);
        numbersMap.put("a-max-Long",Long.MAX_VALUE);

        final Map<String,Object> map = (Map<String, Object>) json.read(new StringReader(jackson.writeValueAsString(numbersMap)));

        // Check Bytes
        Assertions.assertAll("Byte Should be accommodated by Byte",
                () -> Assertions.assertEquals(Byte.class, map.get("a-Byte").getClass()),
                () -> Assertions.assertEquals(Byte.class, map.get("a-min-Byte").getClass()),
                () -> Assertions.assertEquals(Byte.class, map.get("a-max-Byte").getClass())
        );

        // Check Short
        Assertions.assertAll("Short Should be accommodated by Short",
                () -> Assertions.assertEquals(Short.class, map.get("a-Short").getClass()),
                () -> Assertions.assertEquals(Short.class, map.get("a-min-Short").getClass()),
                () -> Assertions.assertEquals(Short.class, map.get("a-max-Short").getClass())
        );

        // Check Integer
        Assertions.assertAll("Integer Should be accommodated by Integer",
                () -> Assertions.assertEquals(Integer.class, map.get("a-Integer").getClass()),
                () -> Assertions.assertEquals(Integer.class, map.get("a-min-Integer").getClass()),
                () -> Assertions.assertEquals(Integer.class, map.get("a-max-Integer").getClass())
        );
    }

    /**
     * Test Plan.
     * 1. Create a JSON Map ( Map of String and Objects )
     * 2. Get a JsonNode from Jackson
     * 3. Get a String from JSON and create our JSONNode using Jackson
     * 4. Compare Both JSON Nodes and verify they are equal.
     *
     * @throws JsonProcessingException
     */
    @Test
    void testGetJsonText() throws JsonProcessingException {
        // 1. Create a JSON Map ( Map of String and Objects )
        Map<String,Object> jsonMap = new HashMap<>();

        jsonMap.put("a-String","Hello");
        jsonMap.put("a-Number",12);
        jsonMap.put("a-long",12L);
        jsonMap.put("a-Decimal-Number",12.3);
        jsonMap.put("a-boolean",true);
        jsonMap.put("a-null",null);
        jsonMap.put("a-empty-map",new HashMap<>());

        Map<String,Object> innerMap = new HashMap<>();
        innerMap.put("a-String","Inner Hello");
        innerMap.put("a-Number",12);
        innerMap.put("a-long",12L);
        innerMap.put("a-Decimal-Number",12.3);
        innerMap.put("a-boolean",true);
        innerMap.put("a-null",null);


        Map<String,Object> innerInnerMap = new HashMap<>();
        innerInnerMap.put("a-String","Inner Inner Hello");
        innerInnerMap.put("a-Number",13);
        innerInnerMap.put("a-long",13L);
        innerInnerMap.put("a-Decimal-Number",13.4);
        innerInnerMap.put("a-boolean",true);
        innerInnerMap.put("a-null",null);
        innerInnerMap.put("a-empty-map",new HashMap<>());

        innerMap.put("a-inner-inner-map",innerInnerMap);
        jsonMap.put("a-inner-map",innerMap);

        // 2. Get a JsonNode from Jackson
        JsonNode jsonNode = jackson.valueToTree(jsonMap);

        // 3. Get a String from JSON and create our JSONNode using Jackson
        String ourJsonText  = json.jsonText(jsonMap);
        JsonNode ourJsonNode = jackson.readTree(ourJsonText);

        // 4. Compare Both JSON Nodes and verify they are equal.
        Assertions.assertEquals(jsonNode.toString(),ourJsonNode.toString(),"Json Text is wrong");
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
}