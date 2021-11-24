package com.techatpark.sjson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 */
public final class Tokenizer {

    public List<Token> getTokens(final char[] charArray) {
        List<Token> tokens = new ArrayList<>();

        int currentIndex = nextClean(charArray, -1);

        ValueEntry valueEntry;
        String theKey;

        if (charArray[currentIndex] == '{') {
            currentIndex = nextClean(charArray, currentIndex);

            while (charArray[currentIndex] == '"') {
                // 1. Get the Key
                valueEntry = getString(charArray, currentIndex);
                theKey = (String) valueEntry.value();

                // 2. Move to :
                currentIndex = nextClean(charArray, valueEntry.end());

                // 3. Get the Value
                valueEntry = getValue(charArray, nextClean(charArray, currentIndex));
                currentIndex = nextClean(charArray, valueEntry.end());

                // 4. Investigate the pending items to find closing opening tags
                List<ClosingTokenType> closingTokenTypes = new ArrayList<>();



                // Closing Tags
                while (charArray[currentIndex] != ','
                        && charArray[currentIndex] != '"') {
                    if (isClosingTag(charArray[currentIndex])) {
                        closingTokenTypes.add(new ClosingTokenType(getTokenType(charArray[currentIndex]),new ArrayList()));
                    }
                    if (currentIndex == charArray.length - 1) {
                        break;
                    }
                    currentIndex = nextClean(charArray, currentIndex);
                }
                List intermediateScalarItems = null;
                // Go to Next Key Start
                if (charArray[currentIndex] == ',') {
                    currentIndex = nextClean(charArray, currentIndex);
                    while (charArray[currentIndex] == '{'
                            || charArray[currentIndex] == '['
                    || charArray[currentIndex] == '}') {
                        if(getTokenType(charArray[currentIndex]) == TokenType.ARRAY_START) {
                            ValueEntry intermediateArrayEntry = getArray(charArray,currentIndex-1);
                            // TODO: Why Da ?!
                            closingTokenTypes.add(new ClosingTokenType(intermediateArrayEntry.tokenType()
                                    , (List) ((List) intermediateArrayEntry.value()).get(0)));
                            if(intermediateArrayEntry.tokenType() == TokenType.ARRAY_START) {
                                closingTokenTypes.add(new ClosingTokenType(TokenType.OBJECT_START
                                        , new ArrayList()));
                            }
                            if(charArray[intermediateArrayEntry.end()] == ']') {
                                closingTokenTypes.add(new ClosingTokenType(TokenType.ARRAY_END
                                        , new ArrayList()));
                            }
                            currentIndex = nextClean(charArray, intermediateArrayEntry.end());
                            if(charArray[currentIndex] == ',') {

                                currentIndex = nextClean(charArray,currentIndex);
                            }
                        }else {
                            closingTokenTypes.add(new ClosingTokenType(getTokenType(charArray[currentIndex])
                                    ,new ArrayList()));
                            if(currentIndex == charArray.length-1) {
                                break;
                            }else {
                                currentIndex = nextClean(charArray, currentIndex);
                            }

                        }

                    }

                    if( charArray[currentIndex] != '"' ) {
                        TokenType scalarToken = getTokenType(charArray[currentIndex]);
                        switch (scalarToken) {
                            case NULL,NUMBER,TRUE,FALSE -> {
                                ValueEntry intermediateScalarEntry = getArray(charArray,currentIndex-1);
                                intermediateScalarItems = (List) intermediateScalarEntry.value();
                                closingTokenTypes.get(closingTokenTypes.size()-1).items().addAll(intermediateScalarItems);
                                if(intermediateScalarEntry.tokenType == TokenType.ARRAY) {
                                    closingTokenTypes.add(new ClosingTokenType(TokenType.ARRAY_END,new ArrayList()));
                                    // TODO ?
                                    currentIndex = nextClean(charArray,intermediateScalarEntry.end());
                                    if(charArray[currentIndex] == ',') {

                                        currentIndex = nextClean(charArray,currentIndex);
                                    }else if(charArray[currentIndex] == '}') {
                                        closingTokenTypes.add(new ClosingTokenType(TokenType.OBJECT_END,new ArrayList()));
                                    }
                                }else if(intermediateScalarEntry.tokenType == TokenType.ARRAY_START) {
                                    closingTokenTypes.add(new ClosingTokenType(TokenType.OBJECT_START,new ArrayList()));
                                    currentIndex = nextClean(charArray,intermediateScalarEntry.end());
                                }
                            }
                            case STRING -> {
                                System.out.println("SSSSS");
                            }
                        }
                    }

                }

                List<TokenType> openingTokenTypes = new ArrayList<>();
                if (valueEntry.tokenType() == TokenType.ARRAY_START) {
                    // This is a non-scalar Array
                    openingTokenTypes.add(getTokenType(charArray[valueEntry.end()]));
                }

                tokens.add(new Token(theKey, valueEntry.tokenType(), valueEntry.value(), closingTokenTypes, openingTokenTypes));


            }

        }

        return tokens;
    }

