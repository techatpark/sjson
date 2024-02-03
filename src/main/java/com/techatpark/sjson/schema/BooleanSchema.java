package com.techatpark.sjson.schema;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

public class BooleanSchema extends JsonSchema<Boolean> {
    /**
     * Constructor for JsonSchema based on type.
     *
     * @param schemaAsMap
     */
    BooleanSchema(final Map<String, Object> schemaAsMap) {
        super(schemaAsMap);
    }

    @Override
    public final Boolean read(final Reader reader) throws IOException {
        return null;
    }
}
