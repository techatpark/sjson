package com.techatpark.sjson.core;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * There are cases that where invalid json still be parsed.
 * This class tests them
 */
class InvalidButParsableTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "1e9999", // Exceeds floating-point range
            "-1e9999", // Negative overflow
            "07",
            "{key: value}",
            "\"Newline in string \n remains invalid\""})
    void testInvalid(final String invalidJson) {
        assertDoesNotThrow(
                () -> new Json().read(new StringReader(invalidJson)));
    }

}
