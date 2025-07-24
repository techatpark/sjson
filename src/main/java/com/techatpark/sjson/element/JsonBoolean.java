package com.techatpark.sjson.element;

import com.techatpark.sjson.Json;

import java.io.IOException;

/**
 * Boolean Parser.
 * */

public final class JsonBoolean implements  Json<Boolean> {

    /**
     * This is for length.
     */
    public static final int THREE = 3;

    /**
     * This is for length.
     */
    public static final int FOUR = 4;


    /**
     * Thr value to be returned.
     */
    private final  boolean value;
    /**
     * Reads True from Reader. Reader will stip at the "e" symbol.
     * @param parser
     * @param theValue
     * @throws IOException
     */
    public JsonBoolean(
            final Parser
                            parser,
            final boolean theValue) throws IOException {
        this.value = theValue;
        if (theValue) {
            char[] charBuffer = parser.next(THREE);
            if (charBuffer[0] != 'r'
                    || charBuffer[1] != 'u'
                    || charBuffer[2] != 'e') {
                throw new IllegalArgumentException(ILLEGAL_JSON_VALUE);
            }
        } else {
            char[] charBuffer = parser.next(FOUR);
            if (charBuffer[0] != 'a'
                    || charBuffer[1] != 'l'
                    || charBuffer[2] != 's'
                    || charBuffer[THREE] != 'e') {
                throw new IllegalArgumentException(ILLEGAL_JSON_VALUE);
            }
        }
    }

    @Override
    public Boolean read() {
        return this.value;
    }
}
