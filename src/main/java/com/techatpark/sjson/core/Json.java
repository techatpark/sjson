package com.techatpark.sjson.core;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.techatpark.sjson.core.parser.ArrayParser.getArray;
import static com.techatpark.sjson.core.parser.BooleanParser.getFalse;
import static com.techatpark.sjson.core.parser.BooleanParser.getTrue;
import static com.techatpark.sjson.core.parser.NullParser.getNull;
import static com.techatpark.sjson.core.parser.ObjectParser.getObject;
import static com.techatpark.sjson.core.util.ReaderUtil.ILLEGAL_JSON_VALUE;
import static com.techatpark.sjson.core.util.ReaderUtil.nextClean;
import static com.techatpark.sjson.core.parser.StringParser.getString;
import static com.techatpark.sjson.core.parser.NumberParser.getNumber;

/**
 * Json parser for server side workloads.
 * It tries to get optimized memory and performance with below goals.
 * <p>
 * 1. Represent Json in native format.
 * 2. No external dependencies
 * 3. Trust the validity of json documents
 * 4. It is just enough to say invalid, reasoning is optional
 * <p>
 * Note:
 * This is not general purpose parser. This is useful for Microservices
 * and REST Clients where we primarily need to read/write json data.
 */
public final class Json {

    /**
     * Length of Unicode.
     */
    private static final int UNICODE_LENGTH = 4;

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
     * @param reader - file reader
     * @return object
     * @throws IOException - throws io exception
     */
    public Object read(final Reader reader) throws IOException {
        try (reader) {
            return new ContentExtractor(reader).getValue();
        }
    }

    /**
     * Get Json text for the Map.
     *
     * @param jsonMap
     * @return jsonText
     */
    public String jsonText(final Map<String, Object> jsonMap) {
        return "{" + jsonMap.entrySet().stream()
                .map(entry ->
                        "\"" + escapeJsonTxt(entry.getKey()) + "\":"
                        + getValue(entry.getValue()
                        )).collect(Collectors.joining(",")) + "}";
    }

    /**
     * Get Json Array Text for the List.
     *
     * @param jsonArray
     * @return jsonTxt
     */
    public String jsonText(final List<Object> jsonArray) {
        return "[" + jsonArray.stream()
                .map(o -> getValue(o))
                .collect(Collectors.joining(",")) + "]";
    }

    /**
     * Create Value in according to the Type.
     *
     * @param value
     * @return valueText
     */
    private String getValue(final Object value) {
        return switch (value) {
            case null -> "null";
            case String str -> "\"" + escapeJsonTxt(str) + "\"";
            case Map map -> jsonText(map);
            case List list -> jsonText(list);
            default -> value.toString();
        };
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
                        for (int k = 0; k < UNICODE_LENGTH
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

    /**
     * ContentExtractor is responsible to interact with underlying reader to
     * extract the content.
     */
    public static final class ContentExtractor {

        /**
         * Reader to the JSON Content.
         */
        private final Reader reader;

        /**
         * This holds current character from reader.
         */
        private char cursor;

        /**
         * Creates Content extracter for the reader.
         *
         * @param theReader
         */
        public ContentExtractor(final Reader theReader) {
            this.reader = theReader;
        }

        /**
         * Entry Method for extraction. This will
         * 1. move to the first clean character to determine the Data type
         * 2. Call corresponding get methods based on the type
         *
         * @return object
         * @throws IOException
         */
        public Object getValue() throws IOException {
            // 1. move to the first clean character to determine the Data type
            final char character = nextClean(reader);
            setCursor(character);
            // 2. Call corresponding get methods based on the type
            return switch (character) {
                case '"' -> getString(reader);
                case 'n' -> getNull(reader);
                case 't' -> getTrue(reader);
                case 'f' -> getFalse(reader);
                case '{' -> getObject(reader, this);
                case '[' -> getArray(reader, this);
                case ']' -> this;
                default -> {
                    if (Character.isDigit(character)
                            || character == '+' || character == '-') {
                        yield getNumber(this, reader, character);
                    }
                    throw new IllegalArgumentException(ILLEGAL_JSON_VALUE);
                }
            };
        }

        /**
         * Sets Cursor at given Character.
         * @param character
         */
        public void setCursor(final char character) {
            cursor = character;
        }

        /**
         * Gets Cursor.
         * @return cursor
         */
        public char getCursor() {
            return cursor;
        }
    }
}
