package com.techatpark.sjson.schema;

import java.util.Map;

public class NullSchema extends JsonSchema {
    /**
     * Constructor for JsonSchema based on type.
     *
     * @param schemaAsMap
     */
    NullSchema(final Map<String, Object> schemaAsMap) {
        super(schemaAsMap);
    }
}
