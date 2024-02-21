package com.techatpark.sjson.core;

import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.techatpark.sjson.core.StringParser.getCharacter;
import static com.techatpark.sjson.core.StringParser.getString;

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
     * @param contentExtractor
     * @return json object
     * @throws IOException
     */
    public static Map<String, Object> getObject(final Reader reader,
                            final Json.ContentExtractor
                            contentExtractor) throws IOException {
        boolean eoo = endOfObject(reader, contentExtractor);
        // This is Empty Object
        if (eoo) {
            contentExtractor.moveCursorToNextClean();
            return Collections.emptyMap();
        }
        final Map<String, Object> jsonMap = new HashMap<>();
        while (!eoo) {
            jsonMap.put(getKey(reader, contentExtractor),
                    contentExtractor.getValue());
            eoo = endOfObject(reader, contentExtractor);
        }
        contentExtractor.moveCursorToNextClean();
        return jsonMap;
    }

    /**
     * Read Key as a String. It gets key from String Pool.
     * @param reader
     * @param contentExtractor
     * @return key
     * @throws IOException
     */
    private static String getKey(final Reader reader,
                                 final Json.ContentExtractor
                                         contentExtractor) throws IOException {
        final String key = getString(reader).intern();
        contentExtractor.moveCursorToNextClean();
        return key;
    }

    /**
     * Determines the Object End. By moving till " or }.
     * @param reader
     * @param contentExtractor
     * @return flag
     * @throws IOException
     */
    private static boolean endOfObject(final Reader reader,
                                       final Json.ContentExtractor
                                       contentExtractor) throws IOException {
        char character;
        if (contentExtractor.getCursor() == '}') {
            return true;
        }
        if (contentExtractor.getCursor() == ',') {
            while (getCharacter(reader.read()) != '"') {
                continue;
            }
            return false;
        }
        while ((character = getCharacter(reader.read())) != '"'
                && character != '}') {
            continue;
        }
        return character == '}';
    }

}