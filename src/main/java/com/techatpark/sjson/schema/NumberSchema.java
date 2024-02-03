package com.techatpark.sjson.schema;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

public class NumberSchema extends JsonSchema<Number> {
    /**
     * Constructor for JsonSchema based on type.
     *
     * @param schemaAsMap
     */
    NumberSchema(final Map<String, Object> schemaAsMap) {
        super(schemaAsMap);
    }

    @Override
    public final Number read(final Reader reader) throws IOException {
        return null;
    }
}
