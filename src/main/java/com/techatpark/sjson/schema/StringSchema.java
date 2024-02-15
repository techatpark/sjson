package com.techatpark.sjson.schema;

import com.techatpark.sjson.core.util.NullParser;
import com.techatpark.sjson.core.util.ReaderUtil;
import com.techatpark.sjson.core.util.StringParser;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

public class StringSchema extends JsonSchema<Object> {
    /**
     * Constructor for JsonSchema based on type.
     *
     * @param schemaAsMap
     */
    StringSchema(final Map<String, Object> schemaAsMap) {
        super(schemaAsMap);
    }

    @Override
    public final Object read(final Reader reader) throws IOException {
        if (ReaderUtil.nextClean(reader) == '"') {
            return StringParser.getString(reader);
        } else if (ReaderUtil.nextClean(reader) == 'n') {
            return NullParser.getNull(reader);
        }

        throw new IllegalArgumentException("Invaild String");

    }

    /** Description of something. */
    @Override
    public String toString() {
        return new StringBuilder("{\"type\":\"string\",")
                .append("\"$schema\":\"" + this.getSchema() + "\"}")
                .toString();
    }
}
