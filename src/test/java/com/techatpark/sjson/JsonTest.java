package com.techatpark.sjson;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class JsonTest {



    @Test
    void testvalue() throws IOException {
        final ObjectMapper jackson = new ObjectMapper();
        for (String jsonFile :
                getJSONFiles()) {
            // 1. Generate JSONNode directly
            String originalJsonText = getJSONSample(jsonFile);
            JsonNode jacksonJsonNode = jackson
                    .readTree(new StringReader(originalJsonText));

            // 2. Generate JSONNode through our implementation
            Map<String, Object> ourJsonObject = new Json(originalJsonText)
                    .value();
            String reversedJsonText = jackson
                    .writeValueAsString(ourJsonObject);
            JsonNode ourJsonNode = jackson
                    .readTree(new StringReader(reversedJsonText));


            // 3. Compare Both
            Assertions.assertEquals(jacksonJsonNode,
                    ourJsonNode,
                    "Reverse JSON Failed for " + jsonFile);
        }


    }


    public Set<String> getJSONFiles() throws IOException {
        try (Stream<Path> stream = Files.list(Path.of("src/test/resources/samples/"))) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toSet());
        }
    }

    private String getJSONSample(final String fileName) throws IOException {
        return Files.readString(Path.of("src/test/resources/samples/" + fileName));
    }
}