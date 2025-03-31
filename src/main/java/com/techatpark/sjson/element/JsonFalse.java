package com.techatpark.sjson.element;

import com.techatpark.sjson.Json;

import java.io.IOException;

/**
 * Boolean Parser.
 * */

public final class JsonFalse implements  Json<Boolean> {

    /**
     * This is for length.
     */
    public static final int THREE = 3;
    /**
     * This is for length.
     */
    public static final int FOUR = 4;

    /**
     * Reads False from Reader. Reader will strip at the "e" symbol.
     * @param contextExtractor
     * @throws IOException
     */
    public JsonFalse(
                     final Json.ContextExtractor
                               contextExtractor) throws IOException {
        char[] charBuffer = contextExtractor.next(FOUR);
        if (charBuffer[0] != 'a'
            || charBuffer[1] != 'l'
            || charBuffer[2] != 's'
            || charBuffer[THREE] != 'e') {
            throw new IllegalArgumentException(ILLEGAL_JSON_VALUE);
        }
    }

    @Override
    public Boolean read() {
        return Boolean.FALSE;
    }
}
