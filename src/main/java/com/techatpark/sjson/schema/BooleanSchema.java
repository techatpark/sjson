package com.techatpark.sjson.schema;

import com.techatpark.sjson.core.util.BooleanParser;
import com.techatpark.sjson.core.util.ReaderUtil;

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
        char nextClean = ReaderUtil.nextClean(reader);
        if (nextClean == 't') {
            return BooleanParser.getTrue(reader);
        } else if (nextClean == 'f') {
            return BooleanParser.getFalse(reader);
        }
        throw new IllegalArgumentException("Not Boolean");
    }

    /** Description of something. */
    @Override
    public String toString() {
        return "{\"type\":\"boolean\",\"$schema\":\""
                + this.getSchema() + "\"}";
    }
}
