package com.techatpark.sjson.element;

import com.techatpark.sjson.Json;

import java.io.IOException;

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
     * @param parser
     * @throws IOException
     */
    public JsonNull(
                    final Parser
                            parser) throws IOException {
        char[] charBuffer = parser.next(LENGTH);
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
