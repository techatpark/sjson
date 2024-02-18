package com.techatpark.sjson.core;

import com.techatpark.sjson.core.Parser.NumberParser;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.techatpark.sjson.core.Parser.BooleanParser.getFalse;
import static com.techatpark.sjson.core.Parser.BooleanParser.getTrue;
import static com.techatpark.sjson.core.Parser.NullParser.getNull;
import static com.techatpark.sjson.core.util.ReaderUtil.isSpace;
import static com.techatpark.sjson.core.util.ReaderUtil.nextClean;
import static com.techatpark.sjson.core.Parser.StringParser.getString;
import static com.techatpark.sjson.core.Parser.StringParser.getCharacter;


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
     * Capacity.
     */
    private static final int CAPACITY = 10;

    /**
     * Number 0.
     */
    private static final int ZERO = 0;

    /**
     * For invalid JSON.
     */
    private static final String ILLEGAL_JSON_VALUE = "Illegal value at ";

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
            valueText(builder, entry.getValue());
        }
        return builder.append("}").toString();
    }

    /**
     * Get Json Array Text for the List.
     *
     * @param jsonArray
     * @return jsonTxt
     */
    public String jsonText(final List<Object> jsonArray) {
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for (Object value: jsonArray) {
            if (isFirst) {
                isFirst = false;
            } else {
                builder.append(",");
            }
            valueText(builder, value);
        }
        return builder.append("]").toString();
    }

    /**
     * Create Value in according to the Type.
     *
     * @param builder
     * @param value
     */
    private void valueText(final StringBuilder builder, final Object value) {
        switch (value) {
            case null -> builder.append("null");
            case String str -> processString(builder, str);
            case Map map -> builder.append(jsonText(map));
            case List list -> builder.append(jsonText(list));
            default -> builder.append(value);
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
        builder.append("\"").append(escapeJsonTxt(value)).append("\"");
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
                        for (int k = 0; k < NumberParser.FOUR
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
    private static final class ContentExtractor {

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
        private ContentExtractor(final Reader theReader) {
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
        private Object getValue() throws IOException {
            // 1. move to the first clean character to determine the Data type
            final char character = moveCursorToNextClean();
            // 2. Call corresponding get methods based on the type
            return switch (character) {
                case '"' -> getString(reader);
                case 'n' -> getNull(reader);
                case 't' -> getTrue(reader);
                case 'f' -> getFalse(reader);
                case '{' -> getObject();
                case '[' -> getArray();
                case ']' -> this;
                default -> getNumber(character);
            };
        }

        /**
         * Reads the number from reader.
         * Reader will stop at the next to the end of number.
         *
         * @param startingChar
         * @return number
         * @throws IOException
         */
        private Number getNumber(final char startingChar) throws IOException {

            final StringBuilder builder = new StringBuilder(10);
            char character;

            // Happy Case : Read AllDigits before . character
            while ((character = (char) reader.read()) != ','
                    && Character.isDigit(character)
                    && character != '.'
                    && character != '}'
                    && character != ']'
                    && character != 'e'
                    && character != 'E'
                    && !isSpace(character)) {
                builder.append(character);
            }

            // Maybe a double ?!
            if (character == '.' || character == 'e' || character == 'E') {
                // Decimal Number
                if (character == '.') {
                    StringBuilder decimals = new StringBuilder(CAPACITY);
                    while ((character = (char) reader.read()) != ','
                            && (Character.isDigit(character)
                            || character == '-'
                            || character == '+'
                            || character == 'e'
                            || character == 'E')
                            && character != '}'
                            && character != ']'

                            && !isSpace(character)) {
                        decimals.append(character);
                    }
                    setCursor(character);
                    return getDecimalNumber(startingChar, builder, decimals);
                } else { // Exponential Non Decimal Number
                    builder.append(character);
                    while ((character = (char) reader.read()) != ','
                            && (Character.isDigit(character)
                            || character == '-'
                            || character == '+'
                            || character == 'e'
                            || character == 'E')
                            && character != '}'
                            && character != ']'
                            && !isSpace(character)) {
                        builder.append(character);
                    }
                    setCursor(character);
                    return getExponentialNumber(startingChar, builder);
                }
            } else {
                setCursor(character);
                return getNumber(startingChar, builder);
            }
        }

        /**
         * Gets Number from the String.
         *
         * @param startingChar
         * @param builder
         * @return number
         */
        private Number getNumber(final char startingChar,
                                 final StringBuilder builder) {
            return switch (startingChar) {
                case '-' -> NumberParser.parseNumber(builder.toString(), true);
                case '+' -> NumberParser.parseNumber(builder.toString(), false);
                default -> NumberParser.parseNumber(builder
                        .insert(0, startingChar)
                        .toString(), false);
            };
        }

        /**
         * Gets Decimal Exponential from the String.
         *
         * @param startingChar
         * @param builder
         * @return number
         */
        private Number getExponentialNumber(final char startingChar,
                                            final StringBuilder builder) {
            return new BigDecimal(startingChar + builder.toString());
        }

        /**
         * Gets Decimal Number from the String.
         *
         * @param decimal
         * @param startingChar
         * @param builder
         * @return number
         */
        private Number getDecimalNumber(final char startingChar,
                                        final StringBuilder builder,
                                        final StringBuilder decimal) {
            return NumberParser.parseDecimalNumber(startingChar
                    + builder.toString() + "." + decimal.toString());
        }







        /**
         * Reads Object from a reader. Reader will
         * stop at the next clean char after object.
         *
         * @return json object
         * @throws IOException
         */
        private Map<String, Object> getObject() throws IOException {
            boolean eoo = endOfObject();
            // This is Empty Object
            if (eoo) {
                moveCursorToNextClean();
                return Collections.emptyMap();
            }
            final Map<String, Object> jsonMap = new HashMap<>();
            while (!eoo) {
                jsonMap.put(getKey(), getValue());
                eoo = endOfObject();
            }
            moveCursorToNextClean();
            return jsonMap;
        }

        /**
         * Read Key as a String. It gets key from String Pool.
         *
         * @return key
         * @throws IOException
         */
        private String getKey() throws IOException {
            final String key = getString(reader).intern();
            moveCursorToNextClean();
            return key;
        }

        /**
         * Reades an Array. Reader stops at next clean character.
         *
         * @return list
         * @throws IOException
         */
        private List<Object> getArray() throws IOException {
            final Object value = getValue();
            // If not Empty Array
            if (value == this) {
                moveCursorToNextClean();
                return Collections.emptyList();
            }
            final List<Object> list = new ArrayList<>();
            list.add(value);
            while (!endOfArray()) {
                list.add(getValue());
            }
            moveCursorToNextClean();
            return list;
        }

        /**
         * Skip Spaces and land reader at the valid character.
         *
         * @return valid character
         * @throws IOException
         */
        private char moveCursorToNextClean() throws IOException {
            char character = nextClean(this.reader);
            setCursor(character);
            return character;
        }



        /**
         * Determines the Object End. By moving till " or }.
         *
         * @return flag
         * @throws IOException
         */
        private boolean endOfObject() throws IOException {
            char character;
            if (cursor == '}') {
                return true;
            }
            if (cursor == ',') {
                while (getCharacter(this.reader.read()) != '"') {
                    continue;
                }
                return false;
            }
            while ((character = getCharacter(this.reader.read())) != '"'
                    && character != '}') {
                continue;
            }
            return character == '}';
        }

        /**
         * Determine array close character.
         *
         * @return flag
         * @throws IOException
         */
        private boolean endOfArray() throws IOException {
            char character;
            if (cursor == ']') {
                return true;
            }
            if (cursor == ',') {
                return false;
            }
            while ((character = getCharacter(this.reader.read())) != ','
                    && character != ']') {
                continue;
            }
            return character == ']';
        }
        /**
         * Sets Cursor at given Character.
         * @param character
         */
        public void setCursor(final Character character) {
            cursor = character;
        }


    }
}
