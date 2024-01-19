package com.techatpark.sjson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parses well-formed JSON from trusted sources into Java objects.
 * This parser assumes the input JSON string is correctly
 * formatted and does not contain malicious content.
 * It is not suitable for parsing JSON from untrusted sources
 * due to the lack of extensive error handling.
 */
public class TrustedJsonParser {
    /**
     * The length of the "true" boolean literal in JSON (4 characters).
     */
    private static final int TRUE_LENGTH = 4;

    /**
     * The length of the "false" boolean literal in JSON (5 characters).
     */
    private static final int FALSE_LENGTH = 5;

    /**
     * The length of the "null" literal in JSON (4 characters).
     */
    private static final int NULL_LENGTH = 4;

    /**
     * The current index used for parsing the JSON string.
     */
    private int index = 0;

    /**
     * The JSON string to be parsed.
     */
    private final String json;

    /**
     * Constructs a TrustedJsonParser with the provided JSON string.
     *
     * @param jsonString the JSON string to parse
     */
    public TrustedJsonParser(final String jsonString) {
        this.json = jsonString.trim();
    }

    /**
     * Parses the JSON string into a Java object.
     *
     * @return the parsed Java object
     * @throws IllegalArgumentException if the JSON format is incorrect
     */
    public Object parse() {
        return parseValue();
    }

    private Object parseValue() {
        ensureNotEndOfJson();
        char currentChar = currentChar();

        switch (currentChar) {
            case '{':
                return parseObject();
            case '[':
                return parseArray();
            case '\"':
                return parseString();
            case 't':
            case 'f':
                return parseBoolean();
            case 'n':
                parseNull();
                return null;
            default:
                if (Character.isDigit(currentChar) || currentChar == '-') {
                    return parseNumber();
                }
                throw new IllegalArgumentException(
                        "Unexpected character: " + currentChar
                );
        }
    }

    private Map<String, Object> parseObject() {
        Map<String, Object> object = new HashMap<>();
        index++; // skip '{'

        while (currentChar() != '}') {
            ensureNotEndOfJson();
            skipWhitespace();
            String key = parseString();
            ensureNotEndOfJson();
            skipWhitespace();
            if (currentChar() != ':') {
                throw new IllegalArgumentException(
                        "Expected ':' after key in object"
                );
            }
            index++; // skip ':'
            Object value = parseValue();
            object.put(key, value);
            skipWhitespace();
            if (currentChar() == ',') {
                index++; // skip ','
            }
        }
        index++; // skip '}'
        return object;
    }

    private List<Object> parseArray() {
        List<Object> array = new ArrayList<>();
        index++; // skip '['

        while (currentChar() != ']') {
            ensureNotEndOfJson();
            skipWhitespace();
            array.add(parseValue());
            skipWhitespace();
            if (currentChar() == ',') {
                index++; // skip ','
            }
        }
        index++; // skip ']'
        return array;
    }

    private String parseString() {
        index++; // skip initial '\"'
        StringBuilder sb = new StringBuilder();
        while (currentChar() != '\"') {
            sb.append(currentChar());
            index++;
        }
        index++; // skip closing '\"'
        return sb.toString();
    }

    private boolean parseBoolean() {
        if (json.startsWith("true", index)) {
            index += TRUE_LENGTH;
            return true;
        } else if (json.startsWith("false", index)) {
            index += FALSE_LENGTH;
            return false;
        }
        throw new IllegalArgumentException(
                "Unexpected token for boolean"
        );
    }

    private void parseNull() {
        if (!json.startsWith("null", index)) {
            throw new IllegalArgumentException(
                    "Unexpected token for null"
            );
        }
        index += NULL_LENGTH;
    }

    private Number parseNumber() {
        int start = index;
        while (index < json.length()
                && (Character.isDigit(currentChar())
                || currentChar() == '.'
                || currentChar() == '-')) {
            index++;
        }
        return Double.parseDouble(json.substring(start, index));
    }

    private void skipWhitespace() {
        while (index < json.length() && Character.isWhitespace(currentChar())) {
            index++;
        }
    }

    private void ensureNotEndOfJson() {
        if (index >= json.length()) {
            throw new IllegalArgumentException("Unexpected end of JSON");
        }
    }

    private char currentChar() {
        ensureNotEndOfJson();
        return json.charAt(index);
    }
}
