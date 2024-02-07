package com.techatpark.sjson.schema;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.techatpark.sjson.core.Json;
import jakarta.validation.ConstraintViolation;

/**
 * Represents a JSON schema document. Provides functionality to serialize
 * Java objects to JSON strings.
 * @param <T> of type T.
 */
public abstract class JsonSchema<T> {

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
    public static JsonSchema getJsonSchema(final Reader reader)
            throws IOException {
        Map<String, Object> schemaAsMap =
                (Map<String, Object>) new Json().read(reader);
        return getJsonSchema(JsonType
                .valueOf(JsonType.class, schemaAsMap.get("type")
                        .toString().toUpperCase()), schemaAsMap);

    }

    /**
     * Generate a JsonNode containing the JSON Schema representation
     * of the given type.
     *
     * @param mainTargetType – type for which to generate the JSON Schema
     * @param typeParameters – optional type parameters
     *                       (in case of the mainTargetType being
     *                       a parameterised type)
     * @return generated JSON Schema
     */
    public static JsonSchema getJsonSchema(final Type mainTargetType,
                                           final Type... typeParameters) {
        return getJsonSchema(getJsonType(mainTargetType), null);

    }

    /**
     * Determines the JSON type for the given field type.
     *
     * @param fieldType The Type of the field.
     * @return The JSON type corresponding to the field type.
     */
    private static JsonType getJsonType(final Type fieldType) {
        if (fieldType == int.class || fieldType == long.class
                || fieldType == Integer.class || fieldType == Long.class) {
            return JsonType.INTEGER;
        } else if (fieldType == String.class) {
            return JsonType.STRING;
        } else if (fieldType == double.class || fieldType == float.class
                || fieldType == Double.class || fieldType == Float.class
                || fieldType == BigDecimal.class) {
            return JsonType.NUMBER;
        } else if (fieldType instanceof Collection<?>) {
            return JsonType.ARRAY;
        } else if (fieldType == boolean.class || fieldType == Boolean.class) {
            return JsonType.BOOLEAN;
        } else {
            return JsonType.NULL;
        }
    }

    private static JsonSchema getJsonSchema(final JsonType jsonType,
                            final Map<String, Object> schemaAsMap) {
        return switch (jsonType) {
            case STRING -> new StringSchema(schemaAsMap);
            case INTEGER, NUMBER -> new NumberSchema(schemaAsMap);
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
        if (schemaAsMap != null) {
            this.schema = schemaAsMap.get("$schema").toString();
            this.title = schemaAsMap.get("title").toString();
            this.description = schemaAsMap.get("description").toString();
        } else {
            this.schema = "https://json-schema.org/draft/2020-12/schema";
            this.title = null;
            this.description = null;
        }
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
    public abstract T read(Reader reader) throws IOException;

    /**
     * Converts a Map into its JSON string representation.
     *
     * @param jsonMap the Map representing the JSON object
     * @return the JSON string representation of the Map
     */
    public String jsonText(final Map<String, Object> jsonMap) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    /**
     * gets Schema.
     * @return schema
     */
    public String getSchema() {
        return schema;
    }

    /**
     * Gets Title.
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets Description.
     * @return description
     */
    public String getDescription() {
        return description;
    }
}
