package com.techatpark.sjson.element;

import com.techatpark.sjson.Json;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public final class JsonObject implements Json<Map<String, Object>> {

    /**
     * Json Value.
     */
    private final Map<String, Json<?>> jsonObject;

    /**
     * Reads Object from a reader. Reader will
     * stop at the next clean char after object.
     * @param contextExtractor
     * @throws IOException
     */
    public JsonObject(
                      final Json.ContextExtractor
                                contextExtractor) throws IOException {
        contextExtractor.startObject();
        boolean eoo = endOfObject(contextExtractor);
        // This is Empty Object
        if (eoo) {
            contextExtractor.setCursorToNextClean();
            jsonObject = Collections.emptyMap();
        } else {
            jsonObject = new HashMap<>();
            String key;
            while (!eoo) {
                key = new JsonString(contextExtractor).read();
                contextExtractor.next(':');
                jsonObject.put(key,
                        contextExtractor.parse());
                eoo = endOfObject(contextExtractor);
            }
            contextExtractor.setCursorToNextClean();
        }

        contextExtractor.endObject();
    }

    /**
     * Determines the Object End. By moving till " or }.
     * @param contextExtractor
     * @return flag
     * @throws IOException
     */
    private static boolean endOfObject(final Json.ContextExtractor
                               contextExtractor) throws IOException {
        char character;
        if (contextExtractor.getCursor() == ',') {
            while (contextExtractor.getCharacter() != '"') {
                continue;
            }
            return false;
        }

        if (contextExtractor.getCursor() == '}') {
            return true;
        }

        while ((character = contextExtractor.getCharacter()) != '"'
                && character != '}') {
            continue;
        }
        return character == '}';
    }



    @Override
    public Map<String, Object> read() {
        Map<String, Object> objectMap = HashMap.newHashMap(jsonObject.size());

        for (Map.Entry<String, Json<?>> entry : jsonObject.entrySet()) {
            objectMap.put(entry.getKey(), entry.getValue().read());
        }

        return objectMap;
    }
}
