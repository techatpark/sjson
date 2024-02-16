package com.techatpark.sjson.schema;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class BooleanSchemaTest {

    @Test
    void read() throws IOException {

        ObjectMapper objectmapper = new ObjectMapper();
        BooleanSchema booleanSchema = (BooleanSchema) JsonSchema.getJsonSchema(Boolean.class);

        String input = "true";

        Assertions.assertEquals(objectmapper.readValue(input,Boolean.class),
                booleanSchema.read(new StringReader(input)),
                "Boolean reading failed");

    }
}