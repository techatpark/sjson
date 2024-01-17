package com.techatpark.sjson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
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
    public final Json sJson = new Json();



    /**
     * Test Plan.
     * 1. Get JSON Object from Local Folder and Iterate
     *      1. Get a JsonNode from Jackson
     *      2. Get a String from SJson and create our JSONNode using Jackson
     *      3. Compare Both JSON Nodes and verify they are equal.
     * @throws JsonProcessingException
     */
    @Test
    void testGetJsonText() throws IOException {

        Object sJsonObject ;

        for (Path path :
                getJSONFiles()) {
            // This is our Code
            sJsonObject = sJson
                    .read(new BufferedReader(new FileReader(path.toFile())));

            if(sJsonObject instanceof Map) {
                    Map<String,Object> sJsonAsMap = (Map<String, Object>) sJsonObject;

                    // 1. Get a JsonNode from Jackson
                    JsonNode jsonNode = jackson.readTree(path.toFile());

                    // 2. Get a String from SJson and create our JSONNode using Jackson
                    JsonNode ourJsonNode = jackson.readTree(sJson.jsonText(sJsonAsMap));

                    // 3. Compare Both JSON Nodes and verify they are equal.
                    Assertions.assertEquals(jsonNode,ourJsonNode,"Json Text is wrong for "
                            + path);
            }
        }
    }

    @Test
    void testNullJsonText() {

        // Kindly review this test case.
        String nullJson = "{\"null\":null}";

        Map<String,Object> sJsonAsMap = new HashMap<>();
        sJsonAsMap.put(null, null);

        String nullSJson = sJson.jsonText(sJsonAsMap);

        Assertions.assertEquals(nullJson, nullSJson);
    }
    /**
     * Utility to get Json Files from Test Resources directory.
     * @return Set of Paths
     * @throws IOException
     */
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