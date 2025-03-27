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
    @ValueSource(strings = {"{key: value}"})
    void testInvalid(final String invalidJson) {
        assertDoesNotThrow(
                () -> new Json().read(new StringReader(invalidJson)));
    }

}
