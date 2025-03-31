package com.techatpark.sjson.element;

import com.techatpark.sjson.Json;

import java.io.IOException;

/**
 * Parser for String.
 */
public final class JsonString implements Json<String> {



    /**
     * Length for hex char.
     */
    private static final int LENGTH = 4;

    /**
     * Radix for hex char.
     */
    private static final int RADIX = 16;

    /**
     * Json String.
     */
    private final StringBuilder jsonString;

    /**
     * Reads String from Reader. Reader will stop at the " symbol
     * @param contextExtractor
     * @throws IOException
     */
    public JsonString(
                      final Json.ContextExtractor
                               contextExtractor) throws IOException {
        jsonString = getString(contextExtractor);
    }

    private static StringBuilder getString(
                        final Json.ContextExtractor contextExtractor)
            throws IOException {
        final StringBuilder sb = new StringBuilder();
        char character;

        while (
            (character = contextExtractor.getCharacter()) != '\\'
                && character != '"'
        ) {
            sb.append(character);
        }

        // Normal String
        if (character == '"') {
            return sb;
        }

        // String with escape characters ?!
        for (;;) {
            switch (character) {
                case '\\':
                    character = contextExtractor.getCharacter();
                    switch (character) {
                        case '"', '\'', '\\', '/' -> sb.append(character);
                        case 'u' -> sb.append((char) Integer
                            .parseInt(new String(contextExtractor.next(LENGTH)),
                                    RADIX));
                        case 'b' -> sb.append('\b');
                        case 't' -> sb.append('\t');
                        case 'n' -> sb.append('\n');
                        case 'f' -> sb.append('\f');
                        case 'r' -> sb.append('\r');
                        default -> throw new IllegalArgumentException(
                                ILLEGAL_JSON_VALUE);
                    }
                    break;
                case 0, '\n', '\r':
                    throw new IllegalArgumentException(ILLEGAL_JSON_VALUE);
                default:
                    if (character == '"') {
                        return sb;
                    }
                    sb.append(character);
            }
            character = contextExtractor.getCharacter();
        }
    }


    @Override
    public String read() {
        return jsonString.toString();
    }
}
