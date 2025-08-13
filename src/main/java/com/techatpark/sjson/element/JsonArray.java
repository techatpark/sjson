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
     * @param parser
     * @throws IOException
     */
    public JsonArray(
                     final Parser
                             parser) throws IOException {
        parser.startArray();
        final Json<?> value = parser.parse();
        // If Empty Array
        if (value == parser) {
            parser.setCursorToNextClean();
            jsonElements = Collections.emptyList();
        } else {
            jsonElements = new ArrayList<>();
            jsonElements.add(value);
            while (!endOfArray(parser)) {
                jsonElements.add(parser.parse());
            }
            parser.setCursorToNextClean();
        }

        parser.endArray();
    }


    /**
     * Determine array close character.
     * @param parser
     * @return flag
     * @throws IOException
     */
    private boolean endOfArray(final Parser parser) throws IOException {
        char character;
        if (parser.getCursor() == ',') {
            return false;
        }
        if (parser.getCursor() == ']') {
            return true;
        }

        while ((character = parser.getCharacter()) != ','
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
