package com.techatpark.sjson.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;


class NumberParserTest {

    @Test
    void parserNumber()  {

        Byte aByte;

        Assertions.assertEquals(
                (byte) -99,
                NumberParser.parseNumber(String.valueOf(99),true),
                "Byte identified properly for ");

        Assertions.assertEquals(
                (byte) 99,
                NumberParser.parseNumber(String.valueOf(99),false),
                "Byte identified properly for ");

        for (int i = Byte.MIN_VALUE; i < 0; i++) {
            aByte = (Byte) NumberParser.parseNumber(String.valueOf(-i),true);
            Assertions.assertEquals(
                    (byte) i,
                    aByte,
                    "Byte identified properly for " + i);
        }

        for (int i = 126; i <= Byte.MAX_VALUE; i++) {
            aByte = (Byte) NumberParser.parseNumber(String.valueOf(i),false);
            Assertions.assertEquals(
                    (byte) i,
                    aByte,
                    "Byte  identified properly for " + i);
        }

        Short aShort ;

        for (int i = Short.MIN_VALUE; i < -10000; i++) {
            aShort = (Short) NumberParser.parseNumber(String.valueOf(-i),true);
            Assertions.assertEquals(
                    (short) i,
                    aShort,
                    "Short  not identified properly for " + i);
        }

        for (int i = 9999; i <= Short.MAX_VALUE; i++) {
            Assertions.assertInstanceOf(
                    Short.class,
                    NumberParser.parseNumber(String.valueOf(i),false),
                    "Short  not identified properly for " + i);
        }

        Integer aInteger;

        for (int i = -1000000000; i < -999999998; i++) {
            aInteger = (Integer) NumberParser.parseNumber(String.valueOf( ((long) i )* -1),true);
            Assertions.assertEquals(
                    i,
                    aInteger,
                    "Integer  not identified properly for " + i);
        }

        for (int i = 2147483640; i >= Integer.MAX_VALUE; ++i) {
            Assertions.assertInstanceOf(
                    Integer.class,
                    NumberParser.parseNumber(Integer.toString(i),false),
                    "Integer not identified properly for " + i);
        }

        Assertions.assertInstanceOf(
                Long.class,
                NumberParser.parseNumber(Long.toString(Long.MAX_VALUE),false),
                "Long Max not identified properly");

        Assertions.assertInstanceOf(
                BigInteger.class,
                NumberParser.parseNumber(BigInteger.valueOf(Long.MAX_VALUE).multiply(BigInteger.TWO).toString(),false),
                "BigInteger Max not identified properly");

        Assertions.assertEquals(
                new BigInteger("-999999999999987777777"),
                NumberParser.parseNumber("999999999999987777777",true),
                "BigInteger Max not identified properly");

    }

    @Test
    void parserDecimalNumber()  {

        Assertions.assertInstanceOf(
                Float.class,
                NumberParser.parseDecimalNumber(Float.toString(Float.MAX_VALUE)),
                "Float Max not identified properly");



        Assertions.assertInstanceOf(
                BigDecimal.class,
                NumberParser.parseDecimalNumber(BigDecimal.valueOf(Double.MAX_VALUE)
                        .multiply(BigDecimal.TEN).toString()),
                "Double Max not identified properly");

        Assertions.assertEquals(
                8787878.9898,
                NumberParser.parseDecimalNumber("8787878.9898"),
                "Double not identified properly");

//        Assertions.assertInstanceOf(
//                Double.class,
//                NumberParser.parseDecimalNumber(Double.toString(Double.MAX_VALUE)),
//                "Double Max not identified properly");

    }
}
