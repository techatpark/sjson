package com.techatpark.sjson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public final class Json {

    public Object read(final String jsonText) {
        final char[] charArray = jsonText.toCharArray();
        return getValue(charArray,nextClean(charArray,-1)).value();
    }

    private ValueEntry<Map<String,Object>> getJsonObject(final char[] charArray, final int index) {

        Map<String,Object> jsonMap = new HashMap<>();
        int currentIndex = nextClean(charArray, index);

        ValueEntry valueEntry;
        String theKey;

        while (charArray[currentIndex] == '"') {
            // 1. Get the Key
            valueEntry = getString(charArray, currentIndex);
            theKey = (String) valueEntry.value();

            // 2. Move to :
            currentIndex = nextClean(charArray, valueEntry.end());

            // 3. Get the Value
            valueEntry = getValue(charArray, nextClean(charArray, currentIndex));
            currentIndex = nextClean(charArray, valueEntry.end());

            // 4. Place the value
            jsonMap.put(theKey,valueEntry.value());

            // 5. Check if it has a comma(,)
            if(charArray[currentIndex] == ',') {
                currentIndex = nextClean(charArray, currentIndex);
            }
        }

        return new ValueEntry(jsonMap,currentIndex);
    }

    private ValueEntry<List> getJsonArray(final char[] charArray, final int index) {

        List list = new ArrayList();
        int currentIndex = nextClean(charArray, index);

        ValueEntry valueEntry;

        while (charArray[currentIndex] != ']') {

            // 1. Get the Value
            valueEntry = getValue(charArray, currentIndex);
            currentIndex = nextClean(charArray, valueEntry.end());

            // 2. Place the value
            list.add(valueEntry.value());

            // 3. Check if it has a comma(,)
            if(charArray[currentIndex] == ',') {
                currentIndex = nextClean(charArray, currentIndex);
            }

        }


        return new ValueEntry(list,currentIndex);
    }


    private ValueEntry<?> getValue(final char[] charArray, final int index) {
        ValueEntry<?> valueEntry ;
        switch (charArray[index]) {
            case '"' -> valueEntry = getString(charArray, index);
            case 'n' -> valueEntry = getNull(charArray, index);
            case 't' -> valueEntry = getTrue(charArray, index);
            case 'f' -> valueEntry = getFalse(charArray, index);
            case '{' -> valueEntry = getJsonObject(charArray, index);
            case '[' -> valueEntry = getJsonArray(charArray, index);
            default -> {
                if (Character.isDigit(charArray[index]) || charArray[index] == '-') {
                    valueEntry = getNumber(charArray, index);
                } else {
                    throw new IllegalArgumentException("Invalid Token at " + index);
                }
            }

        }
        return valueEntry;
    }

    private ValueEntry<Number> getNumber(final char[] charArray, final int index) {

        Number theValue;
        boolean isNegative = charArray[index] == '-';
        int currentIndex = isNegative ? (index + 1) : index;

        boolean containsDot = false;
        StringBuilder builder = new StringBuilder();

        while ( Character.isDigit(charArray[currentIndex])
                || charArray[currentIndex] == '.') {
            builder.append(charArray[currentIndex]);

            currentIndex++;
            if (currentIndex == charArray.length) {
                break;
            }else if (charArray[currentIndex] == '.') {
                containsDot = true;
            }
        }

        //TODO Why We do this ?!
        currentIndex = currentIndex - 1;
        if (containsDot) {
            theValue = Double.parseDouble(builder.toString());
            if (isNegative) {
                theValue = (Double) theValue * -1;
            }
        } else {
            theValue = Long.parseLong(builder.toString());
            if (isNegative) {
                theValue = (Long) theValue * -1;
            }
        }


        return new ValueEntry(theValue, currentIndex);

    }

    private ValueEntry<Boolean> getTrue(final char[] charArray, final int index) {
        if (charArray[index] == 't'
                && charArray[index + 1] == 'r'
                && charArray[index + 2] == 'u'
                && charArray[index + 3] == 'e') {
            return new ValueEntry(true, index + 3);
        } else {
            throw new IllegalArgumentException("Illegal value at " + index);
        }
    }

    private ValueEntry<Boolean> getFalse(final char[] charArray, final int index) {
        if (charArray[index] == 'f'
                && charArray[index + 1] == 'a'
                && charArray[index + 2] == 'l'
                && charArray[index + 3] == 's'
                && charArray[index + 4] == 'e') {
            return new ValueEntry( false, index + 4);
        } else {
            throw new IllegalArgumentException("Illegal value at " + index);
        }
    }

    private ValueEntry<Object> getNull(final char[] charArray, final int index) {
        if (charArray[index] == 'n'
                && charArray[index + 1] == 'u'
                && charArray[index + 2] == 'l'
                && charArray[index + 3] == 'l') {
            return new ValueEntry( null, index + 3);
        } else {
            throw new IllegalArgumentException("Illegal value at " + index);
        }
    }

    private ValueEntry<String> getString(final char[] charArray, final int index) {

        StringBuilder builder = new StringBuilder();
        int currentIndex = index;
        char c;
        while (charArray[++currentIndex] != '"'
                || charArray[currentIndex - 1] == '\\') {
            c = charArray[currentIndex];
            switch (c) {
                case 0:
                case '\n':
                case '\r':
                    throw new IllegalArgumentException("Unterminated string");
                case '\\':
                    currentIndex = currentIndex + 1;
                    c = charArray[currentIndex];
                    switch (c) {
                        case 'b':
                            builder.append('\b');
                            break;
                        case 't':
                            builder.append('\t');
                            break;
                        case 'n':
                            builder.append('\n');
                            break;
                        case 'f':
                            builder.append('\f');
                            break;
                        case 'r':
                            builder.append('\r');
                            break;
                        case 'u':
                            try {
                                String a = String.copyValueOf(charArray, currentIndex + 1, 4);
                                currentIndex = currentIndex + 4;
                                builder.append((char) Integer.parseInt(a, 16));
                            } catch (NumberFormatException e) {
                                throw new IllegalArgumentException("Illegal escape.", e);
                            }
                            break;
                        case '"':
                        case '\'':
                        case '\\':
                        case '/':
                            builder.append(c);
                            break;
                        default:
                            throw new IllegalArgumentException("Illegal escape.");
                    }
                    break;
                default:
                    if (c == '"') {
                        break;
                    }
                    builder.append(c);
            }

        }
        return new ValueEntry( builder.toString(),
                currentIndex);
    }

    private int nextClean(final char[] charArray, final int index) {
        int currentIndex = index;

        while (charArray[++currentIndex] == ' '
                || charArray[currentIndex] == '\n'
                || charArray[currentIndex] == '\r'
                || charArray[currentIndex] == '\t') {
        }

        return currentIndex;
    }

    public record ValueEntry<T>(T value,
                             int end) {
    }

}