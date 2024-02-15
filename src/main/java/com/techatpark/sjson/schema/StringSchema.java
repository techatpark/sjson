package com.techatpark.sjson.schema;

import com.techatpark.sjson.core.util.NullParser;
import com.techatpark.sjson.core.util.ReaderUtil;
import com.techatpark.sjson.core.util.StringParser;

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
        char nextClean = ReaderUtil.nextClean(reader);
        if (nextClean == '"') {
            return StringParser.getString(reader);
        } else if (nextClean == 'n') {
            return (String) NullParser.getNull(reader);
        }
        throw new IllegalArgumentException("Invalid String");

    }

    /** Description of something. */
    @Override
    public String toString() {
        return new StringBuilder("{\"type\":\"string\",")
                .append("\"$schema\":\"" + this.getSchema() + "\"}")
                .toString();
    }
}
