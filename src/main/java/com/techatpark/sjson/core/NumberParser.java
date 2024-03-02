package com.techatpark.sjson.core;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Parser for Numbers.
 */
public final class NumberParser {

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
        final StringBuilder numberBuilder = new StringBuilder(10);
        numberBuilder.append(startingChar);
        int read = reader.read();
        char charactor = (char) read;
        while (read  != -1
                && charactor != ','
                    && charactor != '}'
                                    && charactor != ']') {
            numberBuilder.append(charactor);
            read = reader.read();
            charactor = (char) read;
        }

        contentExtractor.setCursor(charactor);
        return build(numberBuilder);
    }


    private static Number build(final StringBuilder numberBuilder) {
        String numberStr = numberBuilder.toString().trim();
        try {
            // Try to parse as different types based on the range
            if (numberStr.contains(".")) {
                // Check if the number is a decimal
                double doubleValue = Double.parseDouble(numberStr);
                if (doubleValue >= Float.MIN_VALUE
                        && doubleValue <= Float.MAX_VALUE) {
                    return (float) doubleValue;
                } else {
                    return doubleValue;
                }
            } else {
                // Check if the number is an integer
                long longValue = Long.parseLong(numberStr);
                if (longValue >= Byte.MIN_VALUE
                        && longValue <= Byte.MAX_VALUE) {
                    return (byte) longValue;
                } else if (longValue >= Short.MIN_VALUE
                        && longValue <= Short.MAX_VALUE) {
                    return (short) longValue;
                } else if (longValue >= Integer.MIN_VALUE
                        && longValue <= Integer.MAX_VALUE) {
                    return (int) longValue;
                } else if (longValue <= Long.MAX_VALUE) {
                    return longValue;
                } else {
                    // For very large integers, use BigInteger
                    return new BigInteger(numberStr);
                }
            }
        } catch (NumberFormatException e) {
            // If parsing fails, return BigDecimal
            return new BigDecimal(numberStr);
        }
    }

}
