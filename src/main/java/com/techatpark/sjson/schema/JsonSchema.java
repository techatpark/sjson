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
public abstract class JsonSchema {

    /**
     * string type schema.
     */
    private final String schema;
    /**
     * string type title.
     */
    private final String title;
    /**
     * string type description.
     */
    private final String description;


    /**
     * Constructor for JsonSchema based on type.
     * @param reader the Reader to read JSON data from
     * @return JsonSchema representation of the read JSON
     * @throws IOException if an I/O error occurs
     */
    public static JsonSchema createJsonSchema(final Reader reader)
            throws IOException {
        Map<String, Object> schemaAsMap =
                (Map<String, Object>) new Json().read(reader);
        return switch (JsonType
                .valueOf(JsonType.class, schemaAsMap.get("type")
                        .toString().toUpperCase())) {
            case STRING -> new StringSchema(schemaAsMap);
            case INTEGER -> new IntegerSchema(schemaAsMap);
            case NUMBER -> new NumberSchema(schemaAsMap);
            case BOOLEAN -> new BooleanSchema(schemaAsMap);
            case OBJECT -> new ObjectSchema(schemaAsMap);
            case NULL -> new NullSchema(schemaAsMap);
            case ARRAY -> new ArraySchema(schemaAsMap);
        };

    }
    /**
     * Constructor for JsonSchema based on type.
     * @param schemaAsMap
     */
     JsonSchema(final Map<String, Object> schemaAsMap) {

        this.schema = schemaAsMap.get("$schema").toString();
        this.title = schemaAsMap.get("title").toString();
        this.description = schemaAsMap.get("description").toString();
    }

    /** Description of something. */
    @Override
    public String toString() {
        return "JsonSchema{"
                + "schema='" + schema + '\''
                + ", title='" + title + '\''
                + ", description='" + description + '\''
                + '}';
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
