package com.techatpark.sjson;

import com.techatpark.sjson.util.NumberParser;



import java.io.IOException;
import java.io.Reader;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;

/**
 * Json Schema that represents the schema document.
 * Ref: https://json-schema.org/specification
 */
public class JsonSchema {

    /**
     * @param reader file reader for json schema
     */
    public JsonSchema(final Reader reader) {

    }

    /**
     * Reads JSON as a Java Object.
     * <p>
     * It will return native java objects as given below based
     * on JSON Data Type.
     * Ref: https://www.w3schools.com/js/js_json_datatypes.asp
     * <p>
     * string - java.lang.String
     * number - java.lang.Number
     * object - java.util.Map
     * array  - java.util.List
     * boolean - java.lang.Boolean
     * null - null
     *
     * @param reader - file reader for json data
     * @return object
     * @throws IOException - throws io exception
     */
    public Object read(final Reader reader) throws IOException {
        return null;
    }

    /**
     * Get Json text for the Map.
     *
     * @param jsonMap
     * @return jsonText
     */
    public String jsonText(final Map<String, Object> jsonMap) {
        StringBuilder builder = new StringBuilder();
        boolean isFirst = true;
        builder.append("{");
        for (Map.Entry<String, Object> entry : jsonMap.entrySet()) {
            if (isFirst) {
                isFirst = false;
            } else {
                builder.append(",");
            }
            // Create Key enclosed with "
            builder.append("\"")
                    .append(escapeJsonTxt(entry.getKey()))
                    .append("\":"); // Create Key value separator

            Object value = entry.getValue();

            valueText(builder, value);
        }
        return builder.append("}").toString();
    }

    /**
     * Create Value in according to the Type.
     *
     * @param builder
     * @param value
     */
    private void valueText(final StringBuilder builder, final Object value) {
        if (value == null) {
            builder.append("null");
        } else if (value instanceof String) {
            processString(builder, (String) value);
        } else if (value instanceof Map) {
            builder.append(jsonText((Map<String, Object>)
                    value));
        } else if (value instanceof List) {
            builder.append(jsonText((Map<String, Object>) value));
        } else {
            builder.append(value);
        }
    }


    /**
     * Process String.
     *
     * @param builder
     * @param value
     */
    private void processString(final StringBuilder builder,
                               final String value) {
        builder.append("\"")
                .append(escapeJsonTxt(value))
                .append("\"");
    }

    /**
     * Escape JSON Text.
     * Escape quotes, \, /, \r, \n, \b, \f, \t
     * and other control characters (U+0000 through U+001F).
     * @param s
     * @return escapeJsonTxt
     */
    private String escapeJsonTxt(final String s) {
        if (s == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        escape(s, sb);
        return sb.toString();
    }

    /**
     * Escape Text.
     * @param s - Must not be null.
     * @param sb
     */
    private void escape(final String s, final StringBuilder sb) {
        final int len = s.length();
        for (int i = 0; i < len; i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '/':
                    sb.append("\\/");
                    break;
                default:
                    //Reference: http://www.unicode.org/versions/Unicode5.1.0/
                    if ((ch >= '\u0000' && ch <= '\u001F')
                            || (ch >= '\u007F' && ch <= '\u009F')
                            || (ch >= '\u2000' && ch <= '\u20FF')) {
                        String ss = Integer.toHexString(ch);
                        sb.append("\\u");
                        for (int k = 0; k < NumberParser.NUMBER_FOUR
                                - ss.length(); k++) {
                            sb.append('0');
                        }
                        sb.append(ss.toUpperCase());
                    } else {
                        sb.append(ch);
                    }
            }
        }
    }


}