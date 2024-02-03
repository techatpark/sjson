package com.techatpark.sjson.schema;

import jakarta.validation.constraints.Null;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

public class NullSchema extends JsonSchema<Null> {
    /**
     * Constructor for JsonSchema based on type.
     *
     * @param schemaAsMap
     */
    NullSchema(final Map<String, Object> schemaAsMap) {
        super(schemaAsMap);
    }

    @Override
    public final Null read(final Reader reader) throws IOException {
        return null;
    }
}
