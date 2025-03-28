package com.techatpark.sjson.core;

import org.json.JSONObject;
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

        JSONObject jsonObject = new JSONObject(new StringReader(largeJson.toString()));

        assertThrows(IllegalArgumentException.class, () -> parser.read(new StringReader(largeJson.toString())));
    }
}
