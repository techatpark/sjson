package com.techatpark.sjson;

import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

class JsonSchemaTest {

    @Test
    void read() throws IOException {

        JsonSchema jsonSchema =
                new JsonSchema(new FileReader("src/test/resources/schemas/product.json"));

        jsonSchema.read(new FileReader("src/test/resources/schemas/person.json"));

        jsonSchema.jsonText(new HashMap<>());

    }
}