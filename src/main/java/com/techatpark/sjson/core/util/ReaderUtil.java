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
