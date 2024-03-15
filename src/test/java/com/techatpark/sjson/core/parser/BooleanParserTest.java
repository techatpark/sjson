package com.techatpark.sjson.core.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techatpark.sjson.core.Json;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for Boolean Parsers.
 */
class BooleanParserTest {

    final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Tests Valid Boolean Values.
     * <p>
     * Steps:
     *  1) Pass valid boolean value ( originalValue ).
     *  2) Get JSON String from Jackson.
     *  3) With this JSON String read Java Object using SJson.
     * <p>
     * Expected Result:
     * This value should be equal to originalValue.
     * @param originalValue - Valid Boolean Value
     * @throws IOException
     */
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testValid(final Boolean originalValue) throws IOException {
        String jsonString = objectMapper.writeValueAsString(originalValue);
        Assertions.assertEquals(originalValue ,
                new Json().read(new StringReader(jsonString)));
    }
    /**
     * Tests invalid Boolean Values.
     * <p>
     * Steps:
     *  1) Pass invalid boolean value ( invalidJson ).
     *  3) Read JSON String as Java Object using SJson.
     * <p>
     * Expected Result:
     * IllegalArgumentException should be thrown .
     * @param invalidJson - Valid Boolean Value
     */
    @ParameterizedTest
    @ValueSource(strings = {"tru", "fals"})
    void testInvalid(final String invalidJson) {
        assertThrows(IllegalArgumentException.class,
                () -> new Json().read(new StringReader(invalidJson)));
    }
}