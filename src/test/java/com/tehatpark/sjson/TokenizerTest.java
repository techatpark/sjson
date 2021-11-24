package com.tehatpark.sjson;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

class TokenizerTest {

    private final Tokenizer tokenizer = new Tokenizer();

    @Test
    void testParsing() throws IOException {

        final char[] charArray = getJSONSample("basic");

        tokenizer.getTokens(charArray).forEach(token -> {
            System.out.format("%12s%15s%25s%15s%50s\n",
                    token.tokenType(),
                    token.key(),
                    token.value(),
                    token.openingTokenTypes().isEmpty() ? ""  : token
                            .openingTokenTypes()
                            .stream().map(Tokenizer.TokenType::toString)
                            .collect(Collectors.joining(",")),
                    token.closingTokenTypes().isEmpty() ? ""  : token
                            .closingTokenTypes()
                            .stream().map(Tokenizer.ClosingTokenType::toString)
                            .collect(Collectors.joining(",")));
        });

    }


    private char[] getJSONSample(final String fileName) throws IOException {
        return Files
                .readString(Path
                        .of("src/test/resources/samples/" + fileName + ".json"))
                .toCharArray();
    }
}