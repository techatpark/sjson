package com.techatpark.sjson;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.stream.Stream;

class JsonValidationTest {




    public final Json sJson = new Json();


    /**
     * Test Illegal JSON Texts.
     * @throws IOException
     */
    @Test
    void testIllegal() throws IOException {
        try (Stream<Path> stream
                     = Files.list(Paths.get("src/test/resources/illegal"))) {
            stream
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {

                        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
                            sJson.read(new FileReader(path.toFile()));
                        });

                    });
        }
    }




}