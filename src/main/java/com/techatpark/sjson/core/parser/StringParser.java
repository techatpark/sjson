package com.techatpark.sjson.core.parser;

import java.io.IOException;
import java.io.Reader;

import static com.techatpark.sjson.core.util.ReaderUtil.ILLEGAL_JSON_VALUE;
import static com.techatpark.sjson.core.util.ReaderUtil.getCharacter;
import static com.techatpark.sjson.core.util.ReaderUtil.next;

/**
 * Parser for String.
 */
public final class StringParser {



    /**
     * Length for hex char.
     */
    private static final int LENGTH = 4;

    /**
     * Radix for hex char.
     */
    private static final int RADIX = 16;


    /**
     * Utility Class.
     */
    private StringParser() {
    }

    /**
     * Reads String from Reader. Reader will stop at the " symbol
     * @param reader
     * @return string
     * @throws IOException
     */
    public static String getString(final Reader reader) throws IOException {
        final StringBuilder sb = new StringBuilder();
        char character;

        while ((character = getCharacter(reader.read())) != '\\'
                && character != '"') {
            sb.append(character);
        }

        // Normal String
        if (character == '"') {
            return sb.toString();
        }

        // String with escape characters ?!
        for (;;) {
            switch (character) {
                case '\\':
                    character = getCharacter(reader.read());
                    switch (character) {
                        case '"', '\'', '\\', '/' -> sb.append(character);
                        case 'u' -> sb.append((char) Integer
                                .parseInt(new String(next(reader, LENGTH)),
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
                        return sb.toString();
                    }
                    sb.append(character);
            }
            character = getCharacter(reader.read());
        }
    }


}
