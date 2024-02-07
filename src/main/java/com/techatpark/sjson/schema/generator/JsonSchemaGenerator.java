/**
 * Provides Json Generator
 */
package com.techatpark.sjson.schema.generator;

import com.techatpark.sjson.schema.JsonType;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * JsonSchemaGenerator is a utility class for
 * generating JSON schemas from Java classes.
 * It provides a method {@code create} that takes a Class object
 * and returns a JSON schema
 * representing the structure of the class.
 */
public final class JsonSchemaGenerator {

    /**
     * Generates a JSON schema for the given Class.
     *
     * @param aClass The Class for which the JSON schema is to be generated.
     * @return A JSON schema representing the structure of the given Class.
     */
    public String create(final Class<?> aClass) {
        StringBuilder schemaBuilder =
                new StringBuilder("{\"type\":\"object\",\"properties\":{");

        Field[] fields = aClass.getDeclaredFields();
        boolean isFirstField = true;

        for (Field field : fields) {
            if (!isFirstField) {
                schemaBuilder.append(",");
            } else {
                isFirstField = false;
            }

            String fieldName = field.getName();
            Type fieldType = field.getGenericType();

            schemaBuilder.append("\"").append(fieldName).append("\":");

            if (fieldType instanceof ParameterizedType) {
                schemaBuilder.
                        append(generateParameterizedTypeSchema(fieldType));
            } else if (field.getType().isArray()) {
                schemaBuilder.append(getJsonTypeForArrays(fieldType));
            } else {
                schemaBuilder.append("{\"type\":\"")
                        .append(getJsonType(fieldType).getType()).append("\"}");
            }
        }

        schemaBuilder.append("}");
        schemaBuilder
                .append(",\"$schema\":\""
                        + "https://json-schema.org/draft/2020-12/schema")
                .append("\"}");
        return schemaBuilder.toString();
    }

    /**
     * Generates a JSON schema for the given parameterized type.
     *
     * @param fieldType The parameterized type for which
     *                  the JSON schema is to be generated.
     * @return A JSON schema representing the structure of
     * the given parameterized type.
     */
    private String generateParameterizedTypeSchema(final Type fieldType) {
        if (fieldType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) fieldType;
            Type[] typeArguments = parameterizedType.getActualTypeArguments();

            if (typeArguments.length == 1
                    && (typeArguments[0] == String.class
                    || typeArguments[0] == Character.class)) {
                return "{\"type\":\"array\",\"items\":{\"type\":\"string\"}}";
            } else if (typeArguments.length == 1
                    && (typeArguments[0] == Integer.class
                    || typeArguments[0] == Long.class
                    || typeArguments[0] == BigInteger.class)) {
                return "{\"type\":\"array\",\"items\":{\"type\":\"integer\"}}";
            } else if (typeArguments.length == 1
                    && (typeArguments[0] == Double.class
                    || typeArguments[0] == Float.class)) {
                return "{\"type\":\"array\",\"items\":{\"type\":\"number\"}}";
            } else if (typeArguments.length == 1
                    && typeArguments[0] == Boolean.class) {
                return "{\"type\":\"array\",\"items\":{\"type\":\"boolean\"}}";
            } else if (typeArguments.length == 1
                    && typeArguments[0] == BigDecimal.class) {
                return "{\"type\":\"array\",\"items\":{\"type\":\"number\"}}";
            } else if (typeArguments.length == 2) {
                return "{\"type\":\"object\"}}";
            } else {
                // Handle other parameterized types as needed...
                return "{\"type\":\"array\",\"items\":{\"type\":\"unknown\"}}";
            }
        } else {
            return "{\"type\":\"unknown\"}";
        }
    }

    /**
     * Determines the JSON type for the given field type.
     *
     * @param fieldType The Type of the field.
     * @return The JSON type corresponding to the field type.
     */
    private JsonType getJsonType(final Type fieldType) {
        if (fieldType == int.class || fieldType == long.class
                || fieldType == Integer.class || fieldType == Long.class) {
            return JsonType.INTEGER;
        } else if (fieldType == String.class) {
            return JsonType.STRING;
        } else if (fieldType == double.class || fieldType == float.class
                || fieldType == Double.class || fieldType == Float.class) {
            return JsonType.NUMBER;
        } else if (fieldType == List.class) {
            return JsonType.ARRAY;
        } else if (fieldType == BigDecimal.class) {
            return JsonType.NUMBER;
        } else if (fieldType == boolean.class || fieldType == Boolean.class) {
            return JsonType.BOOLEAN;
        } else {
            return JsonType.NULL;
        }
    }
    /**
     * Determines the JSON type for primitive array the given field type.
     *
     * @param fieldType The Type of the field.
     * @return The JSON type corresponding to the field type.
     */
    private String getJsonTypeForArrays(final Type fieldType) {
        if (fieldType == int[].class || fieldType == Integer[].class
                || fieldType == long[].class || fieldType == Long[].class
                || fieldType == BigInteger[].class) {
            return "{\"type\":\"array\",\"items\":{\"type\":\"integer\"}}";
        } else if (fieldType == String[].class || fieldType == char[].class
                || fieldType == Character[].class) {
            return "{\"type\":\"array\",\"items\":{\"type\":\"string\"}}";
        } else if (fieldType == double[].class || fieldType == Double[].class
                || fieldType == float[].class || fieldType == Float[].class) {
            return "{\"type\":\"array\",\"items\":{\"type\":\"number\"}}";
        } else if (fieldType == BigDecimal[].class) {
            return "{\"type\":\"array\",\"items\":{\"type\":\"number\"}}";
        } else {
            return "unknown";
        }
    }
}

