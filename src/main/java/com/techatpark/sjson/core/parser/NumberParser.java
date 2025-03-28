package com.techatpark.sjson.core.parser;

import com.techatpark.sjson.core.Json;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Parser for Numbers.
 */
public final class NumberParser {

    /**
     * Maximum Characters for a Number.
     */
    public static final int DEFAULT_MAX_NUM_LEN = 1000;

    /**
     * Utility Class.
     */
    private NumberParser() {
    }

    /**
     * Reads the number from reader.
     * Reader will stop at the next to the end of number.
     *
     * @param contextExtractor
     * @param reader
     * @param startingChar
     * @return number
     * @throws IOException
     */
    public static Number getNumber(
            final Json.ContextExtractor contextExtractor,
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

        contextExtractor.setCursor(charactor);
        return build(numberBuilder);
    }


    private static Number build(final StringBuilder numberBuilder) {
        String numberStr = numberBuilder.toString().trim();
        try {
            // Try to parse as different types based on the range
            if (numberStr.contains(".")) {
                // Check if the number is a decimal
                double doubleValue = Double.parseDouble(numberStr);
                if (doubleValue >= -Float.MAX_VALUE
                        && doubleValue <= Float.MAX_VALUE) {
                    return (float) doubleValue;
                } else {
                    return doubleValue;
                }
            } else {
                // Check if the number is an integer
                if (numberBuilder.length() > DEFAULT_MAX_NUM_LEN) {
                    throw new IllegalArgumentException(
                            "Number value length exceeds the maximum allowed ("
                            + DEFAULT_MAX_NUM_LEN + ")");
                }
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
                } else {
                    return longValue;
                }
            }
        } catch (NumberFormatException e) {
            return parseBigNumber(numberStr);
        }
    }

    private static Number parseBigNumber(final String numberStr) {
        try {
            return new BigInteger(numberStr); // Try BigInteger first
        } catch (NumberFormatException e) {
            // Only create BigDecimal if BigInteger fails
            BigDecimal bd = new BigDecimal(numberStr);
            try {
                // Convert to BigInteger if there's no fraction
                return bd.toBigIntegerExact();
            } catch (ArithmeticException ex) {
                return bd; // If it's a decimal, return BigDecimal
            }
        }
    }

}
