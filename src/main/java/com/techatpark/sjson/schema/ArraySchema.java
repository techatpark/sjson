package com.techatpark.sjson.schema;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Map;

public class ArraySchema extends JsonSchema<Arrays> {
    /**
     * Constructor for JsonSchema based on type.
     *
     * @param schemaAsMap
     */
    ArraySchema(final Map<String, Object> schemaAsMap) {
        super(schemaAsMap);
    }

    @Override
    public final Arrays read(final Reader reader) throws IOException {
        return null;
    }


}
