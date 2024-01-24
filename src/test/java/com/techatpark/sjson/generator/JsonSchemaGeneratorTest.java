package com.techatpark.sjson.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.reinert.jjschema.v1.JsonSchemaFactory;
import com.github.reinert.jjschema.v1.JsonSchemaV4Factory;
import com.techatpark.sjson.generator.model.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonSchemaGeneratorTest {

    private final JsonSchemaGenerator jsonSchemaGenerator;

    private final ObjectMapper objectMapper;

    private final JsonSchemaFactory schemaFactory;

    public JsonSchemaGeneratorTest() {
        jsonSchemaGenerator = new JsonSchemaGenerator();
        objectMapper = new ObjectMapper();
        schemaFactory = new JsonSchemaV4Factory();
        schemaFactory.setAutoPutDollarSchema(true);
    }

    @Test
    void testGenerator() throws Exception {
        assertEquals(schemaFactory.createSchema(Product.class),
                objectMapper.readTree(jsonSchemaGenerator.create(Product.class)),
                "JSON Schema was not generated properly");
    }

    @ParameterizedTest(name = "{index} => fieldType={0}, expectedSchema={1}")
    @MethodSource("generateParameterizedTypeSchemaTestData")
    void testGenerateParameterizedTypeSchema(Type fieldType, String expectedSchema) {
        String actualSchema = jsonSchemaGenerator.generateParameterizedTypeSchema(fieldType);
        assertEquals(expectedSchema, actualSchema);
    }

    private static Stream<Object[]> generateParameterizedTypeSchemaTestData() {
        return Stream.of(
                // Test case 1: Parameterized type with String as the actual type argument
                new Object[]{getParameterizedType(String.class), "{\"type\":\"array\",\"items\":{\"type\":\"string\"}}"},
                // Test case 2: Parameterized type with BigDecimal as the actual type argument
                new Object[]{getParameterizedType(BigDecimal.class), "{\"type\":\"array\",\"items\":{\"type\":\"unknown\"}}"},
                // Test case 3: Parameterized type with List<String> as the actual type argument
                new Object[]{getParameterizedType(List.class, String.class), "{\"type\":\"array\",\"items\":{\"type\":\"string\"}}"},
                // Test case 4: Parameterized type with List<BigDecimal> as the actual type argument
                new Object[]{getParameterizedType(List.class, BigDecimal.class), "{\"type\":\"array\",\"items\":{\"type\":\"unknown\"}}"}
        );
    }

    private static ParameterizedType getParameterizedType(Class<?> actualTypeArgument) {
        return new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[]{actualTypeArgument};
            }

            @Override
            public Type getRawType() {
                return List.class;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };
    }

    private static ParameterizedType getParameterizedType(Class<?> rawType, Class<?> actualTypeArgument) {
        return new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[]{actualTypeArgument};
            }

            @Override
            public Type getRawType() {
                return rawType;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };
    }


    @ParameterizedTest(name = "{index} => fieldType={0}, expectedJsonType={1}")
    @CsvSource({
            "int, integer",
            "long, integer",
            "java.lang.Integer, integer",
            "java.lang.Long, integer",
            "double, number",
            "float, number",
            "java.lang.Double, number",
            "java.lang.Float, number",
            "java.math.BigDecimal, number",
            "java.util.Map, unknown"
    })
    void testGetJsonType(Class<?> fieldType, String expectedJsonType) {
        String actualJsonType = jsonSchemaGenerator.getJsonType(fieldType);
        assertEquals(expectedJsonType, actualJsonType);
    }

}
