# SJson

SJson is a json parser for server side workloads. It tries to get optimized memory and performance with below goals.

1. Trust the validity of json documents
2. It is just enough to say invalid, reasoning is optional
3. Represent Json in native format.
4. No external dependencies

**Note:** This is **not** general purpose parser. This is specifically written for REST Client use case. 
1. Service to Service Communications in microservices
2. Client SDK such as Elastic REST Client.

## Usage

Include below in your pom.xml

````
<dependency>
   <groupId>com.techatpark.sjson</groupId>
   <artifactId>json-parser</artifactId>
   <version>1.0.0</version>
<dependency>   
````

Below is the code to read JSON as Java Object

````
   Json json = new Json();
   Object obj = json.read(new StringReader("{ \"abc\" : \"def\" }"));
````

## Development

Below VM Options should be added for JVM. This is required to calculate the size of the objects

````
-javaagent:<<PATH_TO_JAR>>/jamm-0.4.1.jar --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.math=ALL-UNNAMED
````