package com.techatpark.sjson;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public final class Json {

    public Object read(final Reader reader) throws IOException {
        try (Reader shiftReader = new ShiftReader(reader)) {
            return getValue(shiftReader);
        }
    }

    private Map<String, Object> getObject(final Reader reader) throws IOException {

        char character;
        if( (character = nextClean(reader)) == '}') {
            return Map.of();
        }

        final Map<String, Object> jsonMap = new HashMap<>();
        String theKey;

        while (character == '"') {
            // 1. Get the Key
            theKey = getString(reader);

            // 2. Move to :
            nextClean(reader);

            // 3. Get the Value
            jsonMap.put(theKey, getValue(reader));

            if ( (character = nextClean(reader)) == ',') {
                character = nextClean(reader);
            }
        }

        return jsonMap;
    }

    private List getArray(final Reader reader) throws IOException {
        final Object value = getValue(reader);
        // If not Empty List
        if(value != reader) {
            final List list = new ArrayList();
            list.add(value);
            while(nextClean(reader) == ',') {
                list.add(getValue(reader));
            }
            return list;
        }
        return List.of();
    }

    private String getString(final Reader reader) throws IOException {
        char c;
        final StringBuilder sb = new StringBuilder();
        for (;;) {
            c = (char) reader.read();
            switch (c) {
                case 0,'\n','\r':
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
                            sb.append((char)Integer.parseInt(next4(reader), 16));
                            break;
                        case '"','\'','\\','/':
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

    private Number getNumber(final Reader reader,final char startingChar) throws IOException {
        final StringBuilder builder = new StringBuilder();
        builder.append(startingChar);

        char character;

        while ( Character.isDigit(character = (char) reader.read()) ) {
            builder.append(character);
        }

        if(character == '.') {
            builder.append('.');
            while ( Character.isDigit(character = (char) reader.read()) ) {
                builder.append(character);
            }
            ((ShiftReader)reader).reverse(character);
            return Double.parseDouble(builder.toString());
        } else {
            ((ShiftReader)reader).reverse(character);
            return Long.parseLong(builder.toString());
        }
    }

    private Object getValue(final Reader reader) throws IOException {
        return getValue(reader,nextClean(reader));
    }

    private Object getValue(final Reader reader,final char character) throws IOException {
        switch (character) {
            case '"' : return getString(reader);
            case 'n' : return getNull(reader);
            case 't' : return getTrue(reader);
            case 'f' : return getFalse(reader);
            case '{' : return getObject(reader);
            case '[' : return getArray(reader);
            case ']' : return reader;
            default : return getNumber(reader,character);
        }
    }

    private boolean getTrue(final Reader reader) throws IOException {
        if ( (char) reader.read() == 'r'
                && (char) reader.read() == 'u'
                && (char) reader.read() == 'e') {
            return true;
        } else {
            throw new IllegalArgumentException("Illegal value at ");
        }
    }

    private boolean getFalse(final Reader reader) throws IOException {
        if ( (char) reader.read() == 'a'
                && (char) reader.read() == 'l'
                && (char) reader.read() == 's'
                && (char) reader.read() == 'e') {
            return false;
        } else {
            throw new IllegalArgumentException("Illegal value at ");
        }
    }

    private Object getNull(final Reader reader) throws IOException {
        if ( (char) reader.read() == 'u'
                && (char) reader.read() == 'l'
                && (char) reader.read() == 'l') {
            return null;
        } else {
            throw new IllegalArgumentException("Illegal value at " );
        }
    }

    private char nextClean(final Reader reader) throws IOException {
        char character ;
        while((character = (char) reader.read()) == ' '
                || character == '\n'
                || character == '\r'
                || character == '\t') {
        }
        return character;
    }

    private String next4(final Reader reader) throws IOException {
        return new String(new char[]{(char) reader.read(), (char) reader.read(),
                (char) reader.read(), (char) reader.read()});
    }

    private class ShiftReader extends Reader {

        private final Reader reader;

        private int previous;

        private ShiftReader(final Reader reader) {
            this.reader = reader;
            this.previous = 0;
        }

        void reverse(final int previous) {
            this.previous = previous;
        }

        /**
         * Reads a single character.  This method will block until a character is
         * available, an I/O error occurs, or the end of the stream is reached.
         *
         * <p> Subclasses that intend to support efficient single-character input
         * should override this method.
         *
         * @return The character read, as an integer in the range 0 to 65535
         * ({@code 0x00-0xffff}), or -1 if the end of the stream has
         * been reached
         * @throws IOException If an I/O error occurs
         */
        @Override
        public int read() throws IOException {
            if(this.previous == 0) {
                return this.reader.read();
            }
            else {
                int temp = this.previous;
                this.previous = 0;
                return temp;
            }
        }

        /**
         * Reads characters into a portion of an array.  This method will block
         * until some input is available, an I/O error occurs, or the end of the
         * stream is reached.
         *
         * <p> If {@code len} is zero, then no characters are read and {@code 0} is
         * returned; otherwise, there is an attempt to read at least one character.
         * If no character is available because the stream is at its end, the value
         * {@code -1} is returned; otherwise, at least one character is read and
         * stored into {@code cbuf}.
         *
         * @param cbuf Destination buffer
         * @param off  Offset at which to start storing characters
         * @param len  Maximum number of characters to read
         * @return The number of characters read, or -1 if the end of the
         * stream has been reached
         * @throws IndexOutOfBoundsException If {@code off} is negative, or {@code len} is negative,
         *                                   or {@code len} is greater than {@code cbuf.length - off}
         * @throws IOException               If an I/O error occurs
         */
        @Override
        public int read(final char[] cbuf, final int off, final int len) throws IOException {
            return this.reader.read(cbuf,off,len);
        }

        /**
         * Closes the stream and releases any system resources associated with
         * it.  Once the stream has been closed, further read(), ready(),
         * mark(), reset(), or skip() invocations will throw an IOException.
         * Closing a previously closed stream has no effect.
         *
         * @throws IOException If an I/O error occurs
         */
        @Override
        public void close() throws IOException {
            this.reader.close();
        }
    }
}