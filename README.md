[![Build](https://github.com/techatpark/sjson/actions/workflows/develop.yml/badge.svg)](https://github.com/techatpark/sjson/actions/workflows/develop.yml)

# SJson

**SJson** is a lightweight, high-performance JSON parser built for server-side Java. Designed for REST APIs and microservices, it delivers faster parsing with lower memory usage using native Java structures.

---

## ⚡ Key Features

- 🚫 No external dependencies
- 📦 Uses native Java types (`Map`, `List`, etc.)
- 🚀 Optimized for fast serialization & deserialization
- ✅ Assumes valid JSON (minimal validation overhead)
- 🧠 Clean, modern, and extendable Java code

> 💡 **Note:** SJson is purpose-built for backend systems—not a general-purpose parser.

Here’s the updated section with **Data Engineering workloads** added, keeping it concise and aligned with the tone:

---

### Ideal For

- Microservices: Service-to-service communication
- Client SDKs: Lightweight JSON processing (e.g., Elastic clients)
- Data Engineering: ETL pipelines, streaming ingestion, compact intermediate JSON parsing

---

Would you also like a brief usage example in a data processing context (like parsing large streaming JSON records)?

---

## 📦 Maven Dependency

```xml
<dependency>
   <groupId>com.techatpark.sjson</groupId>
   <artifactId>json-parser</artifactId>
   <version>{{version}}</version>
</dependency>
```

---

## 🛠️ Usage Example

```java
import java.io.StringReader;

// Parse JSON Object
Object obj = Json.read(new StringReader("{ \"abc\" : \"def\" }"));  // Map<String, Object>

// Parse JSON Array
Object arr = Json.read(new StringReader("[1, true, { \"abc\" : \"def\" }]"));  // List<Object>
```

---

## 🎓 Learning Resource

SJson was featured at the **Bangalore Open Source Java User Group** to teach students the internals of JSON parsers.

📺 **Watch the tech talk** (video coming soon)

---
