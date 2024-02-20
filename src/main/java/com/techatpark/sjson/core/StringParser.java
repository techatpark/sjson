package com.techatpark.sjson.core;

import java.io.IOException;
import java.io.Reader;

/**
 * Parser for String.
 */
public final class StringParser {

    /**
     * For invalid JSON.
     */
    private static final String ILLEGAL_JSON_VALUE = "Illegal value at ";

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

    /**
     * get Character.
     * @throws IllegalArgumentException if EOF
     * @param value
     * @return char value
     */
    public static char getCharacter(final int value) {
        if (value == -1) {
            throw new IllegalArgumentException(ILLEGAL_JSON_VALUE);
        }
        return (char) value;
    }

    /**
     * Reads next chars for given length
     * from the reader and fill an char array.
     * @param reader
     * @param length
     * @return char array
     * @throws IOException
     */
    public static char[] next(final Reader reader,
                               final int length) throws IOException {
        char[] cbuf = new char[length];
        reader.read(cbuf, 0, length);
        return cbuf;
    }

}
