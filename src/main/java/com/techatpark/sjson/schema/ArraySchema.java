package com.techatpark.sjson.schema;

import java.util.Map;

public class ArraySchema extends JsonSchema {
    /**
     * Constructor for JsonSchema based on type.
     *
     * @param schemaAsMap
     */
    ArraySchema(final Map<String, Object> schemaAsMap) {
        super(schemaAsMap);
    }
}
