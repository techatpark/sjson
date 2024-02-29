package com.techatpark.sjson.core;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;

import static com.techatpark.sjson.core.util.ReaderUtil.isSpace;

/**
 * Parser for Numbers.
 */
public final class NumberParser {

    /**
     * Number 1.
     */
    private static final int ONE = 1;
    /**
     * Number 2.
     */
    private static final int TWO = 2;
    /**
     * Number 3.
     */
    private static final int THREE = 3;
    /**
     * Number 4.
     */
    private static final int FOUR = 4;
    /**
     * Number 5.
     */
    private static final int FIVE = 5;
    /**
     * Number 6.
     */
    private static final int SIX = 6;
    /**
     * Number 7.
     */
    private static final int SEVEN = 7;
    /**
     * Number 8.
     */
    private static final int EIGHT = 8;
    /**
     * Number 9.
     */
    private static final int NINE = 9;
    /**
     * Number 10.
     */
    private static final int TEN = 10;
    /**
     * Number 11.
     */
    private static final int ELEVEN = 11;
    /**
     * Number 12.
     */
    private static final int TWELVE = 12;
    /**
     * Number 13.
     */
    private static final int THIRTEEN = 13;
    /**
     * Number 14.
     */
    private static final int FOURTEEN = 14;
    /**
     * Number 15.
     */
    private static final int FIFTEEN = 15;
    /**
     * Number 16.
     */
    private static final int SIXTEEN = 16;
    /**
     * Number 17.
     */
    private static final int SEVENTEEN = 17;
    /**
     * Number 18.
     */
    private static final int EIGHTEEN = 18;
    /**
     * Number 19.
     */
    private static final int NINETEEN = 19;



    /**
     * Utility Class.
     */
    private NumberParser() {
    }

