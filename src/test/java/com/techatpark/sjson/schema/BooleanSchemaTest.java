package com.techatpark.sjson.schema;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class BooleanSchemaTest {
    BooleanSchema booleanSchema =
            (BooleanSchema) JsonSchema.getJsonSchema(Boolean.class);
    @Test
    void read() throws IOException {
        System.out.println(booleanSchema);


        Assertions.assertTrue(
                booleanSchema.read(new StringReader("true")),
                "Boolean reading failed");

        Assertions.assertFalse(
                booleanSchema.read(new StringReader("false")),
                "Boolean reading failed");

        Assertions.assertNull(
                booleanSchema.read(new StringReader("null")),
                "Boolean Reading null");

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            booleanSchema.read(new StringReader("illegal"));
        });

    }
}