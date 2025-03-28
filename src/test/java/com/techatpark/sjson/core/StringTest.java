package com.techatpark.sjson.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertThrows;

class StringTest {

    final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Tests Valid String values.
     * <p>
     *     Steps:
     *     1) Pass valid string value(original value).
     *     2) Get JSON String from Jackson . (jsonString)
     *     3) With this JSON String read java object using JSON.
     * </p>
     * Expected Result:
     * This value should be equal to originalvalue.
     * @param originalValue
     * @throws IOException
     */
    @ParameterizedTest
    @ValueSource(strings = {
            "Hari",
            "Escapes \" \\",
            "Tabnext \n \b \t \f \r",
            "Emoji \uD83D\uDE00 \u0000",
            "こんにちは (Japanese)",
            "مرحبا (Arabic)",
            "你好 (Chinese)",
            "Привет (Russian)",
            "வணக்கம் (Tamil)",
            "Mix: English हिंदी 中文 日本語 \uD83D\uDE80"
    })
    void testValid(final String originalValue) throws IOException {

        String jsonString = objectMapper.writeValueAsString(originalValue);
        Assertions.assertEquals(originalValue, new Json().read(new StringReader(jsonString)));
    }

    /**
     * Tests Invalid String values.
     * <p>
     *     Steps:
     *     1) Pass invalid string value(invalidjson).
     *     2) Read java object using JSON.
     * </p>
     * Expected Result:
     * IllegalArguementException should be thrown.
     * @param invalidjson
     */
    @ParameterizedTest
    @ValueSource(strings = {
            "\"\\s\"",
            "\"dddd",
            "\"\n",
            "\"20\\r\n\"",
            "\"Broken Escape \\xG\"",
            "\"Unfinished \\u123\""
    })
    void testInvalid(final String invalidjson) throws IOException {
        assertThrows(IllegalArgumentException.class,
                () -> new Json().read(new StringReader(invalidjson)));
    }
}
