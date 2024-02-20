package com.techatpark.sjson.core;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.techatpark.sjson.core.StringParser.getCharacter;

public final class ArrayParser {

    /**
     * util classes.
     */
    private ArrayParser() {
    }

    /**
     * Reades an Array. Reader stops at next clean character.
     * @param reader
     * @param contentExtractor
     * @return list
     * @throws IOException
     */
    public static List<Object> getArray(final Reader reader,
                                  final Json.ContentExtractor
                                          contentExtractor) throws IOException {
        final Object value = contentExtractor.getValue();
        // If not Empty Array
        if (value == contentExtractor) {
            contentExtractor.moveCursorToNextClean();
            return Collections.emptyList();
        }
        final List<Object> list = new ArrayList<>();
        list.add(value);
        while (!endOfArray(reader, contentExtractor)) {
            list.add(contentExtractor.getValue());
        }
        contentExtractor.moveCursorToNextClean();
        return list;
    }
    /**
     * Determine array close character.
     * @param reader
     * @param contentExtractor
     * @return flag
     * @throws IOException
     */
    private static boolean endOfArray(final Reader reader,
                                      final Json.ContentExtractor
            contentExtractor) throws IOException {
        char character;
        if (contentExtractor.getCursor() == ']') {
            return true;
        }
        if (contentExtractor.getCursor() == ',') {
            return false;
        }
        while ((character = getCharacter(reader.read())) != ','
                && character != ']') {
            continue;
        }
        return character == ']';
    }


}
