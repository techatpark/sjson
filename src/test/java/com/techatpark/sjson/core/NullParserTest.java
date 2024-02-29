package com.techatpark.sjson.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class NullParserTest {

    final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Tests Valid Null value.
     * <p>
     *     Steps:
     *     1) Pass a null value.
     *     2) Get JSON String from Jackson . (jsonString)
     *     3) With this JSON String read java object using JSON.
     * </p>
     * Expected Result:
     * This value should be null.
     * @throws IOException
     */
    @Test
    void testValid() throws IOException {
        String jsonString = objectMapper.writeValueAsString(null);
        Assertions.assertNull(new Json().read(new StringReader(jsonString)));
    }

    /**
     * Tests Invalid Null value.
     * <p>
     *     Steps:
     *     1) Pass a invalid null value(invalidjson).
     *     2) Read java object using JSON.
     * </p>
     * Expected Result:
     * IllegalArguementException should be thrown.
     * @param invalidjson
     */
    @ParameterizedTest
    @ValueSource(strings = {
            "nu"
    })
    void testInvalid(final String invalidjson) throws IOException {
        assertThrows(IllegalArgumentException.class,
                () -> new Json().read(new StringReader(invalidjson)));
    }

}