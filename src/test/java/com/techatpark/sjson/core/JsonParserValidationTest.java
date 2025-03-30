package com.techatpark.sjson.core;

import com.techatpark.sjson.core.Json;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

class JsonParserValidationTest {

    

    /**
     * Test Illegal JSON Texts.
     *
     * @throws IOException
     */
    @ParameterizedTest
    @MethodSource("illegalJsonPaths")
    void testIllegal(Path path) {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Json.read(new FileReader(path.toFile()));
        });
    }

    private static Stream<Path> illegalJsonPaths() throws IOException {
        return Files.list(Paths.get("src/test/resources/illegal"))
                .filter(path -> !Files.isDirectory(path));
    }
}
