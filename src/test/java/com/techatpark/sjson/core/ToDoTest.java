package com.techatpark.sjson.core;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

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

    private final Json parser = new Json();  // Replace with your actual class

    /**
     * ✅ Test case: Valid JSON - String
     */
    @Test
    void testValidJsonString() throws IOException {
        Object result = parser.read(new StringReader("\"hello\""));
        assertEquals("hello", result);
    }

    /**
     * ✅ Test case: Valid JSON - Number
     */
    @Test
    void testValidJsonNumber() throws IOException {
        Object result = parser.read(new StringReader("123"));
        assertEquals((byte) 123, result);
    }

    /**
     * ✅ Test case: Valid JSON - Floating Point Number
     */
    @Test
    void testValidJsonFloat() throws IOException {
        Object result = parser.read(new StringReader("123.45"));
        assertEquals(123.45, ((Number) result).doubleValue(), 0.001);
    }

    /**
     * ✅ Test case: Valid JSON - Boolean
     */
    @Test
    void testValidJsonBoolean() throws IOException {
        assertEquals(true, parser.read(new StringReader("true")));
        assertEquals(false, parser.read(new StringReader("false")));
    }

    /**
     * ✅ Test case: Valid JSON - Null
     */
    @Test
    void testValidJsonNull() throws IOException {
        assertNull(parser.read(new StringReader("null")));
    }

    /**
     * ✅ Test case: Valid JSON - Array
     */
    @Test
    void testValidJsonArray() throws IOException {
        Object result = parser.read(new StringReader("[1, 2, 3]"));
        assertTrue(result instanceof List);
        assertEquals(List.of((byte)1, (byte)2, (byte)3), result);
    }

    /**
     * ✅ Test case: Valid JSON - Object
     */
    @Test
    void testValidJsonObject() throws IOException {
        Object result = parser.read(new StringReader("{\"key\": \"value\"}"));
        assertTrue(result instanceof Map);
        assertEquals("value", ((Map<?, ?>) result).get("key"));
    }

    /**
     * ❌ Test case: Invalid JSON - Missing Quotes
     */
    @Test
    void testInvalidJsonMissingQuotes() throws IOException {
        assertThrows(IllegalArgumentException.class, () -> parser.read(new StringReader("{key: value}")));
    }

    /**
     * ❌ Test case: Invalid JSON - Unclosed Object
     */
    @Test
    void testInvalidJsonUnclosedObject() throws IOException {
        assertThrows(IllegalArgumentException.class, () -> parser.read(new StringReader("{\"key\": \"value\"")));
    }

    /**
     * ❌ Test case: Invalid JSON - Unclosed Array
     */
    @Test
    void testInvalidJsonUnclosedArray() throws IOException {
        assertThrows(IllegalArgumentException.class, () -> parser.read(new StringReader("[1, 2, 3")));
    }

    /**
     * ❌ Test case: Invalid JSON - Malformed Structure
     */
    @Test
    void testInvalidJsonMalformed() throws IOException {
        assertThrows(IllegalArgumentException.class, () -> parser.read(new StringReader("{\"key\": [1, 2, 3")));
    }

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

        assertThrows(IllegalArgumentException.class, () -> parser.read(new StringReader(largeJson.toString())));
    }
}
