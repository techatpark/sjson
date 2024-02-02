package com.techatpark.sjson.schema;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.Set;

import com.techatpark.sjson.Json;
import jakarta.validation.ConstraintViolation;

/**
 * Represents a JSON schema document. Provides functionality to serialize
 * Java objects to JSON strings.
 */
public final class JsonSchema {

    private final String schema;
    private final String title;
    private final String description;
    private final JsonType type;

    /**
     * Constructor for JsonSchema.
     */
    public JsonSchema(final Reader reader) throws IOException {
        Map<String, Object> schemaAsMap =
                (Map<String, Object>) new Json().read(reader);

        this.schema = schemaAsMap.get("$schema").toString();
        this.title = schemaAsMap.get("title").toString();
        this.description = schemaAsMap.get("description").toString();
        this.type = JsonType
                .valueOf(JsonType.class,schemaAsMap.get("type").toString().toUpperCase());
    }

    @Override
    public String toString() {
        return "JsonSchema{" +
                "schema='" + schema + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                '}';
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
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
