package com.techatpark.sjson.core.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techatpark.sjson.core.Json;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.StringReader;

import static com.techatpark.sjson.core.util.ReaderUtil.nextClean;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
    @MethodSource("com.techatpark.sjson.core.util.TestDataProvider#numbers")
    void testValid(final Number originalValue) throws IOException {
        String jsonString = objectMapper.writeValueAsString(originalValue);
        Assertions.assertEquals(originalValue.toString(), new Json().read(new StringReader(jsonString)).toString());
    }

    /**
     * Tests Where NumberParser completes cursor.
     * <p>
     *     Steps:
     *     1) Pass valid number value(validjson) (With following 1).
     *     2) Read java object using NumberParser.
     * </p>
     * Expected Result:
     * next Clean Should be at 1
     *
     * @param originalValue
     */
    @ParameterizedTest
    @MethodSource("com.techatpark.sjson.core.util.TestDataProvider#numbers")
    void testCursor(final Number originalValue) throws IOException {
        String jsonString = objectMapper.writeValueAsString(originalValue);
        testCursor(jsonString,",1");
        testCursor(jsonString,",\n\t1");
        testCursor(jsonString,"    ,\n\t1");
    }

    private void testCursor(final String jsonString, final String suffix) throws IOException {
        final StringReader reader = new StringReader(jsonString + suffix);
        final char firstChar = nextClean(reader); // Move to First Digit
        NumberParser.getNumber(new Json.ContextExtractor(reader),reader,  firstChar);
        assertEquals('1',
                nextClean(reader));
    }

}