package com.techatpark.sjson.element;

import com.techatpark.sjson.Json;

import java.io.IOException;
import java.io.Reader;

/**
 * Boolean Parser.
 * */

public final class JsonTrue implements  Json<Boolean> {

    /**
     * This is for length.
     */
    public static final int THREE = 3;

    /**
     * Reads True from Reader. Reader will stip at the "e" symbol.
     * @param reader
     * @param contextExtractor
     * @throws IOException
     */
    public JsonTrue(final Reader reader,
                    final Json.ContextExtractor
                              contextExtractor) throws IOException {
        char[] charBuffer = contextExtractor.next(THREE);
        if (charBuffer[0] != 'r'
                || charBuffer[1] != 'u'
                || charBuffer[2] != 'e') {
            throw new IllegalArgumentException(ILLEGAL_JSON_VALUE);
        }
    }

    @Override
    public Boolean read() {
        return Boolean.TRUE;
    }
}
