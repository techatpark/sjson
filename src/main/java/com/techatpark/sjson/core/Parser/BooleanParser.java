package com.techatpark.sjson.core.Parser;

import java.io.IOException;
import java.io.Reader;

import static com.techatpark.sjson.core.Parser.StringParser.next;
/**
 * Boolean Parser.
 * */

public final class BooleanParser {
    /**
     * For invalid JSON.
     */
    private static final String ILLEGAL_JSON_VALUE = "Illegal value at ";
    /**
     * This is for length.
     */
    public static final int THREE = 3;
    /**
     * This is for length.
     */
    public static final int FOUR = 4;

    /**
     * util classes.
     */
    private BooleanParser() {

    }
    /**
     * Reads True from Reader. Reader will stip at the "e" symbol.
     * @param reader
     * @return string
     * @throws IOException
     */
    public static boolean getTrue(final Reader reader) throws IOException {
        char[] charBuffer = next(reader, THREE);
        if (charBuffer[0] == 'r'
                && charBuffer[1] == 'u'
                && charBuffer[2] == 'e') {
            // cursor = 'e';
            return true;
        } else {
            throw new IllegalArgumentException(ILLEGAL_JSON_VALUE);
        }
    }
    /**
     * Reads False from Reader. Reader will strip at the "e" symbol.
     * @param reader
     * @return string
     * @throws IOException
     */
    public static boolean getFalse(final Reader reader) throws IOException {
        char[] charBuffer = next(reader, FOUR);
        if (charBuffer[0] == 'a'
                && charBuffer[1] == 'l'
                && charBuffer[2] == 's'
                && charBuffer[THREE] == 'e') {
            // cursor = 'e';
            return false;
        } else {
            throw new IllegalArgumentException(ILLEGAL_JSON_VALUE);
        }
    }
}
