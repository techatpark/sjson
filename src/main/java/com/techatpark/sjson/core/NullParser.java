package com.techatpark.sjson.core;

import java.io.IOException;
import java.io.Reader;

import static com.techatpark.sjson.core.util.ReaderUtil.ILLEGAL_JSON_VALUE;
import static com.techatpark.sjson.core.util.ReaderUtil.next;

/**
 * Null Parser.
 * */
public final class NullParser {

    /**
     * This is for length.
     */
    public static final int LENGTH = 3;


    /**
     * util classes.
     */
    private NullParser() {

    }
    /**
     * Reads Null from Reader. Reader will stip at the "l" symbol.
     * @param reader
     * @return string
     * @throws IOException
     */
    public static Object getNull(final Reader reader) throws IOException {
        char[] charBuffer = next(reader, LENGTH);
        if (charBuffer[0] == 'u'
                && charBuffer[1] == 'l'
                && charBuffer[2] == 'l') {
            // cursor = 'l';
            return null;
        } else {
            throw new IllegalArgumentException(ILLEGAL_JSON_VALUE);
        }
    }
}
