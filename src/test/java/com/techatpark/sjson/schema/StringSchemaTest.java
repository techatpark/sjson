package com.techatpark.sjson.schema;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class StringSchemaTest {

    @Test
    void read() throws IOException {

        ObjectMapper objectmapper = new ObjectMapper();
        StringSchema stringschema = (StringSchema) JsonSchema.getJsonSchema(String.class);

        String input = """
                ""
                """;

        Assertions.assertEquals(objectmapper.readValue(input,String.class),
            stringschema.read(new StringReader(input)),
                "String reading failed");
    }

}