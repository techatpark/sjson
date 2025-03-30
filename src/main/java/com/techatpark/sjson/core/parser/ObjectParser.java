package com.techatpark.sjson.core.parser;

import com.techatpark.sjson.core.Json;

import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.techatpark.sjson.core.parser.StringParser.getString;
import static com.techatpark.sjson.core.util.ReaderUtil.next;
import static com.techatpark.sjson.core.util.ReaderUtil.getCharacter;

public final class ObjectParser {

    /**
     * util classes.
     */
    private ObjectParser() {
    }

    /**
     * Reads Object from a reader. Reader will
     * stop at the next clean char after object.
     * @param reader
     * @param contextExtractor
     * @return json object
     * @throws IOException
     */
    public static Map<String, Object> getObject(final Reader reader,
                            final Json.ContextExtractor
                                    contextExtractor) throws IOException {
        contextExtractor.startObject();
        boolean eoo = endOfObject(reader, contextExtractor);
        // This is Empty Object
        if (eoo) {
            contextExtractor.setCursorToNextClean();
            return Collections.emptyMap();
        }
        final Map<String, Object> jsonMap = new HashMap<>();
        String key;
        while (!eoo) {
            key = getString(reader);
            next(reader, ':');
            jsonMap.put(key,
                    contextExtractor.getValue());
            eoo = endOfObject(reader, contextExtractor);
        }
        contextExtractor.setCursorToNextClean();
        contextExtractor.endObject();
        return jsonMap;
    }

    /**
     * Determines the Object End. By moving till " or }.
     * @param reader
     * @param contextExtractor
     * @return flag
     * @throws IOException
     */
    private static boolean endOfObject(final Reader reader,
                       final Json.ContextExtractor
                               contextExtractor) throws IOException {
        char character;
        if (contextExtractor.getCursor() == ',') {
            while (getCharacter(reader.read()) != '"') {
                continue;
            }
            return false;
        }

        if (contextExtractor.getCursor() == '}') {
            return true;
        }

        while ((character = getCharacter(reader.read())) != '"'
                && character != '}') {
            continue;
        }
        return character == '}';
    }

    /**
     * Skip Spaces and land reader at the valid character.
     * @param reader
     * @param character
     * @throws IOException
     */
    public static void next(final Reader reader,
                            final char character) throws IOException {
        int charVal;
        do {
            charVal = (char) reader.read();
        } while (charVal != character);
    }

}
