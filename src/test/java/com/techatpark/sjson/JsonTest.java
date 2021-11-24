package com.techatpark.sjson;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import static net.andreinc.mockneat.unit.objects.ObjectMap.*;
import static net.andreinc.mockneat.unit.user.Names.*;
import static net.andreinc.mockneat.unit.address.Addresses.*;
import static net.andreinc.mockneat.unit.address.Countries.*;
import static net.andreinc.mockneat.unit.financial.CreditCards.*;
import static net.andreinc.mockneat.unit.financial.Currencies.*;
import static net.andreinc.mockneat.unit.types.Floats.*;

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

    @Test
    void testRandom() {
        Gson gson = new Gson().newBuilder().setPrettyPrinting().create();
        objectMap()
                .put("firstName", names().first())
                .put("lastName", names().last())
                .put("address",
                        objectMap() // object
                                .put("line1", addresses().line1())
                                .put("line2", addresses().line2())
                )
                .put("financial",
                        objectMap() // object
                                .put("creditCard", creditCards().masterCard())
                                .put("amount", floats().range(100.0f, 10_000.0f))
                                .put("currency", currencies().code())
                )
                .put("countries", countries().names().set(10)) // array
                .map(gson::toJson)
                .consume(System.out::println);

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