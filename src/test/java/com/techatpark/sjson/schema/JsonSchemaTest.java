package com.techatpark.sjson.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import com.techatpark.sjson.util.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;


class JsonSchemaTest {

    public final ObjectMapper mapper = new ObjectMapper();

    @ParameterizedTest
    @MethodSource("jsonSchemaFilesProvider")
    @Disabled
    void read(final File schemaFile) throws IOException {
        File dataFile = new File(new File(schemaFile.getParentFile().getParentFile(),"samples")
                ,schemaFile.getName());
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
        JsonSchema jsonSchema = factory.getSchema(new FileInputStream(schemaFile));
        JsonNode jsonNode = mapper.readTree(new FileReader(dataFile));
        Set<ValidationMessage> expectedErrors = jsonSchema.validate(jsonNode);
        Set<ValidationMessage> actualErrors = jsonSchema.validate(jsonNode);
        Assertions.assertEquals(expectedErrors.size(), actualErrors.size());
    }

    /**
     * Provides paths to JSON files for parameterized tests.
     *
     * @return Stream of paths to JSON files
     * @throws IOException if there is an issue listing files
     */
    private static Set<File> jsonSchemaFilesProvider() throws IOException {
        return TestUtil.getJSONSchemaFiles();
    }

}