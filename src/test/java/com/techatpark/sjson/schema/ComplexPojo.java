package com.techatpark.sjson.schema;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public class ComplexPojo {
    private int integerValue;
    private double doubleValue;
    private boolean booleanValue;
    private String stringValue;
    private List<String> stringList;
    private List<Character> characterList;
    private List<Integer> integerList;
    private List<Long> longList;
    private List<Double> doubleList;
    private List<Float> floatList;
    private List<Boolean> booleanList;
    private List<BigInteger> bigIntegerList;
    private List<BigDecimal> bigDecimalList;

    //    private Map<String, Integer> stringIntegerMap;
//    private NestedObject nestedObject;
    private int[] intArray;
    private Integer[] intWrapperArray;
    private BigInteger[] bigIntegerArray;
    //    private NestedObject[] nestedObjectArray;
    private long[] longArray;
    private Long[] longWrapperArray;
    private char[] charArray;
    private Character[] characterArray;
    private float[] floatArray;
    private Float[] floatWrapperArray;
    private double[] doubleArray;
    private Double[] doubleWrapperArray;
    private String[] stringArray;
    // Constructors, getters, and setters can be added as needed.
    private BigDecimal[] bigDecimalArray;

    public static class NestedObject {
        private String nestedStringValue;
        private boolean nestedBooleanValue;

        // Constructors, getters, and setters can be added as needed.
    }
}
