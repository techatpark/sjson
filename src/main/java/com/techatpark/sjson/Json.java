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
 * <p>
 * 1. Trust the validity of json documents
 * 2. It is just enough to say invalid, reasoning is optional
 * 3. Represent Json in native format.
 * 4. No external dependencies
 * <p>
 * Note:
 * This is not general purpose parser. This is useful for Microservices
 * and REST Clients where we primarily need to read/write json data.
 */
public final class Json {

    /**
     * Length for hex char.
     */
    public static final int LENGTH = 4;

    /**
     * Radix for hex char.
     */
    public static final int RADIX = 16;

    /**
     * Capacity.
     */
    public static final int CAPACITY = 10;

    /**
     * Max length A Byte can accommodate.
     */
    public static final int BYTE_LENGTH = 2;

    /**
     * Max length A Short can accommodate.
     */
    public static final int SHORT_LENGTH = 4;

    /**
     * Max length A Integer can accommodate.
     */
    public static final int INTEGER_LENGTH = 9;

    /**
     * Max length A Long can accommodate.
     */
    public static final int LONG_LENGTH = 18;

    /**
     * Number 0.
     */
    public static final int NUMBER_ZERO = 0;
    /**
     * Number 1.
     */
    public static final int NUMBER_ONE = 1;
    /**
     * Number 2.
     */
    public static final int NUMBER_TWO = 2;
    /**
     * Number 3.
     */
    public static final int NUMBER_THREE = 3;
    /**
     * Number 4.
     */
    public static final int NUMBER_FOUR = 4;
    /**
     * Number 5.
     */
    public static final int NUMBER_FIVE = 5;
    /**
     * Number 6.
     */
    public static final int NUMBER_SIX = 6;
    /**
     * Number 7.
     */
    public static final int NUMBER_SEVEN = 7;
    /**
     * Number 8.
     */
    public static final int NUMBER_EIGHT = 8;
    /**
     * Number 9.
     */
    public static final int NUMBER_NINE = 9;
    /**
     * Number 10.
     */
    public static final int NUMBER_TEN = 10;
    /**
     * Number 11.
     */
    public static final int NUMBER_ELEVEN = 11;
    /**
     * Number 12.
     */
    public static final int NUMBER_TWELVE = 12;
    /**
     * Number 13.
     */
    public static final int NUMBER_THIRTEEN = 13;
    /**
     * Number 14.
     */
    public static final int NUMBER_FOURTEEN = 14;
    /**
     * Number 15.
     */
    public static final int NUMBER_FIFTEEN = 15;
    /**
     * Number 16.
     */
    public static final int NUMBER_SIXTEEN = 16;
    /**
     * Number 17.
     */
    public static final int NUMBER_SEVENTEEN = 17;
    /**
     * Number 18.
     */
    public static final int NUMBER_EIGHTEEN = 18;

    /**
     * 10 power of 1.
     */
    public static final long TEN_POW_1 = 10L;
    /**
     * 10 power of 2.
     */
    public static final long TEN_POW_2 = 100L;
    /**
     * 10 power of 3.
     */
    public static final long TEN_POW_3 = 1000L;
    /**
     * 10 power of 4.
     */
    public static final long TEN_POW_4 = 10000L;
    /**
     * 10 power of 5.
     */
    public static final long TEN_POW_5 = 100000L;
    /**
     * 10 power of 6.
     */
    public static final long TEN_POW_6 = 1000000L;
    /**
     * 10 power of 7.
     */
    public static final long TEN_POW_7 = 10000000L;
    /**
     * 10 power of 8.
     */
    public static final long TEN_POW_8 = 100000000L;
    /**
     * 10 power of 9.
     */
    public static final long TEN_POW_9 = 1000000000L;
    /**
     * 10 power of 10.
     */
    public static final long TEN_POW_10 = 10000000000L;
    /**
     * 10 power of 11.
     */
    public static final long TEN_POW_11 = 100000000000L;
    /**
     * 10 power of 12.
     */
    public static final long TEN_POW_12 = 1000000000000L;
    /**
     * 10 power of 13.
     */
    public static final long TEN_POW_13 = 10000000000000L;
    /**
     * 10 power of 14.
     */
    public static final long TEN_POW_14 = 100000000000000L;
    /**
     * 10 power of 15.
     */
    public static final long TEN_POW_15 = 1000000000000000L;
    /**
     * 10 power of 16.
     */
    public static final long TEN_POW_16 = 10000000000000000L;
    /**
     * 10 power of 17.
     */
    public static final long TEN_POW_17 = 100000000000000000L;
    /**
     * 10 power of 18.
     */
    public static final long TEN_POW_18 = 1000000000000000000L;

