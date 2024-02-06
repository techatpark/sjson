package com.techatpark.sjson.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techatpark.sjson.core.util.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class JsonTest {

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
    @ParameterizedTest
    @MethodSource("jsonFilesProvider")
    void testGetJsonText(Path path) throws IOException {

        Object sJsonObject ;


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
     * Provides paths to JSON files for parameterized tests.
     *
     * @return Stream of paths to JSON files
     * @throws IOException if there is an issue listing files
     */
    private static Set<Path> jsonFilesProvider() throws IOException {
        return TestUtil.getJSONFiles();
    }

    @Test
    void testJSon() throws IOException {
        String jsonText = """
                {
                  
                  "negZeroKey": -0.0
                 
                
                }
                """;
        Map<String,Object> ourJsonObject = (Map<String, Object>) new Json().read(new StringReader(jsonText));
        JsonNode jacksonJsonNode = new ObjectMapper().readTree(jsonText);
        String reversedJsonText = jackson.writeValueAsString(ourJsonObject);
        JSONAssert.assertEquals(
                jsonText, reversedJsonText, JSONCompareMode.LENIENT);

    }

}