package com.techatpark.sjson;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
/**
 *
 */
public final class Json {

    public Object read(final Reader reader) throws IOException {
        try (reader) {
            ContentExtractor extractor = new ContentExtractor(reader);
            return getValue(extractor,nextClean(extractor));
        }
    }

    private boolean getTrue(final ContentExtractor extractor) throws IOException {
        if ((char) extractor.read() == 'r'
                && (char) extractor.read() == 'u'
                && (char) extractor.read() == 'e') {
            return true;
        } else {
            throw new IllegalArgumentException("Illegal value at ");
        }
    }

    private boolean getFalse(final ContentExtractor extractor) throws IOException {
        if ((char) extractor.read() == 'a'
                && (char) extractor.read() == 'l'
                && (char) extractor.read() == 's'
                && (char) extractor.read() == 'e') {
            return false;
        } else {
            throw new IllegalArgumentException("Illegal value at ");
        }
    }

    private Object getNull(final ContentExtractor extractor) throws IOException {
        if ((char) extractor.read() == 'u'
                && (char) extractor.read() == 'l'
                && (char) extractor.read() == 'l') {
            return null;
        } else {
            throw new IllegalArgumentException("Illegal value at ");
        }
    }

    private String getString(final ContentExtractor extractor) throws IOException {
        char c;
        final StringBuilder sb = new StringBuilder();
        for (; ; ) {
            c = (char) extractor.read();
            switch (c) {
                case 0:
                case '\n':
                case '\r':
                    throw new IllegalArgumentException("Invalid Token at ");
                case '\\':
                    c = (char) extractor.read();
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
                            sb.append((char) Integer.parseInt(next4(extractor), 16));
                            break;
                        case '"':
                        case '\'':
                        case '\\':
                        case '/':
                            sb.append(c);
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

    private Number getNumber(final ContentExtractor extractor, final char startingChar) throws IOException {

        final StringBuilder builder = new StringBuilder();
        builder.append(startingChar);
        char character;

        while (Character.isDigit(character = (char) extractor.read())) {
            builder.append(character);
        }

        if (character == '.') {
            builder.append('.');
            while (Character.isDigit(character = (char) extractor.read())) {
                builder.append(character);
            }
            ((ContentExtractor) extractor).reverse(character);
            BigDecimal bigDecimal = new BigDecimal(builder.toString());
            return bigDecimal;
        } else {
            ((ContentExtractor) extractor).reverse(character);
            BigInteger bigInteger = new BigInteger(builder.toString());
            return bigInteger;
        }
    }

    private Map<String, Object> getObject(final ContentExtractor extractor) throws IOException {

        boolean eoo = endOfObject(extractor);
        if (eoo) {
            return Collections.EMPTY_MAP;
        }

        final Map<String, Object> jsonMap = new HashMap<>();
        String theKey;

        while (!eoo) {
            // 1. Get the Key. User String Pool as JSON Keys may be repeating across
            theKey = getString(extractor).intern();

            // 2. Get the Value
            jsonMap.put(theKey, getValue(extractor,nextCleanAfter(extractor,':')));

            eoo = endOfObject(extractor);
        }

        return Collections.unmodifiableMap(jsonMap);
    }

    private List getArray(final ContentExtractor extractor) throws IOException {

        Object value = getValue(extractor,nextClean(extractor));
        // If not Empty List
        if (value == extractor) {
            return Collections.EMPTY_LIST;
        }
        final List list = new ArrayList();
        list.add(value);
        boolean eoa = endOfArray(extractor);
        while (!eoa) {
            value = getValue(extractor,nextClean(extractor));
            list.add(value);
            eoa = endOfArray(extractor);
        }

        return Collections.unmodifiableList(list);
    }

    private char nextCleanAfter(final ContentExtractor extractor,final char skipChar) throws IOException {
        while (extractor.read() != skipChar) {
        }
        return nextClean(extractor);
    }

    private char nextClean(final ContentExtractor extractor) throws IOException {
        char character;
        while ((character = (char) extractor.read()) == ' '
                || character == '\n'
                || character == '\r'
                || character == '\t') {
        }
        return character;
    }


    private boolean endOfObject(final ContentExtractor extractor) throws IOException {
        char character;
        while ((character = (char) extractor.read()) != '"'
                && character != '}') {
        }
        return character == '}';
    }

    private boolean endOfArray(final ContentExtractor extractor) throws IOException {
        char character;
        while ((character = (char) extractor.read()) != ','
                && character != ']') {
        }
        return character == ']';
    }

    private String next4(final ContentExtractor extractor) throws IOException {
        return new String(new char[]{(char) extractor.read(), (char) extractor.read(),
                (char) extractor.read(), (char) extractor.read()});
    }

    private Object getValue(final ContentExtractor extractor,final char character) throws IOException {
        switch (character) {
            case '"':
                return getString(extractor);
            case 'n':
                return getNull(extractor);
            case 't':
                return getTrue(extractor);
            case 'f':
                return getFalse(extractor);
            case '{':
                return getObject(extractor);
            case '[':
                return getArray(extractor);
            case ']':
                return extractor;
            default:
                return getNumber(extractor, character);
        }
    }

    private class ContentExtractor {

        private final Reader reader;

        private int previous;
        private int current ;

        private ContentExtractor(final Reader reader) {
            this.reader = reader;
            this.previous = 0;
        }

        private void reverse(final int previous) {
            this.previous = previous;
        }

        public int read() throws IOException {
            if (this.previous == 0) {
                return current = this.reader.read();
            } else {
                int temp = this.previous;
                this.previous = 0;
                return temp;
            }
        }

    }
}