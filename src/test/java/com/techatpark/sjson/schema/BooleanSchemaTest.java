package com.techatpark.sjson.schema;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class BooleanSchemaTest {
    BooleanSchema booleanSchema =
            (BooleanSchema) JsonSchema.getJsonSchema(Boolean.class);
    @Test
    void read() throws IOException {
        assertTrue(
                booleanSchema.read(new StringReader("true")),
                "Boolean reading failed");

        assertFalse(
                booleanSchema.read(new StringReader("false")),
                "Boolean reading failed");

        assertNull(
                booleanSchema.read(new StringReader("null")),
                "Boolean Reading null");

        assertThrows(IllegalArgumentException.class, () -> {
            booleanSchema.read(new StringReader("illegal"));
        });

    }
}