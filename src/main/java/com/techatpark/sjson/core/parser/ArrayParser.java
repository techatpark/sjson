package com.techatpark.sjson.core.parser;

import com.techatpark.sjson.core.Json;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.techatpark.sjson.core.util.ReaderUtil.getCharacter;
import static com.techatpark.sjson.core.util.ReaderUtil.nextClean;

public final class ArrayParser {

    /**
     * util classes.
     */
    private ArrayParser() {
    }

    /**
     * Reades an Array. Reader stops at next clean character.
     * @param reader
     * @param contextExtractor
     * @return list
     * @throws IOException
     */
    public static List<Object> getArray(final Reader reader,
                                  final Json.ContextExtractor
                                          contextExtractor) throws IOException {
        contextExtractor.startArray();
        final Object value = contextExtractor.getValue();
        // If not Empty Array
        if (value == contextExtractor) {
            contextExtractor.setCursor(nextClean(reader));
            return Collections.emptyList();
        }
        final List<Object> list = new ArrayList<>();
        list.add(value);
        while (!endOfArray(reader, contextExtractor)) {
            list.add(contextExtractor.getValue());
        }
        contextExtractor.setCursor(nextClean(reader));
        contextExtractor.endArray();
        return list;
    }
    /**
     * Determine array close character.
     * @param reader
     * @param contextExtractor
     * @return flag
     * @throws IOException
     */
    private static boolean endOfArray(final Reader reader,
                              final Json.ContextExtractor
                                      contextExtractor) throws IOException {
        char character;
        if (contextExtractor.getCursor() == ',') {
            return false;
        }
        if (contextExtractor.getCursor() == ']') {
            return true;
        }

        while ((character = getCharacter(reader.read())) != ','
                && character != ']') {
            continue;
        }
        return character == ']';
    }


}
