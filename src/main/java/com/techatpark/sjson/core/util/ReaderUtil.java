package com.techatpark.sjson.core.util;

import java.io.IOException;
import java.io.Reader;

public final class ReaderUtil {

    /**
     * For invalid JSON.
     */
    public static final String ILLEGAL_JSON_VALUE = "Illegal value at ";


    /**
     * Utility Class.
     */
    private ReaderUtil() {
    }

    /**
     * Skip Spaces and land reader at the valid character.
     * @param reader
     * @param character
     * @throws IOException
     */
    public static void next(final Reader reader,
                            final char character) throws IOException {
        int charVal;
        do {
            charVal = (char) reader.read();
        } while (charVal != character);
    }

    /**
     * Skip Spaces and land reader at the valid character.
     * @param reader
     * @return valid character
     * @throws IOException
     */
    public static char nextClean(final Reader reader) throws IOException {
        char character;
        do {
            character = (char) reader.read();
        } while (isSpace(character));
        return character;
    }

    /**
     * Determines if this is a space charecter.
     *
     * @param character
     * @return flag
     */
    public static boolean isSpace(final char character) {
        return (character == ' '
                || character == '\n'
                || character == '\r'
                || character == '\t');
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
