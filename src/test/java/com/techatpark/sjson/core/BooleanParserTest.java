package com.techatpark.sjson.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class BooleanParserTest {

    final ObjectMapper objectMapper = new ObjectMapper();

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testValid(final Boolean aBoolean) throws IOException{
        String jsonString = objectMapper.writeValueAsString(aBoolean);
        StringReader reader = new StringReader(jsonString);
        Assertions.assertEquals(aBoolean , new Json().read(reader));
    }

    @ParameterizedTest
    @ValueSource(strings = {"tru", "fals"})
    void testInvalid(final String jsonString) throws IOException{
        StringReader reader = new StringReader(jsonString);
        Assertions.assertThrows(IllegalArgumentException.class, () ->{
            new Json().read(reader);
        });

    }
}