package com.techatpark.sjson.core;

import com.techatpark.sjson.core.util.NumberParser;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * Length for hex char.
     */
    private static final int LENGTH = 4;

    /**
     * Radix for hex char.
     */
    private static final int RADIX = 16;

    /**
     * Capacity.
     */
    private static final int CAPACITY = 10;



    /**
     * Number 0.
     */
    private static final int NUMBER_ZERO = 0;

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

            Object value = entry.getValue();

            valueText(builder, value);
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
        if (value == null) {
            builder.append("null");
        } else if (value instanceof String) {
            processString(builder, (String) value);
        } else if (value instanceof Map) {
            builder.append(jsonText((Map<String, Object>)
                    value));
        } else if (value instanceof List) {
            builder.append(jsonText((List)
                    value));
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

    /**
     * ContentExtractor is responsible to interact with underlying reader to
     * extract the content.
     */
    private final class ContentExtractor {

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
            final char character = nextClean();
            // 2. Call corresponding get methods based on the type
            return switch (character) {
                case '"' -> getString();
                case 'n' -> getNull();
                case 't' -> getTrue();
                case 'f' -> getFalse();
                case '{' -> getObject();
                case '[' -> getArray();
                case ']' -> this;
                default -> getNumber(character);
            };
        }

        /**
         * Reads String from Reader. Reader will stop at the " symbol
         *
         * @return string
         * @throws IOException
         */
        private String getString() throws IOException {
            final StringBuilder sb = new StringBuilder();
            char character;

            while ((character = getCharacter(reader.read())) != '\\'
                    && character != '"') {
                sb.append(character);
            }

            // Normal String
            if (character == '"') {
                return sb.toString();
            }

            // String with escape characters ?!
            for (;;) {
                switch (character) {
                    case 0,'\n','\r':
                        throw new IllegalArgumentException(ILLEGAL_JSON_VALUE);
                    case '\\':
                        character = getCharacter(reader.read());
                        switch (character) {
                            case 'b':
                                sb.append('\b');
                                break;
                            case 't':
                                sb.append('\t');
                                break;
                            case 'n':
                                sb.append('\n');
                                break;
                            case 'f':
                                sb.append('\f');
                                break;
                            case 'r':
                                sb.append('\r');
                                break;
                            case 'u':
                                sb.append((char) Integer
                                        .parseInt(new String(next(LENGTH)),
                                                RADIX));
                                break;
                            case '"':
                            case '\'':
                            case '\\':
                            case '/':
                                sb.append(character);
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        ILLEGAL_JSON_VALUE);
                        }
                        break;
                    default:
                        if (character == '"') {
                            return sb.toString();
                        }
                        sb.append(character);
                }
                character = getCharacter(reader.read());
            }
        }

        /**
         * get Character.
         * @throws IllegalArgumentException if EOF
         * @param value
         * @return char value
         */
        private char getCharacter(final int value) {
            if (value == -1) {
                throw new IllegalArgumentException(ILLEGAL_JSON_VALUE);
            }
            return (char) value;
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
            switch (startingChar) {
                case '-':
                    return NumberParser.parseNumber(builder.toString(), true);
                case '+':
                    return NumberParser.parseNumber(builder.toString(), false);
                default:
                    return NumberParser.parseNumber(builder
                            .insert(0, startingChar)
                            .toString(), false);
            }
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
         * Reads True from Reader. Reader will stip at the "e" symbol.
         *
         * @return string
         * @throws IOException
         */
        private boolean getTrue() throws IOException {
            char[] charBuffer = next(NumberParser.NUMBER_THREE);
            if (charBuffer[0] == 'r'
                    && charBuffer[1] == 'u'
                    && charBuffer[2] == 'e') {
                // cursor = 'e';
                return true;
            } else {
                throw new IllegalArgumentException(ILLEGAL_JSON_VALUE);
            }
        }

        /**
         * Reads False from Reader. Reader will strip at the "e" symbol.
         *
         * @return string
         * @throws IOException
         */
        private boolean getFalse() throws IOException {
            char[] charBuffer = next(NumberParser.NUMBER_FOUR);
            if (charBuffer[NUMBER_ZERO] == 'a'
                    && charBuffer[NumberParser.NUMBER_ONE] == 'l'
                    && charBuffer[NumberParser.NUMBER_TWO] == 's'
                    && charBuffer[NumberParser.NUMBER_THREE] == 'e') {
                // cursor = 'e';
                return false;
            } else {
                throw new IllegalArgumentException(ILLEGAL_JSON_VALUE);
            }
        }

        /**
         * Reads Null from Reader. Reader will stip at the "l" symbol.
         *
         * @return string
         * @throws IOException
         */
        private Object getNull() throws IOException {
            char[] charBuffer = next(NumberParser.NUMBER_THREE);
            if (charBuffer[NUMBER_ZERO] == 'u'
                    && charBuffer[NumberParser.NUMBER_ONE] == 'l'
                    && charBuffer[NumberParser.NUMBER_TWO] == 'l') {
                // cursor = 'l';
                return null;
            } else {
                throw new IllegalArgumentException(ILLEGAL_JSON_VALUE);
            }
        }

        /**
         * Reads next chars for given length
         * from the reader and fill an char array.
         *
         * @param length
         * @return char array
         * @throws IOException
         */
        private char[] next(final int length) throws IOException {
            char[] cbuf = new char[length];
            reader.read(cbuf, NUMBER_ZERO, length);
            return cbuf;
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
                nextClean();
                return Collections.emptyMap();
            }
            final Map<String, Object> jsonMap = new HashMap<>();
            while (!eoo) {
                jsonMap.put(getKey(), getValue());
                eoo = endOfObject();
            }
            nextClean();
            return Collections.unmodifiableMap(jsonMap);
        }

        /**
         * Read Key as a String. It gets key from String Pool.
         *
         * @return key
         * @throws IOException
         */
        private String getKey() throws IOException {
            final String key = getString().intern();
            nextClean();
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
                nextClean();
                return Collections.emptyList();
            }
            final List<Object> list = new ArrayList<>();
            list.add(value);
            boolean eoa = endOfArray();
            while (!eoa) {
                list.add(getValue());
                eoa = endOfArray();
            }
            nextClean();
            return Collections.unmodifiableList(list);
        }


        /**
         * Skip Spaces and land reader at the valid character.
         *
         * @return valid character
         * @throws IOException
         */
        private char nextClean() throws IOException {
            char character;
            do {
                character = (char) this.reader.read();
            } while (isSpace(character));
            setCursor(character);
            return cursor;
        }

        /**
         * Determines if this is a space charecter.
         *
         * @param character
         * @return flag
         */
        private boolean isSpace(final char character) {
            return (character == ' '
                    || character == '\n'
                    || character == '\r'
                    || character == '\t');
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
        private void setCursor(final Character character) {
            cursor = character;
        }


    }
}