    /**
     * For invalid JSON.
     */
    public static final String ILLEGAL_JSON_VALUE = "Illegal value at ";


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
         *
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
            if (character == '"') {
                cursor = character;
                return getString(sb);
            }

            // String with escape characters ?!
            for ( ; ;) {
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
                                        "Invalid Token at ");
                        }
                        break;
                    default:
                        if (character == '"') {
                            cursor = character;
                            return getString(sb);
                        }
                        sb.append(character);
                }
                character = (char) reader.read();
            }
        }

        private String getString(final StringBuilder builder) {
            return builder.toString();
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
                            && character != '}'
                            && character != ']'
                            && !isSpace(character)) {
                        decimals.append(character);
                    }
                    cursor = character;
                    return getDecimalNumber(startingChar, builder, decimals);
                } else { // Exponential Non Decimal Number
                    builder.append(character);
                    while ((character = (char) reader.read()) != ','
                            && character != '}'
                            && character != ']'
                            && !isSpace(character)) {
                        builder.append(character);
                    }
                    cursor = character;
                    return getExponentialNumber(startingChar, builder);
                }

            } else {
                cursor = character;
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
            if (builder.length() < BYTE_LENGTH) {
                return getByte(startingChar, builder);
            }
            if (builder.length() < SHORT_LENGTH) {
                return getShort(startingChar, builder);
            }
            if (builder.length() < INTEGER_LENGTH) {
                return getInteger(startingChar, builder);
            }
            if (builder.length() < LONG_LENGTH) {
                return getLong(startingChar, builder);
            }
            return new BigInteger(startingChar + builder.toString());
        }

        /**
         * Get Byte from String.
         *
         * @param startingChar
         * @param builder
         * @return number
         */
        private byte getByte(final char startingChar,
                             final StringBuilder builder) {
            boolean isNegative = (startingChar == '-');
            int length = builder.length();
            if (length != 0) {
                byte number = (byte) getValue(builder.charAt(length - 1));
                for (int i = 1; i < length; i++) {
                    number += getValue(builder.charAt(i - 1), length - i);
                }
                return isNegative ? (byte) (number * -1)
                    : (byte) (number
                        + ((byte) getValue(startingChar, length)));
            } else {
                return (byte) getValue(startingChar);
            }
        }

        /**
         * Get Short from String.
         *
         * @param startingChar
         * @param builder
         * @return number
         */
        private Number getShort(final char startingChar,
                                final StringBuilder builder) {
            boolean isNegative = (startingChar == '-');
            int length = builder.length();
            short number = (short) getValue(builder.charAt(length - 1));
            for (int i = 1; i < length; i++) {
                number += getValue(builder.charAt(i - 1), length - i);
            }
            if (isNegative) {
                number = (short) (number * -1);
                if (number >= Byte.MIN_VALUE) {
                    return Short.valueOf(number).byteValue();
                }
                return number;

            } else {
                number = (short) (number
                        + ((short) getValue(startingChar, length)));
                if (number <= Byte.MAX_VALUE) {
                    return Short.valueOf(number).byteValue();
                } else {
                    return number;
                }
            }

        }

        /**
         * Get Integer from String.
         *
         * @param startingChar
         * @param builder
         * @return number
         */
        private Number getInteger(final char startingChar,
                                  final StringBuilder builder) {
            boolean isNegative = (startingChar == '-');
            int length = builder.length();
            int number = getValue(builder.charAt(length - 1));
            for (int i = 1; i < length; i++) {
                number += getValue(builder.charAt(i - 1), length - i);
            }
            if (isNegative) {
                number = (number * -1);
                if (number >= Short.MIN_VALUE) {
                    return Integer.valueOf(number).shortValue();
                }
                return number;

            } else {
                number = (number + ((int) getValue(startingChar, length)));
                if (number <= Short.MAX_VALUE) {
                    return Integer.valueOf(number).shortValue();
                } else {
                    return number;
                }
            }

        }

        /**
         * Get Long from String.
         *
         * @param startingChar
         * @param builder
         * @return number
         */
        private Number getLong(final char startingChar,
                               final StringBuilder builder) {
            boolean isNegative = (startingChar == '-');
            int length = builder.length();
            long number = getValue(builder.charAt(length - 1));
            for (int i = 1; i < length; i++) {
                number += getValue(builder.charAt(i - 1), length - i);
            }
            if (isNegative) {
                number = (number * -1);
                if (number >= Integer.MIN_VALUE) {
                    return Long.valueOf(number).intValue();
                }
                return number;

            } else {
                number = (number + (getValue(startingChar, length)));
                if (number <= Integer.MAX_VALUE) {
                    return Long.valueOf(number).intValue();
                } else {
                    return number;
                }
            }

        }

        private int getValue(final char character) {
            switch (character) {
                case '0':
                    return NUMBER_ZERO;
                case '1':
                    return NUMBER_ONE;
                case '2':
                    return NUMBER_TWO;
                case '3':
                    return NUMBER_THREE;
                case '4':
                    return NUMBER_FOUR;
                case '5':
                    return NUMBER_FIVE;
                case '6':
                    return NUMBER_SIX;
                case '7':
                    return NUMBER_SEVEN;
                case '8':
                    return NUMBER_EIGHT;
                case '9':
                    return NUMBER_NINE;
                default:
                    throw new IllegalArgumentException("Invalid JSON at");
            }
        }

        private long getValue(final char character, final int placement) {
            switch (placement) {
                case NUMBER_ONE:
                    return getValue(character) * TEN_POW_1;
                case NUMBER_TWO:
                    return getValue(character) * TEN_POW_2;
                case NUMBER_THREE:
                    return getValue(character) * TEN_POW_3;
                case NUMBER_FOUR:
                    return getValue(character) * TEN_POW_4;
                case NUMBER_FIVE:
                    return getValue(character) * TEN_POW_5;
                case NUMBER_SIX:
                    return getValue(character) * TEN_POW_6;
                case NUMBER_SEVEN:
                    return getValue(character) * TEN_POW_7;
                case NUMBER_EIGHT:
                    return getValue(character) * TEN_POW_8;
                case NUMBER_NINE:
                    return getValue(character) * TEN_POW_9;
                case NUMBER_TEN:
                    return getValue(character) * TEN_POW_10;
                case NUMBER_ELEVEN:
                    return getValue(character) * TEN_POW_11;
                case NUMBER_TWELVE:
                    return getValue(character) * TEN_POW_12;
                case NUMBER_THIRTEEN:
                    return getValue(character) * TEN_POW_13;
                case NUMBER_FOURTEEN:
                    return getValue(character) * TEN_POW_14;
                case NUMBER_FIFTEEN:
                    return getValue(character) * TEN_POW_15;
                case NUMBER_SIXTEEN:
                    return getValue(character) * TEN_POW_16;
                case NUMBER_SEVENTEEN:
                    return getValue(character) * TEN_POW_17;
                case NUMBER_EIGHTEEN:
                    return getValue(character) * TEN_POW_18;
                default:
                    throw new IllegalArgumentException("Invalid JSON");
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
            BigDecimal bigDecimal = new BigDecimal(startingChar
                    + builder.toString() + "." + decimal.toString());
            // TODO Better Way to check if this is float / double
            if (bigDecimal
                    .equals(BigDecimal.valueOf(bigDecimal.floatValue()))) {
                return bigDecimal.floatValue();
            }
            if (bigDecimal
                    .equals(BigDecimal.valueOf(bigDecimal.doubleValue()))) {
                return bigDecimal.doubleValue();
            }
            return bigDecimal;
        }

        /**
         * Reads True from Reader. Reader will stip at the "e" symbol.
         *
         * @return string
         * @throws IOException
         */
        private boolean getTrue() throws IOException {
            char[] charBuffer = next(NUMBER_THREE);
            if (charBuffer[0] == 'r'
                    && charBuffer[1] == 'u'
                    && charBuffer[2] == 'e') {
                cursor = 'e';
                return true;
            } else {
                throw new IllegalArgumentException(ILLEGAL_JSON_VALUE);
            }
        }

        /**
         * Reads False from Reader. Reader will stip at the "e" symbol.
         *
         * @return string
         * @throws IOException
         */
        private boolean getFalse() throws IOException {
            char[] charBuffer = next(NUMBER_FOUR);
            if (charBuffer[NUMBER_ZERO] == 'a'
                    && charBuffer[NUMBER_ONE] == 'l'
                    && charBuffer[NUMBER_TWO] == 's'
                    && charBuffer[NUMBER_THREE] == 'e') {
                cursor = 'e';
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
            char[] charBuffer = next(NUMBER_THREE);
            if (charBuffer[NUMBER_ZERO] == 'u'
                    && charBuffer[NUMBER_ONE] == 'l'
                    && charBuffer[NUMBER_TWO] == 'l') {
                cursor = 'l';
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
            String key = getString().intern();
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
            cursor = character;
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
                while (this.reader.read() != '"') {
                    continue;
                }
                return false;
            }
            while ((character = (char) this.reader.read()) != '"'
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
            while ((character = (char) this.reader.read()) != ','
                    && character != ']') {
                continue;
            }
            return character == ']';
        }

    }
}
