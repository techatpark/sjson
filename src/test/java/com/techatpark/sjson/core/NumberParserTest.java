package com.techatpark.sjson.core;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.io.StringReader;

class NumberParserTest {

    final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Tests Valid Number values.
     * <p>
     *     Steps:
     *     1) Pass valid integer number value(original value).
     *     2) Get JSON String from Jackson . (jsonString)
     *     3) With this JSON String read java object using SJSON.
     * </p>
     * Expected Result:
     * This value should be equal to originalvalue.
     * @param originalValue
     * @throws IOException
     */
    @ParameterizedTest
    @ValueSource(ints = {
            Integer.MIN_VALUE,
            Integer.MAX_VALUE
    })
    void testValid(final Number originalValue) throws IOException {
        String jsonString = objectMapper.writeValueAsString(originalValue);
        Assertions.assertEquals(originalValue, new Json().read(new StringReader(jsonString)));
    }


}