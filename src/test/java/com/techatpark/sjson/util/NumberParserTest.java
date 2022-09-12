package com.techatpark.sjson.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;

class NumberParserTest {

    @Test
    void parserNumber()  {

        NumberParser parser = new NumberParser();

        Byte aByte;

        Assertions.assertEquals(
                (byte) -99,
                parser.parseNumber(String.valueOf(99),true),
                "Byte identified properly for ");

        Assertions.assertEquals(
                (byte) 99,
                parser.parseNumber(String.valueOf(99),false),
                "Byte identified properly for ");

        for (int i = Byte.MIN_VALUE; i < 0; i++) {
            aByte = (Byte) parser.parseNumber(String.valueOf(-i),true);
            Assertions.assertEquals(
                    (byte) i,
                    aByte,
                    "Byte identified properly for " + i);
        }

        for (int i = 126; i <= Byte.MAX_VALUE; i++) {
            aByte = (Byte) parser.parseNumber(String.valueOf(i),false);
            Assertions.assertEquals(
                    (byte) i,
                    aByte,
                    "Byte  identified properly for " + i);
        }

        Short aShort ;

        for (int i = Short.MIN_VALUE; i < -10000; i++) {
            aShort = (Short) parser.parseNumber(String.valueOf(-i),true);
            Assertions.assertEquals(
                    (short) i,
                    aShort,
                    "Short  not identified properly for " + i);
        }

        for (int i = 9999; i <= Short.MAX_VALUE; i++) {
            Assertions.assertInstanceOf(
                    Short.class,
                    parser.parseNumber(String.valueOf(i),false),
                    "Short  not identified properly for " + i);
        }

        Integer aInteger;

        for (int i = -1000000000; i < -999999998; i++) {
            aInteger = (Integer) parser.parseNumber(String.valueOf( ((long) i )* -1),true);
            Assertions.assertEquals(
                    i,
                    aInteger,
                    "Integer  not identified properly for " + i);
        }

        for (int i = 2147483640; i >= Integer.MAX_VALUE; ++i) {
            Assertions.assertInstanceOf(
                    Integer.class,
                    parser.parseNumber(Integer.toString(i),false),
                    "Integer not identified properly for " + i);
        }

        Assertions.assertInstanceOf(
                Long.class,
                parser.parseNumber(Long.toString(Long.MAX_VALUE),false),
                "Long Max not identified properly");

        Assertions.assertInstanceOf(
                BigInteger.class,
                parser.parseNumber(BigInteger.valueOf(Long.MAX_VALUE).multiply(BigInteger.TWO).toString(),false),
                "BigInteger Max not identified properly");

        Assertions.assertEquals(
                new BigInteger("-999999999999987777777"),
                parser.parseNumber("999999999999987777777",true),
                "BigInteger Max not identified properly");

    }

    @Test
    void parserDecimalNumber() throws ParseException {

        NumberParser parser = new NumberParser();

        Assertions.assertInstanceOf(
                Float.class,
                parser.parseDecimalNumber(Float.toString(Float.MAX_VALUE)),
                "Float Max not identified properly");

        Assertions.assertInstanceOf(
                Double.class,
                parser.parseDecimalNumber(Double.toString(Double.MAX_VALUE)),
                "Double Max not identified properly");

        Assertions.assertInstanceOf(
                BigDecimal.class,
                parser.parseDecimalNumber(BigDecimal.valueOf(Double.MAX_VALUE)
                        .multiply(BigDecimal.TEN).toString()),
                "Double Max not identified properly");

    }
}
