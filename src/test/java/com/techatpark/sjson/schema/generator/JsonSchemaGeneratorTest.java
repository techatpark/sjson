package com.techatpark.sjson.schema.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.victools.jsonschema.generator.OptionPreset;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfig;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaVersion;
import com.techatpark.sjson.schema.generator.example.ComplexPojo;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
 class JsonSchemaGeneratorTest {

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
            ComplexPojo.class
    })
    @Disabled
    void testGenerator(final Class theClass) throws Exception {
        String rawJsonSchema = objectMapper.writeValueAsString(generator.generateSchema(theClass));
        assertEquals(objectMapper.readTree(rawJsonSchema),
                objectMapper.readTree(jsonSchemaGenerator.create(theClass)),
                "JSON Schema was not generated properly");
    }

}
