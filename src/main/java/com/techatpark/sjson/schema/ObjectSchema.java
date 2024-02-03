package com.techatpark.sjson.schema;

import java.util.Map;

public class ObjectSchema extends JsonSchema {
    /**
     * Constructor for JsonSchema based on type.
     *
     * @param schemaAsMap
     */
    ObjectSchema(final Map<String, Object> schemaAsMap) {
        super(schemaAsMap);
    }
}
