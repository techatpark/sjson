package com.techatpark.sjson;

import com.techatpark.sjson.schema.JsonSchema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JsonSchemaTest {

    private JsonSchema jsonSchema;

    @BeforeEach
    public void setUp() {
        jsonSchema = new JsonSchema();
    }

    @Test
    public void testJsonTextWithSimpleMap() {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("key1", "value1");
        jsonMap.put("key2", 123);

        String expectedJson = "{\"key1\":\"value1\",\"key2\":123}";
        assertEquals(expectedJson, jsonSchema.jsonText(jsonMap));
    }

    @Test
    public void testJsonTextWithNestedMap() {
        Map<String, Object> nestedMap = new HashMap<>();
        nestedMap.put("nestedKey1", "nestedValue1");

        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("key1", "value1");
        jsonMap.put("key2", nestedMap);

        String expectedJson = "{\"key1\":\"value1\",\"key2\":{\"nestedKey1\":\"nestedValue1\"}}";
        assertEquals(expectedJson, jsonSchema.jsonText(jsonMap));
    }

    @Test
    public void testJsonTextWithList() {
        List<Object> list = Arrays.asList("item1", 123, true);

        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("key1", list);

        String expectedJson = "{\"key1\":[\"item1\",123,true]}";
        assertEquals(expectedJson, jsonSchema.jsonText(jsonMap));
    }

    @Test
    public void testJsonTextWithSpecialCharacters() {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("key1", "value \"1\"");
        jsonMap.put("key2", "line\nbreak");

        String expectedJson = "{\"key1\":\"value \\\"1\\\"\",\"key2\":\"line\\nbreak\"}";
        assertEquals(expectedJson, jsonSchema.jsonText(jsonMap));
    }

    @Test
    public void testReadMethodNotImplemented() {
        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            jsonSchema.read(null);
        });

        String expectedMessage = "Not yet implemented";
        assertEquals(expectedMessage, exception.getMessage());
    }
}
