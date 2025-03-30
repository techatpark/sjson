package com.techatpark.sjson.element;

import com.techatpark.sjson.Json;

import java.io.IOException;
import java.io.Reader;

/**
 * Null Parser.
 * */
public final class JsonNull implements  Json<Object> {

    /**
     * This is for length.
     */
    public static final int LENGTH = 3;

    /**
     * Reads Null from Reader. Reader will stip at the "l" symbol.
     * @param reader
     * @param contextExtractor
     * @throws IOException
     */
    public JsonNull(final Reader reader,
                    final Json.ContextExtractor
                               contextExtractor) throws IOException {
        char[] charBuffer = contextExtractor.next(LENGTH);
        if (charBuffer[0] != 'u'
            || charBuffer[1] != 'l'
            || charBuffer[2] != 'l') {
                throw new IllegalArgumentException(ILLEGAL_JSON_VALUE);
        }
    }

    @Override
    public Object read() {
        return null;
    }
}
