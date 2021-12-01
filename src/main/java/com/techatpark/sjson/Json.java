package com.techatpark.sjson;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Json parser for server side workloads.
 * It tries to get optimized memory and performance with below goals.
 *
 * 1. Trust the validity of json documents
 * 2. It is just enough to say invalid, reasoning is optional
 * 3. Represent Json in native format.
 * 4. No external dependencies
 *
 * Note:
 * This is not general purpose parser. This is useful for Microservices and REST Clients
 * where we primarily need to read/write json data.
 */
public final class Json {

    /**
     * Reads JSON as a Java Object.
     *
     * It will return native java objects as given below based on JSON Data Type.
     * Ref: https://www.w3schools.com/js/js_json_datatypes.asp
     *
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
     * ContentExtractor is responsible to interact with underlying reader to
     * extract the content.
     */
    private final class ContentExtractor {

        /**
         * Reader to the JSON Content.
         */
        private final Reader reader;

        /**
         * This holds current character from reader
         */
        private char current;

        /**
         * Creates Content extracter for the reader
         * @param theReader
         */
        private ContentExtractor(final Reader theReader) {
            this.reader = theReader;
        }

        /**
         * Entry Method for extraction. This will
         *      1. move to the first clean character to determine the Data type
         *      2. Call corresponding get methods based on the type
         * @return
         * @throws IOException
         */
        private Object getValue() throws IOException {
            // 1. move to the first clean character to determine the Data type
            final char character = nextClean();
            // 2. Call corresponding get methods based on the type
            switch (character) {
                case '"':
                    return getString();
                case 'n':
                    return getNull();
                case 't':
                    return getTrue();
                case 'f':
                    return getFalse();
                case '{':
                    return getObject();
                case '[':
                    return getArray();
                case ']':
                    return this;
                default:
                    return getNumber(character);
            }
        }

        /**
         * Reads String from Reader. Reader will stop at the " symbol
         * @return string
         * @throws IOException
         */
        private String getString() throws IOException {
            final StringBuilder sb = new StringBuilder();
            char character;

            while ((character = (char) reader.read()) != '\\'
                    && character != '"') {
                sb.append(character);
            }

            // Normal String
            if(character == '"') {
                current = character;
                return sb.toString();
            }

            // String with escape characters ?!
            for (; ; ) {

                switch (character) {
                    case 0:
                    case '\n':
                    case '\r':
                        throw new IllegalArgumentException("Invalid Token at ");
                    case '\\':
                        character = (char) reader.read();
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
                                sb.append((char) Integer.parseInt(new String(next(4)), 16));
                                break;
                            case '"':
                            case '\'':
                            case '\\':
                            case '/':
                                sb.append(character);
                                break;
                            default:
                                throw new IllegalArgumentException("Invalid Token at ");
                        }
                        break;
                    default:
                        if (character == '"') {
                            current = character;
                            return sb.toString();
                        }
                        sb.append(character);
                }
                character = (char) reader.read();
            }
        }

        /**
         * Reads the number from reader. Reader will stop at the next to the end of number.
         * @param startingChar
         * @return
         * @throws IOException
         */
        private Number getNumber(final char startingChar) throws IOException {

            final StringBuilder builder = new StringBuilder(10);
            builder.append(startingChar);
            char character;

            // Happy Case : Read AllDigits before . character
            while ( ( character = (char) reader.read()) != ','
                    && character != '.'
                    && character != '}'
                    && character != ']'
                    && character != 'e'
                    && character != 'E'
                    && !isSpace(character)) {
                builder.append(character);
            }

            // May be a double ?!
            if(character == '.' || character == 'e' || character == 'E' ) {
                // Decimal Number
                if(character == '.') {
                    StringBuilder decimals = new StringBuilder(10);
                    while ( ( character = (char) reader.read()) != ','
                            && character != '}'
                            && character != ']'
                            && !isSpace(character)) {
                        decimals.append(character);
                    }
                    current = character;
                    return getDecimalNumber(builder,decimals);
                }
                // Exponential Non Decimal Number
                else {
                    builder.append(character);
                    while ( ( character = (char) reader.read()) != ','
                            && character != '}'
                            && character != ']'
                            && !isSpace(character)) {
                        builder.append(character);
                    }
                    current = character;
                    return getExponentialNumber(builder);
                }

            } else {
                current = character;
                return getNumber(builder);
            }

        }

        /**
         * Gets Number from the String.
         * @param builder
         * @return
         */
        private Number getNumber(final StringBuilder builder) {
            if(builder.length() < 3) {
                return Byte.parseByte(builder.toString());
            }
            if(builder.length() < 5) {
                return Short.parseShort(builder.toString());
            }
            if(builder.length() < 10) {
                return Integer.parseInt(builder.toString());
            }
            if(builder.length() < 19) {
                return Long.parseLong(builder.toString());
            }
            return new BigInteger(builder.toString());
        }

