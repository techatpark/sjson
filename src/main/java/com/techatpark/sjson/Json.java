package com.techatpark.sjson;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

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
            ContentExtractor extractor = new ContentExtractor(reader);
            return getValue(extractor, extractor.nextClean());
        }
    }


    private Map<String, Object> getObject(final ContentExtractor extractor) throws IOException {

        boolean eoo = extractor.endOfObject();
        if (eoo) {
            return Collections.EMPTY_MAP;
        }

        final Map<String, Object> jsonMap = new HashMap<>();
        String theKey;

        while (!eoo) {
            // 1. Get the Key. User String Pool as JSON Keys may be repeating across
            theKey = extractor.getString().intern();

            // 2. Get the Value
            jsonMap.put(theKey, getValue(extractor, extractor.nextCleanAfter(':')));

            eoo = extractor.endOfObject();
        }

        return Collections.unmodifiableMap(jsonMap);
    }

    private List getArray(final ContentExtractor extractor) throws IOException {

        Object value = getValue(extractor, extractor.nextClean());
        // If not Empty List
        if (value == extractor) {
            return Collections.EMPTY_LIST;
        }
        final List list = new ArrayList();
        list.add(value);
        boolean eoa = extractor.endOfArray();
        while (!eoa) {
            value = getValue(extractor, extractor.nextClean());
            list.add(value);
            eoa = extractor.endOfArray();
        }

        return Collections.unmodifiableList(list);
    }


    private Object getValue(final ContentExtractor extractor, final char character) throws IOException {
        switch (character) {
            case '"':
                return extractor.getString();
            case 'n':
                return extractor.getNull();
            case 't':
                return extractor.getTrue();
            case 'f':
                return extractor.getFalse();
            case '{':
                return getObject(extractor);
            case '[':
                return getArray(extractor);
            case ']':
                return extractor;
            default:
                return extractor.getNumber(character);
        }
    }

    private class ContentExtractor {

        private final Reader reader;

        private boolean back;
        private int current;

        private ContentExtractor(final Reader reader) {
            this.reader = reader;
        }

        private void back() {
            this.back = true;
        }

        public int read() throws IOException {
            if (this.back) {
                this.back = false;
                return current;
            }
            return current = this.reader.read();
        }

        private char nextCleanAfter(final char skipChar) throws IOException {
            while (read() != skipChar) {
            }
            return nextClean();
        }

        private char nextClean() throws IOException {
            char character;
            while ((character = (char) read()) == ' '
                    || character == '\n'
                    || character == '\r'
                    || character == '\t') {
            }
            return character;
        }


        private boolean endOfObject() throws IOException {
            char character;
            while ((character = (char) read()) != '"'
                    && character != '}') {
            }
            return character == '}';
        }

        private boolean endOfArray() throws IOException {
            char character;
            while ((character = (char) read()) != ','
                    && character != ']') {
            }
            return character == ']';
        }

        private String next4() throws IOException {
            return new String(new char[]{(char) reader.read(), (char) reader.read(),
                    (char) reader.read(), (char) reader.read()});
        }

        private String getString() throws IOException {
            char c;
            final StringBuilder sb = new StringBuilder();
            for (; ; ) {
                c = (char) reader.read();
                switch (c) {
                    case 0:
                    case '\n':
                    case '\r':
                        throw new IllegalArgumentException("Invalid Token at ");
                    case '\\':
                        c = (char) reader.read();
                        switch (c) {
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
                                sb.append((char) Integer.parseInt(next4(), 16));
                                break;
                            case '"':
                            case '\'':
                            case '\\':
                            case '/':
                                sb.append(c);
                                current = c;
                                break;
                            default:
                                throw new IllegalArgumentException("Invalid Token at ");
                        }
                        break;
                    default:
                        if (c == '"') {
                            return sb.toString();
                        }
                        sb.append(c);
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

            if (character == '.') {
                builder.append('.');
                while (Character.isDigit(character = (char) reader.read())
                        || character == 'e' || character == '-') {
                    builder.append(character);
                }
                current = character;
                back();
                BigDecimal bigDecimal = new BigDecimal(builder.toString());
                return bigDecimal;
            } else {
                current = character;
                back();
                BigInteger bigInteger = new BigInteger(builder.toString());
                return bigInteger;
            }
        }

        private boolean getTrue() throws IOException {
            if ((char) reader.read() == 'r'
                    && (char) reader.read() == 'u'
                    && (char) reader.read() == 'e') {
                current = 'e';
                return true;
            } else {
                throw new IllegalArgumentException("Illegal value at ");
            }
        }

        private boolean getFalse() throws IOException {
            if ((char) reader.read() == 'a'
                    && (char) reader.read() == 'l'
                    && (char) reader.read() == 's'
                    && (char) reader.read() == 'e') {
                current = 'e';
                return false;
            } else {
                throw new IllegalArgumentException("Illegal value at ");
            }
        }

        private Object getNull() throws IOException {
            if ((char) reader.read() == 'u'
                    && (char) reader.read() == 'l'
                    && (char) reader.read() == 'l') {
                current = 'l';
                return null;
            } else {
                throw new IllegalArgumentException("Illegal value at ");
            }
        }

    }
}