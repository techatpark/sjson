[![Build](https://github.com/techatpark/sjson/actions/workflows/develop.yml/badge.svg)](https://github.com/techatpark/sjson/actions/workflows/develop.yml)

# SJson

> **SJson** is a lightweight, high-performance JSON parser built for server-side Java. Designed for REST APIs and microservices, it delivers faster parsing with lower memory usage using native Java structures.

## Why Use SJson?

-  Optimized for fast serialization & deserialization
-  No external dependencies. Uses native Java types (`Map`, `List`, etc.)
-  Assumes valid JSON (minimal validation overhead)
-  Clean, modern, and extendable Java code

### Use cases

- **Microservices:** Service-to-service communication
- **Client SDKs:** Lightweight JSON processing (e.g., Elastic clients)
- **Data Engineering:** ETL pipelines, streaming ingestion, compact intermediate JSON parsing

## Usage

Add dependency to your project

### Maven
```xml
<dependency>
    <groupId>com.techatpark.sjson</groupId>
    <artifactId>json-parser</artifactId>
    <version>{{version}}</version>
</dependency>
```
### Gradle
```groovy
implementation 'com.techatpark.sjson:json-parser:{{version}}'
```

You can now perform serialization & deserialization

```java
Object obj = Json.parse(Reader.of("{ \"abc\" : \"def\" }"));  // Map<String, Object>

String jsonString = Json.stringify(obj);
```

## Reference

- https://www.youtube.com/watch?v=NSzRK8f7EX0&pp=ygUSSlNPTiBBUEkgQ29yZSBKYXZh
- https://www.youtube.com/watch?v=W8k9ZCrsphc&t=448s
- https://www.youtube.com/watch?v=R8Xubleffr8