package com.techatpark.sjson.schema;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

public class StringSchema extends JsonSchema<String> {
    /**
     * Constructor for JsonSchema based on type.
     *
     * @param schemaAsMap
     */
    StringSchema(final Map<String, Object> schemaAsMap) {
        super(schemaAsMap);
    }

    @Override
    public final String read(final Reader reader) throws IOException {
        return null;
    }
}
