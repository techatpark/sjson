package com.techatpark.sjson.core;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techatpark.sjson.core.util.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

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
        Assertions.assertEquals(originalValue, new Json().read(new StringReader(originalValue.toString())));
    }

    /**
     * Provides paths to JSON files for parameterized tests.
     *
     * @return Stream of paths to JSON files
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
                Double.MIN_VALUE
        );
    }



}