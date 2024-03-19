package com.techatpark.sjson.schema;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.victools.jsonschema.generator.OptionPreset;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfig;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaVersion;
import com.techatpark.sjson.core.util.TestDataProvider;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

class JsonSchemaTest {

    private final SchemaGenerator generator;

    public final ObjectMapper mapper ;

    JsonSchemaTest() {
        mapper = new ObjectMapper();
        SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2020_12,
                OptionPreset.PLAIN_JSON);
        SchemaGeneratorConfig config = configBuilder.build();
        generator = new SchemaGenerator(config);
    }

    @ParameterizedTest
    @ValueSource(classes = {
            Boolean.class,
            String.class
    })
    @Disabled
    void testGenerator(final Class theClass) throws Exception {
        String rawJsonSchema = mapper.writeValueAsString(generator.generateSchema(theClass));
        System.out.println(JsonSchema.getJsonSchema(theClass).toString());
    }


    @ParameterizedTest
    @MethodSource("jsonSchemaFilesProvider")
    @Disabled
    void read(final File schemaFile) throws IOException {

        JsonSchema jsonSchema = JsonSchema.getJsonSchema(new FileReader(schemaFile));

        System.out.println(jsonSchema.toString());





//        File dataFile = new File(new File(schemaFile.getParentFile().getParentFile(),"samples")
//                ,schemaFile.getName());
//        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
//        JsonSchema jsonSchema = factory.getSchema(new FileInputStream(schemaFile));
//        JsonNode jsonNode = mapper.readTree(new FileReader(dataFile));
//        Set<ValidationMessage> expectedErrors = jsonSchema.validate(jsonNode);
//        Set<ValidationMessage> actualErrors = jsonSchema.validate(jsonNode);
//        Assertions.assertEquals(expectedErrors.size(), actualErrors.size());
    }

    /**
     * Provides paths to JSON files for parameterized tests.
     *
     * @return Stream of paths to JSON files
     * @throws IOException if there is an issue listing files
     */
    private static Set<File> jsonSchemaFilesProvider() throws IOException {
        return TestDataProvider.getJSONSchemaFiles();
    }

}