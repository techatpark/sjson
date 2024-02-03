package com.techatpark.sjson.schema;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

public class ObjectSchema extends JsonSchema<Object> {
    /**
     * Constructor for JsonSchema based on type.
     *
     * @param schemaAsMap
     */
    ObjectSchema(final Map<String, Object> schemaAsMap) {
        super(schemaAsMap);
    }

    @Override
    public final Object read(final Reader reader) throws IOException {
        return null;
    }
}