    private ValueEntry getValue(final char[] charArray, final int index) {
        ValueEntry valueEntry = null;

        switch (getTokenType(charArray[index])) {

            case STRING -> {
                valueEntry = getString(charArray, index);
            }
            case NULL -> {
                valueEntry = getNull(charArray, index);
            }
            case TRUE -> {
                valueEntry = getTrue(charArray, index);
            }
            case FALSE -> {
                valueEntry = getFalse(charArray, index);
            }
            case NUMBER -> {
                valueEntry = getNumber(charArray, index);
            }
            case OBJECT_START -> {
                valueEntry = getObject(charArray, index);
                if (valueEntry.tokenType == TokenType.OBJECT_START) {
                    // This is a non-empty Object
                }
            }
            case ARRAY_START -> {
                valueEntry = getArray(charArray, index);
            }


        }
        return valueEntry;
    }

    private ValueEntry getNumber(final char[] charArray, final int index) {

        Number theValue;
        boolean isNegative = charArray[index] == '-';
        int currentIndex = isNegative ? (index + 1) : index;

        boolean containsDot = false;
        StringBuilder builder = new StringBuilder();

        while (Character.isDigit(charArray[currentIndex])
                || charArray[currentIndex] == '.') {
            builder.append(charArray[currentIndex]);
            ++currentIndex;
            if (charArray[currentIndex] == '.') {
                containsDot = true;
            }
        }

        //TODO Why We do this ?!
        currentIndex = currentIndex - 1;
        if (containsDot) {
            theValue = Float.parseFloat(builder.toString());
            if (isNegative) {
                theValue = (Float) theValue * -1;
            }
        } else {
            theValue = Integer.parseInt(builder.toString());
            if (isNegative) {
                theValue = (Integer) theValue * -1;
            }
        }


        return new ValueEntry(TokenType.NUMBER, theValue, currentIndex);

    }

    private ValueEntry getTrue(final char[] charArray, final int index) {
        if (charArray[index] == 't'
                && charArray[index + 1] == 'r'
                && charArray[index + 2] == 'u'
                && charArray[index + 3] == 'e') {
            return new ValueEntry(TokenType.TRUE, true, index + 3);
        } else {
            throw new IllegalArgumentException("Illegal value at " + index);
        }
    }

    private ValueEntry getFalse(final char[] charArray, final int index) {
        if (charArray[index] == 'f'
                && charArray[index + 1] == 'a'
                && charArray[index + 2] == 'l'
                && charArray[index + 3] == 's'
                && charArray[index + 4] == 'e') {
            return new ValueEntry(TokenType.FALSE, false, index + 4);
        } else {
            throw new IllegalArgumentException("Illegal value at " + index);
        }
    }

