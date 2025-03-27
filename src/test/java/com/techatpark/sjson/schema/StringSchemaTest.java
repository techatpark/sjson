package com.techatpark.sjson.schema;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StringSchemaTest {

    ObjectMapper objectmapper = new ObjectMapper();
    StringSchema stringschema = (StringSchema) JsonSchema.getJsonSchema(String.class);

    @Test
    void read() throws IOException {

        Assertions.assertNull(stringschema.read(new StringReader("null")), "String null reading failed");
        assertStringParsing("\"\"");

        assertThrows(IllegalArgumentException.class, () -> {
            stringschema.read(new StringReader("Illegal"));
        });

    }

    private void assertStringParsing(String input) throws IOException {
        assertEquals(objectmapper.readValue(input, String.class), stringschema.read(new StringReader(input)), "String reading failed");
    }

}