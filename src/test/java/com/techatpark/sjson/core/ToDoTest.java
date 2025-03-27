package com.techatpark.sjson.core;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.StringReader;

/**
 * Unit tests for the JSONParser class.
 * <p>
 * This test suite verifies the behavior of the {@code read()} method,
 * ensuring that it correctly parses valid JSON and throws appropriate exceptions
 * for invalid JSON.
 * <p>
 * The tests cover:
 * <ul>
 *     <li>Valid JSON data types (String, Number, Boolean, Null, Arrays, Objects).</li>
 *     <li>Malformed JSON with syntax errors (expected to throw {@link IllegalArgumentException}).</li>
 *     <li>Edge cases like deeply nested JSON structures (potential stack overflow).</li>
 *     <li>Large JSON inputs to test memory constraints.</li>
 * </ul>
 */
class ToDoTest {

    private final Json parser = new Json();

    /**
     * ❌ Test case: Deeply Nested JSON Object (Stack Overflow Edge Case)
     */
    @Test
    void testDeeplyNestedJson() throws IOException {
        StringBuilder deepJson = new StringBuilder("{");
        for (int i = 0; i < 10_000; i++) {
            deepJson.append("\"key\": {");
        }
        deepJson.append("\"final\": \"value\"");
        for (int i = 0; i < 10_000; i++) {
            deepJson.append("}");
        }

        JSONObject jsonObject = new JSONObject(new StringReader(deepJson.toString()));

        assertThrows(IllegalArgumentException.class, () -> parser.read(new StringReader(deepJson.toString())));
    }

    /**
     * ❌ Test case: Deeply Nested Array (Stack Overflow Edge Case)
     */
    @Test
    void testDeeplyNestedArray() throws IOException {
        StringBuilder deepArray = new StringBuilder("[");
        for (int i = 0; i < 10_000; i++) {
            deepArray.append("[");
        }
        deepArray.append("\"value\"");
        for (int i = 0; i < 10_000; i++) {
            deepArray.append("]");
        }

        // JSONArray jsonArray = new JSONArray(new StringReader(deepArray.toString()));

        assertThrows(IllegalArgumentException.class, () -> parser.read(new StringReader(deepArray.toString())));
    }

    /**
     * ❌ Test case: Large JSON Input (Memory Constraint)
     */
    @Test
    void testLargeJson() throws IOException {
        StringBuilder largeJson = new StringBuilder("{ \"data\": [");
        for (int i = 0; i < 100_000; i++) {
            largeJson.append("{\"id\": ").append(i).append(", \"name\": \"Item").append(i).append("\"},");
        }
        largeJson.append("{\"id\": 100000, \"name\": \"LastItem\"}]}");

        JSONObject jsonObject = new JSONObject(new StringReader(largeJson.toString()));

        assertThrows(IllegalArgumentException.class, () -> parser.read(new StringReader(largeJson.toString())));
    }
}
