package com.techatpark.sjson.schema;

/**
 * Provides a way to define our type
 * our json will be carrying.
 */
public enum JsonType {
    /**
     * string json type.
     */
    STRING("string"),
    /**
     *  float or double i.e number json type.
     */
    NUMBER ("number"),
    /**
     *  an integer json type.
     */
    INTEGER("integer"),
    /**
     *  object json type.
     */
    OBJECT("object"),
    /**
     *  array json type.
     */
    ARRAY("array"),
    /**
     *  boolean json type.
     */
    BOOLEAN("boolean"),
    /**
     *  a null json type.
     */
    NULL("null");

    public String getType() {
        return type;
    }

    private String type;


    JsonType(String s) {
        type=s;
    }

}