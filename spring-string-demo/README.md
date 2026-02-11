# Advanced String Handling in Spring Boot

This Spring Boot application demonstrates advanced string handling techniques and JVM memory management concepts specifically designed for senior developers.

## Key Features

1. **String Interning**: Efficient string comparison using interned constants
2. **Memory Optimization**: Thread-local StringBuilder reuse to minimize allocation
3. **Pattern Caching**: Pre-compiled regex patterns for efficient validation
4. **Large String Handling**: Safe processing of potentially large string inputs
5. **Batch Processing**: Efficient parallel stream processing of string collections

## Advanced Concepts Demonstrated

- JVM String constant pool utilization
- Memory-efficient string operations
- String deduplication techniques
- Safe handling of large string inputs
- Performance optimization strategies

## Endpoints

- `POST /api/string-demo/process-batch` - Process a batch of strings efficiently
- `POST /api/string-demo/concatenate` - Efficient string concatenation
- `GET /api/string-demo/validate-email/{email}` - Email validation with cached pattern
- `POST /api/string-demo/process-large-string` - Safe processing of large strings
- `GET /api/string-demo/status-check/{status}` - Fast status comparison using interning
- `GET /api/string-demo/intern-string/{input}` - Thread-safe string interning

## JVM Tuning Recommendations

For production deployment with heavy string processing:

```
-XX:+UseG1GC
-XX:+G1UseStringDeduplication
-XX:MaxGCPauseMillis=200
-Xmx4g
-Xms2g
```

## Running the Application

```bash
mvn spring-boot:run
```

The application will start on port 8080.