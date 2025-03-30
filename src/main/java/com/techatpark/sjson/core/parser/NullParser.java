package com.techatpark.sjson.core.parser;

import com.techatpark.sjson.core.Json;

import java.io.IOException;
import java.io.Reader;

import static com.techatpark.sjson.core.Json.ILLEGAL_JSON_VALUE;


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
     * @param contextExtractor
     * @return string
     * @throws IOException
     */
    public static Object getNull(final Reader reader,
                             final Json.ContextExtractor
                                     contextExtractor) throws IOException {
        char[] charBuffer = contextExtractor.next(LENGTH);
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
