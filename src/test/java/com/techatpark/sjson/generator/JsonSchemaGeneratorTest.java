package com.techatpark.sjson.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.victools.jsonschema.generator.OptionPreset;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfig;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaVersion;
import com.techatpark.sjson.generator.example.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonSchemaGeneratorTest {

    private final JsonSchemaGenerator jsonSchemaGenerator;

    private final ObjectMapper objectMapper;

    private final SchemaGenerator generator;

    public JsonSchemaGeneratorTest() {
        jsonSchemaGenerator = new JsonSchemaGenerator();
        objectMapper = new ObjectMapper();
        SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2020_12,
                OptionPreset.PLAIN_JSON);
        SchemaGeneratorConfig config = configBuilder.build();
        generator = new SchemaGenerator(config);
    }

    @ParameterizedTest
    @ValueSource(classes = {
            Product.class
    })
    void testGenerator(final Class theClass) throws Exception {
        String rawJsonSchema = objectMapper.writeValueAsString(generator.generateSchema(theClass));

        assertEquals(objectMapper.readTree(rawJsonSchema),
                objectMapper.readTree(jsonSchemaGenerator.create(theClass)),
                "JSON Schema was not generated properly");
    }



}
