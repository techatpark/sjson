package com.techatpark.sjson.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.elements.GivenMethodsConjunction;
import org.junit.jupiter.api.Test;


import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

/**
 * This will make sure we have all the review comments met.
 */
public class CodeReviewTest {

    @Test
    public void some_architecture_rule() {
        JavaClasses classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_JARS)
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_ARCHIVES)
                .importPackages("com.techatpark.sjson");


        ArchRule rule = classes().that().resideInAPackage("com.techatpark.sjson")
                .should().onlyDependOnClassesThat()
                .resideInAnyPackage("java.io"
                        ,"java.math"
                        ,"java.util"
                        ,"jakarta.validation"
                        ,"java.lang"
                        ,"java.lang.reflect"
                        ,"com.techatpark.sjson",
                        "com.techatpark.sjson.*");

        rule.check(classes);

    }
}
