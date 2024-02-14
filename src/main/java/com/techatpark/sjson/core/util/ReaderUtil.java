package com.techatpark.sjson.core.util;

import java.io.IOException;
import java.io.Reader;

public final class ReaderUtil {

    /**
     * Utility Class.
     */
    private ReaderUtil() {
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


}
