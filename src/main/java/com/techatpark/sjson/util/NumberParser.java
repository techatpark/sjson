package com.techatpark.sjson.util;

import java.math.BigDecimal;
import java.math.BigInteger;

public final class NumberParser {

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
     * Number 19.
     */
    public static final int NUMBER_NINETEEN = 19;

    /**
     * Utility Class.
     */
    private NumberParser() {
    }

    /**
     * Gets Compact Number of a source String.
     * @param source
     * @param isNegative
     * @return Byte, Short, Integer, Long or BigInteger
     */
    public static Number parseNumber(final String source,
                                     final boolean isNegative) {
        switch (source.length()) {
            case NUMBER_ONE:
            case NUMBER_TWO:
                return isNegative ? (byte) -Byte.parseByte(source)
                             : Byte.parseByte(source);
            case NUMBER_THREE:
                return getByteOrShort(source, isNegative);
            case NUMBER_FOUR:
                return isNegative ? (short) -Short.parseShort(source)
                        : Short.parseShort(source);
            case NUMBER_FIVE:
                return getShortOrInteger(source, isNegative);
            case NUMBER_SIX:
            case NUMBER_SEVEN:
            case NUMBER_EIGHT:
            case NUMBER_NINE:
                return isNegative ? -Integer.parseUnsignedInt(source)
                        : Integer.parseUnsignedInt(source);
            case NUMBER_TEN:
                return getIntegerOrLong(source, isNegative);
            case NUMBER_ELEVEN:
            case NUMBER_TWELVE:
            case NUMBER_THIRTEEN:
            case NUMBER_FOURTEEN:
            case NUMBER_FIFTEEN:
            case NUMBER_SIXTEEN:
            case NUMBER_SEVENTEEN:
            case NUMBER_EIGHTEEN:
                return isNegative ? -Long.parseUnsignedLong(source)
                        : Long.parseUnsignedLong(source);
            case NUMBER_NINETEEN:
                return getLongOrBigNumber(source, isNegative);
            default:
                return isNegative ? new BigInteger("-" + source)
                        : new BigInteger(source);
        }
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
    public static Number parseDecimalNumber(final String source) {
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

}
