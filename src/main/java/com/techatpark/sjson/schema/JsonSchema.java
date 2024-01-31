package com.techatpark.sjson.schema;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import javax.validation.ConstraintViolation;

/**
 * Represents a JSON schema document. Provides functionality to serialize
 * Java objects to JSON strings.
 */
public final class JsonSchema {

    /**
     * Constructor for JsonSchema.
     */
    public JsonSchema() {
        // Constructor logic if any
    }

    /**
     * Validate the given root Json, starting at the root of the data path.
     *
     * @param reader the root node
     * @return A list of ValidationMessage if there is any validation error,
     * or an empty list if there is no error.
     */
    public Set<ConstraintViolation> validate(final Reader reader) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Reads JSON from a Reader and converts it into a Java Object.
     * This method is not yet implemented.
     *
     * @param reader the Reader to read JSON data from
     * @return Object representation of the read JSON
     * @throws IOException if an I/O error occurs
     */
    public Object read(final Reader reader) throws IOException {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Converts a Map into its JSON string representation.
     *
     * @param jsonMap the Map representing the JSON object
     * @return the JSON string representation of the Map
     */
    public String jsonText(final Map<String, Object> jsonMap) {
        StringJoiner joiner = new StringJoiner(",", "{", "}");
        jsonMap.forEach((final String key, final Object value) ->
                joiner.add(formatEntry(key, value)));
        return joiner.toString();
    }

    /**
     * Formats a key-value pair into a JSON entry.
     *
     * @param key   the key in the map
     * @param value the value corresponding to the key
     * @return formatted JSON entry
     */
    private String formatEntry(final String key, final Object value) {
        return String.format("\"%s\":%s", escapeJson(key), valueToJson(value));
    }

    /**
     * Converts a value to its JSON representation.
     *
     * @param value the value to convert
     * @return JSON representation of the value
     */
    private String valueToJson(final Object value) {
        if (value == null) {
            return "null";
        } else if (value instanceof String) {
            return "\"" + escapeJson((String) value) + "\"";
        } else if (value instanceof Map) {
            return jsonText((Map<String, Object>) value);
        } else if (value instanceof List) {
            return listToJson((List<?>) value);
        } else {
            return value.toString();
        }
    }

    /**
     * Converts a list to its JSON array representation.
     *
     * @param list the list to convert
     * @return JSON array representation of the list
     */
    private String listToJson(final List<?> list) {
        StringJoiner sj = new StringJoiner(",", "[", "]");
        list.forEach(obj -> sj.add(valueToJson(obj)));
        return sj.toString();
    }

    /**
     * Escapes special characters in a JSON string.
     *
     * @param text the string to escape
     * @return escaped JSON string
     */
    private String escapeJson(final String text) {
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
