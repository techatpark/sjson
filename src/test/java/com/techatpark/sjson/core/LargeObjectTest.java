package com.techatpark.sjson.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertThrows;

class LargeObjectTest {

    private final Json parser = new Json();

    @Test
    @Disabled
    void testLargeJson() throws IOException {
        StringBuilder largeJson = new StringBuilder("{ \"data\": [");
        for (int i = 0; i < 100_000; i++) {
            largeJson.append("{\"id\": ").append(i).append(", \"name\": \"Item").append(i).append("\"},");
        }
        largeJson.append("{\"id\": 100000, \"name\": \"LastItem\"}]}");

        JsonNode jsonNode = new ObjectMapper().readTree(new StringReader(largeJson.toString()));

        assertThrows(IllegalArgumentException.class, () -> parser.read(new StringReader(largeJson.toString())));
    }
}
