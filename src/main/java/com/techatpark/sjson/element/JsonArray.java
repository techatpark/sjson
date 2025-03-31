package com.techatpark.sjson.element;

import com.techatpark.sjson.Json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class JsonArray implements Json<List<?>> {

    /**
     * Json Elements of the Array.
     */
    private final List<Json<?>> jsonElements;

    /**
     * Reads an Array. Reader stops at next clean character.
     * @param contextExtractor
     * @throws IOException
     */
    public JsonArray(
                     final Json.ContextExtractor
                               contextExtractor) throws IOException {
        contextExtractor.startArray();
        final Json<?> value = contextExtractor.parse();
        // If not Empty Array
        if (value == contextExtractor) {
            contextExtractor.setCursorToNextClean();
            jsonElements = Collections.emptyList();
        } else {
            jsonElements = new ArrayList<>();
            jsonElements.add(value);
            while (!endOfArray(contextExtractor)) {
                jsonElements.add(contextExtractor.parse());
            }
            contextExtractor.setCursorToNextClean();
        }

        contextExtractor.endArray();
    }


    /**
     * Determine array close character.
     * @param contextExtractor
     * @return flag
     * @throws IOException
     */
    private static boolean endOfArray(
                              final Json.ContextExtractor
                                      contextExtractor) throws IOException {
        char character;
        if (contextExtractor.getCursor() == ',') {
            return false;
        }
        if (contextExtractor.getCursor() == ']') {
            return true;
        }

        while ((character = contextExtractor.getCharacter()) != ','
                && character != ']') {
            continue;
        }
        return character == ']';
    }


    @Override
    public List<?> read() {
        return jsonElements
                .stream().map(Json::read)
                .toList();
    }
}
