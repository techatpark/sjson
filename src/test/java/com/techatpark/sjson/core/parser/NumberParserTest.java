package com.techatpark.sjson.core.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techatpark.sjson.core.Json;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.util.List;

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
    @MethodSource("numbers")
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
    @MethodSource("numbers")
    void testCursor(final Number originalValue) throws IOException {
        String jsonString = objectMapper.writeValueAsString(originalValue);
        testCursor(jsonString,",1");
        testCursor(jsonString,",\n\t1");
        testCursor(jsonString,"    ,\n\t1");
    }

    private void testCursor(final String jsonString, final String suffix) throws IOException {
        StringReader reader = new StringReader(jsonString + suffix);
        char firstChar = nextClean(reader); // Move to First Digit
        NumberParser.getNumber(new Json.ContentExtractor(reader),reader,  firstChar);
        assertEquals('1',
                nextClean(reader));
    }

    /**
     * Provides Numbers to Test.
     *
     * @return Stream of Numbers
     * @throws IOException if there is an issue listing files
     */
    private static List<Number> numbers() {
        return List.of(
                Byte.MIN_VALUE,
                Byte.MAX_VALUE,
                Short.MIN_VALUE,
                Short.MAX_VALUE,
                Integer.MIN_VALUE,
                Integer.MAX_VALUE,
                Long.MIN_VALUE,
                Long.MAX_VALUE,
                BigInteger.valueOf(Long.MIN_VALUE).multiply(BigInteger.TEN),
                BigInteger.valueOf(Long.MAX_VALUE).multiply(BigInteger.TEN),
                Float.MIN_VALUE,
                Float.MAX_VALUE,
                Double.MIN_VALUE,
                123,
                -456,
                12.34,
                -0.567,
                1.23e4,
                5.67E-8,
                0.456,
                1.23e001,
                +789,
                0.4e006,
                0.4e-006,
                0.4e+006,
                4e006,
                4e-006,
                4e+006
        );
    }

}