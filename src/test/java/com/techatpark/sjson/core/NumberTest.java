package com.techatpark.sjson.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techatpark.sjson.core.parser.NumberParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.util.List;

import static com.techatpark.sjson.core.util.ReaderUtil.nextClean;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for parsing numbers with optimal memory allocation.
 * <p>
 * The parser converts numbers to the smallest possible Java type:
 * - Uses `byte` first, then `short`, `int`, `long`, and finally `BigInteger` if needed.
 * - Uses `float` first for decimal numbers, then `double` if necessary.
 * - If the number exceeds Java's limits, it throws `IllegalArgumentException`.
 * </p>
 */
class NumberTest {

    final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testLargeNumber() throws JsonProcessingException {
        String largeNumber = "{\"a\" : " + "9".repeat(100000) + "}";
        assertThrows(IllegalArgumentException.class, () -> new Json().read(new StringReader(largeNumber)));
    }

    /**
     * Tests valid numeric values with the best possible memory optimization.
     * <p>
     * Steps:
     * 1) Serialize the number using Jackson to generate a JSON string.
     * 2) Deserialize the JSON back into a Java object.
     * 3) Verify that the parsed value matches the expected type and value.
     * </p>
     * Expected Result:
     * The parsed number should match the expected value and type.
     *
     * @param expectedNumber The original number value.
     * @throws IOException if an error occurs during serialization or deserialization.
     */
    @ParameterizedTest
    @MethodSource("validNumbers")
    void testValidNumbers(final Number expectedNumber) throws IOException {
        String jsonString = objectMapper.writeValueAsString(expectedNumber);
        Assertions.assertEquals(expectedNumber, new Json().read(new StringReader(jsonString)));
    }

    /**
     * Provides valid numeric values optimized for lower memory usage.
     * <p>
     * - Uses `byte` for values between -128 and 127.
     * - Uses `short` if `byte` is exceeded.
     * - Uses `int`, then `long`, then `BigInteger` if needed.
     * - Uses `float` for decimals unless exceeding `Float.MAX_VALUE`, in which case `double` is used.
     * </p>
     *
     * @return A list of valid numbers.
     */
    static List<Number> validNumbers() {
        return List.of(
                (byte) 123,  // Previously Integer, now Byte
                (byte) -128,
                (byte) 127,
                (short) 32000,  // Previously Integer, now Short
                (short) -32000,
                Integer.MIN_VALUE,
                Integer.MAX_VALUE,
                Long.MIN_VALUE,
                Long.MAX_VALUE,
                BigInteger.valueOf(Long.MAX_VALUE).multiply(BigInteger.TEN), // Should throw error

                // Floating points optimized to float first
                (float) 12.34,  // Previously Double
                (float) -0.567,
                (float) 1.23e4,
                (float) 5.67E-8,
                (float) 0.456,
                (float) 1.23e001,
                (float) 0.4e006,
                (float) 0.4e-006,
                (float) 0.4e+006,
                (float) 4e006,
                (float) 4e-006,
                (float) 4e+006
        );
    }

    /**
     * Tests invalid numbers that should trigger an exception.
     * <p>
     * Steps:
     * 1) Pass an invalid number string.
     * 2) Attempt to parse it using the JSON parser.
     * 3) Verify that an `IllegalArgumentException` is thrown.
     * </p>
     * Expected Result:
     * Parsing should fail with an `IllegalArgumentException`.
     *
     * @param invalidNumber An invalid numeric string input.
     */
    @ParameterizedTest
    @MethodSource("invalidNumbers")
    void testInvalidNumbers(final String invalidNumber) {
        assertThrows(IllegalArgumentException.class, () -> new Json().read(new StringReader(invalidNumber)));
    }

    /**
     * Provides invalid number inputs that should cause parsing to fail.
     * <p>
     * - Extremely large numbers that exceed Java's numeric limits.
     * - Non-numeric strings that include characters.
     * - Misformatted numbers (multiple decimal points, incorrect notation).
     * </p>
     *
     * @return A list of invalid number strings.
     */
    static List<String> invalidNumbers() {
        return List.of(
                "1e9999", // Exceeds floating-point range
                "-1e9999", // Negative overflow
                "NaN", // Not-a-Number
                "Infinity", // Infinite value
                "-Infinity", // Negative infinity
                "123abc", // Contains non-numeric characters
                "0x1A", // Hexadecimal notation unsupported
                "1.2.3", // Multiple decimal points
                "1e+2.3" // Exponential notation misuse
        );
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
    @MethodSource("validNumbers")
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

