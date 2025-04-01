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
     * @param parser
     * @throws IOException
     */
    public JsonObject(
                      final Parser
                              parser) throws IOException {
        parser.startObject();
        boolean eoo = endOfObject(parser);
        // This is Empty Object
        if (eoo) {
            parser.setCursorToNextClean();
            jsonObject = Collections.emptyMap();
        } else {
            jsonObject = new HashMap<>();
            String key;
            while (!eoo) {
                key = new JsonString(parser).read();
                parser.next(':');
                jsonObject.put(key,
                        parser.parse());
                eoo = endOfObject(parser);
            }
            parser.setCursorToNextClean();
        }

        parser.endObject();
    }

    /**
     * Determines the Object End. By moving till " or }.
     * @param parser
     * @return flag
     * @throws IOException
     */
    private static boolean endOfObject(final Parser
                                               parser) throws IOException {
        char character;
        if (parser.getCursor() == ',') {
            while (parser.getCharacter() != '"') {
                continue;
            }
            return false;
        }

        if (parser.getCursor() == '}') {
            return true;
        }

        while ((character = parser.getCharacter()) != '"'
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
