[![Build](https://github.com/techatpark/sjson/actions/workflows/develop.yml/badge.svg)](https://github.com/techatpark/sjson/actions/workflows/develop.yml)

# SJson

SJson is a **lightweight tailer made json parser for server side workloads**. It tries to get optimized memory and performance with below goals.

## Design Goals

1. Optimized for Serialization, Deserialization and validation.
2. Represent Json in native java format.
3. No external dependencies
4. Trust the validity of json documents. It is just enough to say invalid, reasoning is optional
5. Utilize latest java features

**Note:** This is **not** general purpose parser. This is specifically written for REST API use cases. 

1. Service to Service Communications in microservices
2. Client SDK such as Elastic REST Client.

## Usage

Include below in your pom.xml

```xml
<dependency>
   <groupId>com.techatpark.sjson</groupId>
   <artifactId>json-parser</artifactId>
   <version>1.0.0</version>
<dependency>   
```

To read JSON as Java Object

```java
   Json json = Json;
   Object obj = json.read(new StringReader("{ \"abc\" : \"def\" }"));
```

## Development

Below VM Options should be added for JVM. This is required to calculate the size of the objects

```shell
-javaagent:<<PATH_TO_JAR>>/jamm-0.4.1.jar --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.math=ALL-UNNAMED
```
## How it works

SJson was part of the tech talk series at Bangalore Opensource Java User Group. This will give an idea behind this work.

1. Setup : https://www.youtube.com/watch?v=q_1H8ZJceA8
2. Optimization: https://www.youtube.com/watch?v=XMRaLCRfvlQ
3. Collection: https://www.youtube.com/watch?v=tMgy5PxPFQ4