    /**
     * Reads the number from reader.
     * Reader will stop at the next to the end of number.
     *
     * @param contentExtractor
     * @param reader
     * @param startingChar
     * @return number
     * @throws IOException
     */
    public static Number getNumber(
            final Json.ContentExtractor contentExtractor,
                                   final Reader reader,
                                   final char startingChar)
            throws IOException {

        final StringBuilder builder = new StringBuilder(TEN);
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
                StringBuilder decimals = new StringBuilder(TEN);
                while ((character = (char) reader.read()) != ','
                        && (Character.isDigit(character)
                        || character == '-'
                        || character == '+'
                        || character == 'e'
                        || character == 'E')
                        && character != '}'
                        && character != ']'

                        && !isSpace(character)) {
                    decimals.append(character);
                }
                contentExtractor.setCursor(character);
                return getDecimalNumber(startingChar, builder, decimals);
            } else { // Exponential Non Decimal Number
                builder.append(character);
                while ((character = (char) reader.read()) != ','
                        && (Character.isDigit(character)
                        || character == '-'
                        || character == '+'
                        || character == 'e'
                        || character == 'E')
                        && character != '}'
                        && character != ']'
                        && !isSpace(character)) {
                    builder.append(character);
                }
                contentExtractor.setCursor(character);
                return getExponentialNumber(startingChar, builder);
            }
        } else {
            contentExtractor.setCursor(character);
            return buildNumber(startingChar, builder);
        }
    }


    /**
     * Gets Compact Number of a source String.
     * @param source
     * @param isNegative
     * @return Byte, Short, Integer, Long or BigInteger
     */
    private static Number parseNumber(final String source,
                                     final boolean isNegative) {
        return switch (source.length()) {
            case ONE, TWO
                    -> isNegative ? (byte) -Byte.parseByte(source)
                    : Byte.parseByte(source);
            case THREE -> getByteOrShort(source, isNegative);
            case FOUR -> isNegative ? (short) -Short.parseShort(source)
                    : Short.parseShort(source);
            case FIVE -> getShortOrInteger(source, isNegative);
            case SIX, SEVEN, EIGHT, NINE
                    -> isNegative ? -Integer.parseUnsignedInt(source)
                    : Integer.parseUnsignedInt(source);
            case TEN -> getIntegerOrLong(source, isNegative);
            case ELEVEN, TWELVE, THIRTEEN, FOURTEEN, FIFTEEN, SEVENTEEN,
                    SIXTEEN, EIGHTEEN -> isNegative
                    ? -Long.parseUnsignedLong(source)
                            : Long.parseUnsignedLong(source);
            case NINETEEN -> getLongOrBigNumber(source, isNegative);
            default -> isNegative ? new BigInteger("-" + source)
                    : new BigInteger(source);
        };
    }



    private static Number getByteOrShort(final String source,
                                  final boolean isNegative) {
        short aShort = Short.parseShort(source);
        if (isNegative) {
            aShort = (short) -aShort;
            if (aShort >= Byte.MIN_VALUE) {
                return (byte) aShort;
            }
            return aShort;
        } else {
            if (aShort <= Byte.MAX_VALUE) {
                return (byte) aShort;
            }
            return aShort;
        }
    }

    private static Number getShortOrInteger(final String source,
                                     final boolean isNegative) {
        int integer = Integer.parseUnsignedInt(source);
        if (isNegative) {
            integer = -integer;
            if (integer >= Short.MIN_VALUE) {
                return (short) integer;
            }
            return integer;
        } else {
            if (integer <= Short.MAX_VALUE) {
                return (short) integer;
            }
            return integer;
        }
    }

    private static Number getIntegerOrLong(final String source,
                                    final boolean isNegative) {
        long aLong = Long.parseUnsignedLong(source);
        if (isNegative) {
            aLong = -aLong;
            if (aLong >= Integer.MIN_VALUE) {
                return (int) aLong;
            }
            return aLong;
        } else {
            if (aLong <= Integer.MAX_VALUE) {
                return (int) aLong;
            }
            return aLong;
        }
    }

    private static Number getLongOrBigNumber(final String source,
                                      final boolean isNegative) {
        BigInteger bigInteger = new BigInteger(source);
        if (isNegative) {
            bigInteger = bigInteger.multiply(new BigInteger("-1"));
            if (bigInteger.compareTo(BigInteger.valueOf(Long.MIN_VALUE)) < -1) {
                return bigInteger.longValue();
            }
            return bigInteger;
        } else {
            if (bigInteger.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > -1) {
                return bigInteger.longValue();
            }
            return bigInteger;
        }
    }

    /**
     * Gets Compact decimal Number of a source String.
     * @param source
     * @return Float,Double or BigDecimal
     */
    private static Number parseDecimalNumber(final String source) {
        BigDecimal bigDecimal = new BigDecimal(source);
        // TODO Better Way to check if this is float / double
        try {
            if (bigDecimal
                    .equals(new BigDecimal(
                            Float.toString(bigDecimal.floatValue())))) {
                return bigDecimal.floatValue();
            }
            if (bigDecimal
                    .equals(BigDecimal.valueOf(bigDecimal.doubleValue()))) {
                return bigDecimal.doubleValue();
            }
        } catch (java.lang.NumberFormatException ne) {
            return bigDecimal;
        }
        return bigDecimal;
    }

    /**
     * Gets Decimal Number from the String.
     *
     * @param decimal
     * @param startingChar
     * @param builder
     * @return number
     */
    private static Number getDecimalNumber(final char startingChar,
                                    final StringBuilder builder,
                                    final StringBuilder decimal) {
        return NumberParser.parseDecimalNumber(startingChar
                + builder.toString() + "." + decimal.toString());
    }

    /**
     * Builds Number from the String.
     *
     * @param startingChar
     * @param builder
     * @return number
     */
    private static Number buildNumber(final char startingChar,
                             final StringBuilder builder) {
        return switch (startingChar) {
            case '-' -> NumberParser.parseNumber(builder.toString(), true);
            case '+' -> NumberParser.parseNumber(builder.toString(), false);
            default -> NumberParser.parseNumber(builder
                    .insert(0, startingChar)
                    .toString(), false);
        };
    }

    /**
     * Gets Decimal Exponential from the String.
     *
     * @param startingChar
     * @param builder
     * @return number
     */
    private static Number getExponentialNumber(final char startingChar,
                                        final StringBuilder builder) {
        return new BigDecimal(startingChar + builder.toString());
    }

}
