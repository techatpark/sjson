//package com.techatpark.sjson.schema.generator;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.github.reinert.jjschema.v1.JsonSchemaFactory;
//import com.github.reinert.jjschema.v1.JsonSchemaV4Factory;
//import com.techatpark.sjson.generator.JsonSchemaGenerator;
//import com.techatpark.sjson.schema.generator.model.Product;
//import org.junit.jupiter.api.Test;
//
//public class GenartorTest {
//    @Test
//    void testGenerator() {
//        JsonSchemaFactory schemaFactory = new JsonSchemaV4Factory();
//        schemaFactory.setAutoPutDollarSchema(true);
//        JsonNode productSchema = schemaFactory.createSchema(Product.class);
//        System.out.println(productSchema);
//
//        JsonSchemaGenerator jsonSchemaGenerator = new JsonSchemaGenerator();
//
//        System.out.println("\n\n\nYour Output\n================\n");
//
//        System.out.println(jsonSchemaGenerator.create(Product.class));
//    }
//}
