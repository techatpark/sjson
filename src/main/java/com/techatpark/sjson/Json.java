package com.techatpark.sjson;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public final class Json {



    public Object read(final String jsonText) throws IOException {
        final Reader reader = new StringReader(jsonText);
        return getValue(reader);
    }

    private Map<String, Object> getJsonObject(final Reader reader) throws IOException {
        Map<String, Object> jsonMap = new HashMap<>();

        String theKey;
        char character = nextClean(reader);

        while (character == '"') {
            // 1. Get the Key
            theKey = getString(reader);

            // 2. Move to :
            nextClean(reader);

            // 3. Get the Value
            jsonMap.put(theKey, getValue(reader));

            character = nextClean(reader);

            // 5. Check if it has a comma(,)
            if (character == ',') {
                character = nextClean(reader);
            }
        }

        return jsonMap;
    }

    private List getJsonArray(final Reader reader) throws IOException {

        List list = new ArrayList();
        char character = nextClean(reader);

        Object value ;

        if(character == ']') {
            return list;
        }

        while (character != ',') {

            value = getValue(reader,character);

            // 1. Get the Value
            list.add(value);

            character = nextClean(reader);
            if(character == ']') {
                break;
            }
            character = nextClean(reader);

        }

        return list;
    }

    private String getString(final Reader reader) throws IOException {
        char c;
        StringBuilder sb = new StringBuilder();
        for (;;) {
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
                            try {
                                sb.append((char)Integer.parseInt(next4(reader), 16));
                            } catch (NumberFormatException e) {
                                throw new IllegalArgumentException("Invalid Token at ");
                            }
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

    private Number getNumber(final Reader reader,final char startingChar) throws IOException {
        Number theValue ;
        StringBuilder builder = new StringBuilder();
        char character = startingChar;
        boolean containsDot = false;
        reader.mark(1);
        while ( Character.isDigit(character)
                || character == '.'
                || character == '-') {
            builder.append(character);
            if (character == '.') {
                containsDot = true;
            }
            reader.mark(1);
            character = (char) reader.read();
        }

        if (containsDot) {
            theValue = Double.parseDouble(builder.toString());
        } else {
            theValue = Long.parseLong(builder.toString());
        }
        reader.reset();
        reader.mark(0);
        return theValue;
    }


    private Object getValue(final Reader reader) throws IOException {
        return getValue(reader,nextClean(reader));
    }

    private Object getValue(final Reader reader,final char character) throws IOException {
        Object valueEntry;
        switch (character) {
            case '"' -> valueEntry = getString(reader);
            case 'n' -> valueEntry = getNull(reader);
            case 't' -> valueEntry = getTrue(reader);
            case 'f' -> valueEntry = getFalse(reader);
            case '{' -> valueEntry = getJsonObject(reader);
            case '[' -> valueEntry = getJsonArray(reader);
            default -> {
                if (Character.isDigit(character) || character == '-') {
                    valueEntry = getNumber(reader,character);
                } else {
                    throw new IllegalArgumentException("Invalid Token at ");
                }
            }
        }
        return valueEntry;
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
            throw new IllegalArgumentException("Illegal value at");
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


}