package com.techatpark.sjson;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.util.Map;

class RecordTest {

    @Test
    void testGetRecord() throws IOException {
        Reader reader = new BufferedReader(new FileReader(Path
                .of("src/test/resources/samples/student.json" ).toFile()));
        Map<String,Object> jsonMap = (Map<String, Object>) new Json().read(reader);
        Assertions.assertEquals("Sathish",new Student(jsonMap).name(),
                "JSON to Record Conversion failed");
    }

    record Student(Long id,String name,Double salary) {
        public Student(final Map<String,Object> jsonMap) {
            this((Long) jsonMap.get("id"),
                    (String) jsonMap.get("name"),(Double) jsonMap.get("salary"));
        }
    }
}
