package com.techatpark.sjson;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for TrustedJsonParser.
 * This class tests various JSON parsing scenarios using the TrustedJsonParser.
 */
public class TrustedJsonParserTest {

    private TrustedJsonParser parser;

    @BeforeEach
    public void setUp() {
        // Sample JSON string used for testing
        String json = "{\"name\":\"John\", \"age\":30, \"isStudent\":true, \"grades\":[90, 85, 92]}";
        parser = new TrustedJsonParser(json);
    }

    /**
     * Test parsing of a JSON object.
     */
    @Test
    public void testParseObject() {
        Map<String, Object> result = (Map<String, Object>) parser.parse();

        assertEquals("John", result.get("name"));
        assertEquals(30.0, result.get("age"));
        assertEquals(true, result.get("isStudent"));
    }

    /**
     * Test parsing of a JSON array.
     */
    @Test
    public void testParseArray() {
        // Sample JSON string for testing an array
        String json = "[90, 85, 92]";
        parser = new TrustedJsonParser(json);

        List<Object> result = (List<Object>) parser.parse();

        assertEquals(90.0, result.get(0));
        assertEquals(85.0, result.get(1));
        assertEquals(92.0, result.get(2));
    }

    /**
     * Test parsing of a JSON boolean.
     */
    @Test
    public void testParseBoolean() {
        // Sample JSON string for testing boolean
        String json = "{\"isTrue\":true, \"isFalse\":false}";
        parser = new TrustedJsonParser(json);

        Map<String, Object> result = (Map<String, Object>) parser.parse();

        assertEquals(true, result.get("isTrue"));
        assertEquals(false, result.get("isFalse"));
    }

    /**
     * Test parsing of a JSON null value.
     */
    @Test
    public void testParseNull() {
        // Sample JSON string for testing null
        String json = "{\"value\":null}";
        parser = new TrustedJsonParser(json);

        Map<String, Object> result = (Map<String, Object>) parser.parse();

        assertNull(result.get("value"));
    }

    /**
     * Test parsing of a JSON number.
     */
    @Test
    public void testParseNumber() {
        // Sample JSON string for testing numbers
        String json = "{\"num1\":123.456, \"num2\":-42}";
        parser = new TrustedJsonParser(json);

        Map<String, Object> result = (Map<String, Object>) parser.parse();

        assertEquals(123.456, result.get("num1"));
        assertEquals(-42.0, result.get("num2"));
    }

    /**
     * Test parsing of a JSON string.
     */
    @Test
    public void testParseString() {
        // Sample JSON string for testing strings
        String json = "{\"name\":\"Alice\"}";
        parser = new TrustedJsonParser(json);

        Map<String, Object> result = (Map<String, Object>) parser.parse();

        assertEquals("Alice", result.get("name"));
    }

    /**
     * Test parsing of an empty JSON object.
     */
    @Test
    public void testParseEmptyObject() {
        // Sample JSON string for an empty object
        String json = "{}";
        parser = new TrustedJsonParser(json);

        Map<String, Object> result = (Map<String, Object>) parser.parse();

        assertTrue(result.isEmpty());
    }

    /**
     * Test parsing of an empty JSON array.
     */
    @Test
    public void testParseEmptyArray() {
        // Sample JSON string for an empty array
        String json = "[]";
        parser = new TrustedJsonParser(json);

        List<Object> result = (List<Object>) parser.parse();

        assertTrue(result.isEmpty());
    }

    /**
     * Test parsing of a complex JSON structure.
     */
    @Test
    public void testParseComplexJson() {
        // Sample JSON string for a complex structure
        String json = "{\"person\":{\"name\":\"John\", \"age\":30}, \"isMarried\":false}";
        parser = new TrustedJsonParser(json);

        Map<String, Object> result = (Map<String, Object>) parser.parse();
        Map<String, Object> person = (Map<String, Object>) result.get("person");

        assertEquals("John", person.get("name"));
        assertEquals(30.0, person.get("age"));
        assertEquals(false, result.get("isMarried"));
    }

    /**
     * Test parsing with invalid JSON format.
     */
    @Test
    public void testParseException() {
        // Sample JSON string with missing closing brace
        String json = "{\"name\":\"John\", \"age\":30";
        parser = new TrustedJsonParser(json);

        assertThrows(IllegalArgumentException.class, () -> parser.parse(),
                "Parsing incomplete JSON should throw IllegalArgumentException");
    }

    /**
     * Test parsing with unexpected end of JSON.
     */
    @Test
    public void testUnexpectedEndOfJson() {
        // Sample JSON string with unexpected end
        String json = "{\"name\":\"John\", \"age\":";
        parser = new TrustedJsonParser(json);

        assertThrows(IllegalArgumentException.class, () -> parser.parse(),
                "Parsing JSON with unexpected end should throw IllegalArgumentException");
    }

    /**
     * Test parsing with unexpected character in JSON.
     */
    @Test
    public void testUnexpectedCharacter() {
        // Sample JSON string with unexpected character 'y'
        String json = "{\"name\":\"John\", \"age\": 30 years}";
        parser = new TrustedJsonParser(json);

        assertThrows(IllegalArgumentException.class, () -> parser.parse(),
                "Parsing JSON with unexpected character should throw IllegalArgumentException");
    }
}
