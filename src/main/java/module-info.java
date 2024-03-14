/**
 * info.java file.
 */
module json.parser {
    requires java.base;
    requires jakarta.validation;

    opens com.techatpark.sjson.core;
    opens com.techatpark.sjson.schema;
}