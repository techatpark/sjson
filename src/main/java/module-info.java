/**
 * info.java file.
 */
module json.parser {
    requires java.base;
    requires java.validation;
    opens com.techatpark.sjson.util;
    opens com.techatpark.sjson.schema.generator;
    opens com.techatpark.sjson.schema;
}