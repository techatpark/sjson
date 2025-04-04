package com.techatpark.sjson.element;

import com.techatpark.sjson.Json;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Parser for Numbers.
 */
public final class JsonNumber implements Json<Number> {

    /**
     * Maximum Characters for a Number.
     */
    public static final int DEFAULT_MAX_NUM_LEN = 1000;
    /**
     * Number Capacity (Default).
     */
    public static final int CAPACITY = 10;

    /**
     * Json Number.
     */
    private final StringBuilder numberBuilder;

    /**
     * Reads the number from reader.
     * Reader will stop at the next to the end of number.
     *
     * @param parser
     * @param reader
     * @param startingChar
     * @throws IOException
     */
    public JsonNumber(
            final Parser parser,
            final Reader reader,
            final char startingChar)
            throws IOException {
        numberBuilder = new StringBuilder(CAPACITY);
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

        parser.setCursor(charactor);
    }

    @Override
    public Number read() {
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

    private Number parseBigNumber(final String numberStr) {
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
