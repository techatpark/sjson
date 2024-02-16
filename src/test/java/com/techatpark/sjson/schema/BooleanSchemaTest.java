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

        BooleanSchema booleanSchema =
                (BooleanSchema) JsonSchema.getJsonSchema(Boolean.class);

        Assertions.assertTrue(
                booleanSchema.read(new StringReader("true")),
                "Boolean reading failed");

        Assertions.assertFalse(
                booleanSchema.read(new StringReader("false")),
                "Boolean reading failed");

    }
}