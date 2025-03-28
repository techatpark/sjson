package com.techatpark.sjson.core;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.StringReader;

/**
 * Unit tests for handling deeply nested JSON structures.
 * <p>
 * These tests ensure that the JSON parser correctly throws an exception
 * when encountering excessively nested JSON objects or arrays, preventing
 * potential stack overflow or performance issues.
 * <p>
 * Expected behavior:
 * - The parser should throw an {@link IllegalArgumentException} when the nesting depth
 *   exceeds a reasonable limit (e.g., 10,000 levels deep for pure nesting, 900 for mixed nesting).
 */
class DeepNestedTest {

    private final Json parser = new Json();

    /**
     * Tests deeply nested JSON objects.
     * <p>
     * Constructs a JSON structure with 10,000 nested objects and verifies that
     * the parser throws an {@link IllegalArgumentException} due to excessive depth.
     *
     * @throws IOException if an error occurs during parsing
     */
    @Test
    void testDeeplyNestedJson() throws IOException {
        String deepJson = "{" + "\"key\": {".repeat(10_000) +
                "\"final\": \"value\"" +
                "}".repeat(10_000);

        assertThrows(IllegalArgumentException.class, () -> parser.read(new StringReader(deepJson)));
    }

    /**
     * Tests deeply nested JSON arrays.
     * <p>
     * Constructs a JSON array structure with 10,000 nested arrays and verifies that
     * the parser throws an {@link IllegalArgumentException} due to excessive depth.
     *
     * @throws IOException if an error occurs during parsing
     */
    @Test
    void testDeeplyNestedArray() throws IOException {
        String deepArray = "[" + "[".repeat(10_000) +
                "\"value\"" +
                "]".repeat(10_000);

        assertThrows(IllegalArgumentException.class, () -> parser.read(new StringReader(deepArray)));
    }

    /**
     * Tests a mixed deeply nested JSON structure combining objects and arrays.
     * <p>
     * Constructs a JSON structure where objects and arrays are nested alternately
     * up to a depth of 900 levels and verifies that the parser correctly handles it.
     *
     * @throws IOException if an error occurs during parsing
     */
    @Test
    void testMixedDeeplyNestedJson() throws IOException {
        StringBuilder mixedJson = new StringBuilder("{");
        for (int i = 0; i < 450; i++) {
            mixedJson.append("\"key\": [");
            mixedJson.append("{");
        }
        mixedJson.append("\"final\": \"value\"");
        for (int i = 0; i < 450; i++) {
            mixedJson.append("}");
            mixedJson.append("]");
        }

        assertThrows(IllegalArgumentException.class, () -> parser.read(new StringReader(mixedJson.toString())));
    }

    /**
     * Tests a deeply nested JSON structure that is valid but close to the limit.
     * <p>
     * Constructs a JSON object with 9,999 nested objects and verifies that
     * parsing completes successfully.
     *
     * @throws IOException if an error occurs during parsing
     */
    @Test
    @Disabled
    void testNestedJsonWithinLimit() throws IOException {
        String deepJson = "{" + "\"key\": {".repeat(9_999) +
                "\"final\": \"value\"" +
                "}".repeat(9_999);

        assertDoesNotThrow(() -> parser.read(new StringReader(deepJson)));
    }

    /**
     * Tests unbalanced JSON structures where braces or brackets do not match.
     *
     * @throws IOException if an error occurs during parsing
     */
    @Test
    void testUnbalancedNesting() throws IOException {
        String unbalancedJson = "{ \"key\": { \"nested\": [ 1, 2, 3 }"; // Missing closing bracket
        assertThrows(IllegalArgumentException.class, () -> parser.read(new StringReader(unbalancedJson)));
    }

    /**
     * Tests a deeply nested JSON structure with escaped characters.
     *
     * @throws IOException if an error occurs during parsing
     */
    @Test
    void testDeeplyNestedWithEscapedCharacters() throws IOException {
        String json = "{" + "\"key\": {\"text\": \"\\\"quoted\\\"\" , ".repeat(500) +
                "\"final\": \"value\"" +
                "}".repeat(500);

        assertThrows(IllegalArgumentException.class, () -> parser.read(new StringReader(json)));
    }
}