    private ValueEntry getNull(final char[] charArray, final int index) {
        if (charArray[index] == 'n'
                && charArray[index + 1] == 'u'
                && charArray[index + 2] == 'l'
                && charArray[index + 3] == 'l') {
            return new ValueEntry(TokenType.NULL, null, index + 3);
        } else {
            throw new IllegalArgumentException("Illegal value at " + index);
        }
    }

    private ValueEntry getString(final char[] charArray, final int index) {

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
        return new ValueEntry(TokenType.STRING, builder.toString(),
                currentIndex);
    }

    private ValueEntry getObject(final char[] charArray, final int index) {

        int nextNonspaceIndex = nextClean(charArray, index);
        // Empty Object
        if (charArray[nextNonspaceIndex] == '}') {
            return new ValueEntry(TokenType.EMPTY_OBJECT, new HashMap<>(),
                    nextNonspaceIndex);
        } else {
            return new ValueEntry(TokenType.OBJECT_START,
                    new HashMap<>(), index);
        }
    }

    private ValueEntry getArray(final char[] charArray, final int index) {
        List theArray = new ArrayList();
        int currentIndex = nextClean(charArray, index);
        TokenType tokenType = getTokenType(charArray[currentIndex]);
        if (tokenType == TokenType.ARRAY_END) {
            // Empty Array
            return new ValueEntry(TokenType.EMPTY_ARRAY, theArray, currentIndex);
        }


        ValueEntry arrayValue;

        while (tokenType != TokenType.ARRAY_END) {
            arrayValue = getValue(charArray, currentIndex);

            if (arrayValue.tokenType == TokenType.OBJECT_START) {
                // This is non-scalar Array
                break;
            } else if (arrayValue.tokenType == TokenType.ARRAY_START) {
                // This has a non-scalar Array
                arrayValue = getArray(charArray,currentIndex);
                theArray.add( arrayValue.value());
                currentIndex = arrayValue.end();
                break;
            }

            theArray.add(arrayValue.value());

            currentIndex = nextClean(charArray, arrayValue.end());

            if (charArray[currentIndex] == ',') {
                currentIndex = nextClean(charArray, currentIndex);
            }

            tokenType = getTokenType(charArray[currentIndex]);
        }

        if (tokenType == TokenType.ARRAY_END) {
            return new ValueEntry(TokenType.ARRAY, theArray, currentIndex);
        }else {
            return new ValueEntry(TokenType.ARRAY_START, theArray, currentIndex);
        }
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

    private boolean isClosingTag(final char character) {
        return (character == '}'
                || character == ']');
    }


    private TokenType getTokenType(final char frontChar) {
        switch (frontChar) {
            case '{':
                return TokenType.OBJECT_START;
            case '}':
                return TokenType.OBJECT_END;
            case '[':
                return TokenType.ARRAY_START;
            case ']':
                return TokenType.ARRAY_END;
            case '"':
                return TokenType.STRING;
            case 't':
                return TokenType.TRUE;
            case 'f':
                return TokenType.FALSE;
            case 'n':
                return TokenType.NULL;
            default:
                if (Character.isDigit(frontChar) || frontChar == '-') {
                    return TokenType.NUMBER;
                } else {
                    throw new IllegalArgumentException("Invalid Token " + frontChar);
                }

        }
    }


    public enum TokenType {
        OBJECT_START, ARRAY_START, OBJECT_END, STRING, NUMBER, NULL, TRUE, FALSE, ARRAY, EMPTY_ARRAY, EMPTY_OBJECT, ARRAY_END
    }

    public record ClosingTokenType(TokenType tokenType,
                             List items) {

        @Override
        public String toString() {
            return tokenType + items.toString() ;
        }
    }

    public record Token(String key, TokenType tokenType, Object value,
                        List<ClosingTokenType> closingTokenTypes,
                        List<TokenType> openingTokenTypes) {
    }

    private record ValueEntry(TokenType tokenType,
                             Object value,
                             int end) {
    }

}