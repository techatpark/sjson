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
 * Json parser.
 */
public final class Json {

    /**
     * Reads JSON as a Java Object.
     *
     * @param reader
     * @return object
     * @throws IOException
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
         * Flag to start one step back. Look at getNumber
         */
        private boolean back;

        private char current;

        private ContentExtractor(final Reader theReader) {
            this.reader = theReader;
        }

        private void back(final char character) {
            this.back = true;
            this.current = character;
        }

        public char read() throws IOException {
            if (this.back) {
                this.back = false;
                return current;
            }
            return current = (char) this.reader.read();
        }
        /* TODO: Should we read using ContentExtractor ? */
        private char nextCleanAfter(final char skipChar) throws IOException {
            while ((char) this.reader.read() != skipChar) {
            }
            return nextClean();
        }

        /* TODO: Should we read using ContentExtractor ? */
        private char nextClean() throws IOException {
            char character;
            while ((character = (char) this.reader.read()) == ' '
                    || character == '\n'
                    || character == '\r'
                    || character == '\t') {
            }
            return character;
        }

        private boolean endOfObject() throws IOException {
            char character;
            while ((character = read()) != '"'
                    && character != '}') {
            }
            return character == '}';
        }

        private boolean endOfArray() throws IOException {
            char character;
            while ((character = read()) != ','
                    && character != ']') {
            }
            return character == ']';
        }

        private String getString() throws IOException {
            char character;
            final StringBuilder sb = new StringBuilder();
            for (; ; ) {
                character = (char) reader.read();
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
            }
        }

        private Number getNumber(final char startingChar) throws IOException {

            final StringBuilder builder = new StringBuilder(10);
            builder.append(startingChar);
            char character;

            while (Character.isDigit(character = (char) reader.read())) {
                builder.append(character);
            }

            if (character == '.' || character=='e') {
                builder.append(character);
                while (Character.isDigit(character = (char) reader.read())
                        || character == 'e' || character == '-') {
                    builder.append(character);
                }
                back(character);
                BigDecimal bigDecimal = new BigDecimal(builder.toString());
                return bigDecimal;
            } else {
                back(character);
                BigInteger bigInteger = new BigInteger(builder.toString());
                return bigInteger;
            }
        }

        private boolean getTrue() throws IOException {
            char[] cbuf = next(3);
            if (cbuf[0] == 'r'
                    && cbuf[1] == 'u'
                    && cbuf[2] == 'e') {
                current = 'e';
                return true;
            } else {
                throw new IllegalArgumentException("Illegal value at ");
            }
        }

        private boolean getFalse() throws IOException {
            char[] cbuf = next(4);
            if (cbuf[0] == 'a'
                    && cbuf[1] == 'l'
                    && cbuf[2] == 's'
                    && cbuf[3] == 'e') {
                current = 'e';
                return false;
            } else {
                throw new IllegalArgumentException("Illegal value at ");
            }
        }

        private Object getNull() throws IOException {
            char[] cbuf = next(3);
            if (cbuf[0] == 'u'
                    && cbuf[1] == 'l'
                    && cbuf[2] == 'l') {
                current = 'l';
                return null;
            } else {
                throw new IllegalArgumentException("Illegal value at ");
            }
        }

        private char[] next(final int length) throws IOException {
            char[] cbuf = new char[length];
            reader.read(cbuf,0,length);
            return cbuf;
        }

        private Map<String, Object> getObject() throws IOException {

            boolean eoo = endOfObject();
            if (eoo) {
                return Collections.EMPTY_MAP;
            }

            final Map<String, Object> jsonMap = new HashMap<>();

            while (!eoo) {
                // 1. Get the Value
                jsonMap.put(getString().intern(), getValue( nextCleanAfter(':')));
                eoo = endOfObject();
            }

            return Collections.unmodifiableMap(jsonMap);
        }

        private List getArray() throws IOException {

            final Object value = getValue(nextClean());
            // If not Empty List
            if (value == this) {
                return Collections.EMPTY_LIST;
            }
            final List list = new ArrayList();
            list.add(value);
            boolean eoa = endOfArray();
            while (!eoa) {
                list.add(getValue(nextClean()));
                eoa = endOfArray();
            }

            return Collections.unmodifiableList(list);
        }

        private Object getValue() throws IOException {
            return getValue(nextClean());
        }

        private Object getValue(final char character) throws IOException {
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

    }
}
