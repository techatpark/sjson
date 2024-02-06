/**
 * info.java file.
 */
module json.parser {
    requires java.base;
    requires jakarta.validation;
    requires junit;

    opens com.techatpark.sjson.core.util;
    opens com.techatpark.sjson.schema.generator;
    opens com.techatpark.sjson.schema;
}