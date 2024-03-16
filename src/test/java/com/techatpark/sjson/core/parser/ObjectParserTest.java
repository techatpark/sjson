package com.techatpark.sjson.core.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.techatpark.sjson.core.Json;
import com.techatpark.sjson.core.util.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ObjectParserTest {

    final ObjectMapper objectMapper = new ObjectMapper();


    /**
     * Provides paths to JSON files for parameterized tests.
     *
     * @return Stream of paths to JSON files
     * @throws IOException if there is an issue listing files
     */
    private static List<Path> jsonObjectStrings() throws IOException {
        List<Path> jsonObjectStrings = new ArrayList<>();

        for (Path path : TestUtil.getJSONFiles()) {
            String jsonText = Files.readString(path).trim();
            if (jsonText.startsWith("{")) {
                jsonObjectStrings.add(path);
            }
        }

        return jsonObjectStrings;

    }

    @ParameterizedTest
    @MethodSource("jsonObjectStrings")
    void testValid(final Path path) throws IOException {
        Assertions.assertEquals(JsonParser
                        .parseReader(
                                new StringReader(objectMapper.writeValueAsString(
                                        new Json().read(
                                                new FileReader(path.toFile())
                                        )))),
                JsonParser.parseReader(new FileReader(path.toFile())),
                "Reverse JSON Failed for " + path);
    }

}