        /**
         * Gets Decimal Exponential from the String
         * @param builder
         * @return
         */
        private Number getExponentialNumber(final StringBuilder builder) {
            return new BigDecimal(builder.toString());
        }

        /**
         * Gets Decimal Number from the String
         * @param builder
         * @return
         */
        private Number getDecimalNumber(final StringBuilder builder,final StringBuilder decimal) {
            return new BigDecimal(builder.toString() + "." + decimal.toString());
        }

        /**
         * Reads True from Reader. Reader will stip at the "e" symbol
         * @return string
         * @throws IOException
         */
        private boolean getTrue() throws IOException {
            char[] charBuffer = next(3);
            if (charBuffer[0] == 'r'
                    && charBuffer[1] == 'u'
                    && charBuffer[2] == 'e') {
                current = 'e';
                return true;
            } else {
                throw new IllegalArgumentException("Illegal value at ");
            }
        }

        /**
         * Reads False from Reader. Reader will stip at the "e" symbol
         * @return string
         * @throws IOException
         */
        private boolean getFalse() throws IOException {
            char[] charBuffer = next(4);
            if (charBuffer[0] == 'a'
                    && charBuffer[1] == 'l'
                    && charBuffer[2] == 's'
                    && charBuffer[3] == 'e') {
                current = 'e';
                return false;
            } else {
                throw new IllegalArgumentException("Illegal value at ");
            }
        }

        /**
         * Reads Null from Reader. Reader will stip at the "l" symbol
         * @return string
         * @throws IOException
         */
        private Object getNull() throws IOException {
            char[] charBuffer = next(3);
            if (charBuffer[0] == 'u'
                    && charBuffer[1] == 'l'
                    && charBuffer[2] == 'l') {
                current = 'l';
                return null;
            } else {
                throw new IllegalArgumentException("Illegal value at ");
            }
        }

        /**
         * Reads next chars for given length from the reader and fill an char array
         * @param length
         * @return char array
         * @throws IOException
         */
        private char[] next(final int length) throws IOException {
            char[] cbuf = new char[length];
            reader.read(cbuf,0,length);
            return cbuf;
        }

        /**
         * Reads Object from a reader. Reader will stop at the next clean char after object
         *
         * @return json object
         * @throws IOException
         */
        private Map<String, Object> getObject() throws IOException {

            boolean eoo = endOfObject();
            // This is Empty Object
            if (eoo) {
                nextClean();
                return Collections.EMPTY_MAP;
            }
            final Map<String, Object> jsonMap = new HashMap<>();
            while (!eoo) {
                jsonMap.put(getKey(),getValue());
                eoo = endOfObject();
            }
            nextClean();
            return Collections.unmodifiableMap(jsonMap);
        }

        /**
         * Read Key as a String. It gets key from String Pool
         * @return key
         * @throws IOException
         */
        private String getKey() throws IOException {
            String key = getString().intern();
            nextClean();
            return key;
        }

        /**
         * Reades an Array. Reader stops at next clean character
         * @return
         * @throws IOException
         */
        private List getArray() throws IOException {
            final Object value = getValue();
            // If not Empty Array
            if (value == this) {
                nextClean();
                return Collections.EMPTY_LIST;
            }
            final List list = new ArrayList();
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
         * Skip Spaces and land reader at the valid chartor
         * @return valid character
         * @throws IOException
         */
        private char nextClean() throws IOException {
            char character;
            while (isSpace (character = (char) this.reader.read())) {
            }
            current = character;
            return character;
        }

        /**
         * Determines if this is a space charecter
         * @param character
         * @return
         */
        private boolean isSpace(final char character) {
            return (character == ' '
                    || character == '\n'
                    || character == '\r'
                    || character == '\t');
        }

        /**
         * Determines the Object End. By moving till " or }
         * @return
         * @throws IOException
         */
        private boolean endOfObject() throws IOException {
            char character;
            if(current == '}') {
                return true;
            }
            if(current == ',') {
                while (this.reader.read() != '"') {
                }
                return false;
            }
            while ((character = (char) this.reader.read()) != '"'
                    && character != '}') {
            }
            return character == '}';
        }

        /**
         * Determine array close character.
         * @return
         * @throws IOException
         */
        private boolean endOfArray() throws IOException {
            char character;
            if(current == ']') {
                return true;
            }
            if(current == ',') {
                return false;
            }
            while ((character = (char) this.reader.read()) != ','
                    && character != ']') {
            }
            return character == ']';
        }

    }
}
