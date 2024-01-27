package com.techatpark.sjson.generator.example;

import java.util.List;
import java.util.Map;

public class ComplexPojo {
    private int integerValue;
    private double doubleValue;
    private boolean booleanValue;
    private String stringValue;
    private List<String> stringList;
    private Map<String, Integer> stringIntegerMap;
    private NestedObject nestedObject;
    private int[] intArray;
    private NestedObject[] nestedObjectArray;

    // Constructors, getters, and setters can be added as needed.

    public static class NestedObject {
        private String nestedStringValue;
        private boolean nestedBooleanValue;

        // Constructors, getters, and setters can be added as needed.
    }
}
