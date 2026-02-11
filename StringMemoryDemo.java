import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Advanced String Memory Demo - Demonstrates advanced JVM string concepts
 * This class showcases the concepts discussed in the comprehensive guide
 */
public class StringMemoryDemo {
    
    // Demonstrates string interning for performance optimization
    private static final String ACTIVE = "ACTIVE".intern();
    private static final String INACTIVE = "INACTIVE".intern();
    
    // Cache for frequently used strings
    private static final Map<String, String> internedCache = new ConcurrentHashMap<>();
    
    public static void main(String[] args) {
        System.out.println("=== Advanced String Memory Demo ===\n");
        
        demonstrateStringCreationMechanisms();
        demonstrateMemoryImplications();
        demonstrateJVMOptimizations();
        demonstrateEdgeCases();
        demonstrateSpringLikeScenarios();
        demonstrateMemoryLeakPrevention();
        demonstratePerformanceOptimizations();
        
        System.out.println("\n=== Demo Completed ===");
    }
    
    /**
     * Demonstrates different string creation mechanisms and their memory implications
     */
    public static void demonstrateStringCreationMechanisms() {
        System.out.println("--- String Creation Mechanisms ---");
        
        // 1. String literal - goes to constant pool
        String literal1 = "Hello";
        String literal2 = "Hello";
        System.out.println("Literal comparison (==): " + (literal1 == literal2)); // true
        
        // 2. Using 'new' - creates object in heap
        String heapString1 = new String("Hello");
        String heapString2 = new String("Hello");
        System.out.println("New string comparison (==): " + (heapString1 == heapString2)); // false
        System.out.println("New string comparison (.equals): " + heapString1.equals(heapString2)); // true
        
        // 3. String interning
        String interned = heapString1.intern();
        System.out.println("Interned vs literal (==): " + (interned == literal1)); // true
        
        // 4. StringBuilder result - goes to heap
        StringBuilder sb = new StringBuilder();
        sb.append("Hello").append(" ").append("World");
        String sbResult = sb.toString();
        System.out.println("StringBuilder result in pool? " + (sbResult == "Hello World")); // false
        
        System.out.println();
    }
    
    /**
     * Demonstrates memory implications of string operations
     */
    public static void demonstrateMemoryImplications() {
        System.out.println("--- Memory Implications ---");
        
        // Demonstrate substring memory behavior (Java 8+)
        String largeString = "A".repeat(1000000); // Create large string
        String substring = largeString.substring(0, 10); // In Java 8+, this creates new char[]
        System.out.println("Substring: " + substring);
        
        // Show memory usage before and after
        Runtime runtime = Runtime.getRuntime();
        System.gc(); // Suggest garbage collection
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
        
        // Create many temporary strings
        List<String> tempStrings = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            tempStrings.add("Temporary string " + i);
        }
        
        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Memory increase: " + (memoryAfter - memoryBefore) / 1024 + " KB");
        
        // Clear references to allow GC
        tempStrings.clear();
        largeString = null;
        
        System.out.println();
    }
    
    /**
     * Demonstrates JVM string optimizations
     */
    public static void demonstrateJVMOptimizations() {
        System.out.println("--- JVM String Optimizations ---");
        
        // Compact strings demonstration (Java 8+)
        String asciiString = "Hello World"; // Uses byte[] internally in Java 8+
        String unicodeString = "Hello 世界"; // Uses mixed encoding
        
        System.out.println("ASCII string length: " + asciiString.length());
        System.out.println("Unicode string length: " + unicodeString.length());
        
        // String deduplication would be visible in G1GC with appropriate flags
        // Creating duplicate strings to demonstrate potential deduplication
        List<String> duplicates = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            duplicates.add("Duplicate string for deduplication test");
        }
        System.out.println("Created " + duplicates.size() + " duplicate strings");
        
        // Pattern compilation and caching
        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
        boolean isValid = emailPattern.matcher("test@example.com").matches();
        System.out.println("Email validation result: " + isValid);
        
        System.out.println();
    }
    
    /**
     * Demonstrates edge cases and performance limits
     */
    public static void demonstrateEdgeCases() {
        System.out.println("--- Edge Cases and Performance Limits ---");
        
        // Maximum string size demonstration
        int theoreticalMax = Integer.MAX_VALUE - 2;
        System.out.println("Theoretical max string length: " + theoreticalMax);
        
        // Dangerous concatenation (commented out to avoid OOM)
        /*
        try {
            String result = "";
            for (int i = 0; i < 100000; i++) {
                result = result + "a"; // Creates many temporary strings
            }
            System.out.println("Dangerous concatenation result length: " + result.length());
        } catch (OutOfMemoryError e) {
            System.out.println("Out of memory in dangerous concatenation");
        }
        */
        
        // Safe concatenation with StringBuilder
        StringBuilder safeBuilder = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            safeBuilder.append('a');
        }
        System.out.println("Safe concatenation result length: " + safeBuilder.length());
        
        // Unicode handling
        String emoji = "😀"; // Surrogate pair
        System.out.println("Emoji length (char): " + emoji.length()); // 2
        System.out.println("Emoji code point count: " + emoji.codePointCount(0, emoji.length())); // 1
        
        System.out.println();
    }
    
    /**
     * Demonstrates Spring-like scenarios with strings
     */
    public static void demonstrateSpringLikeScenarios() {
        System.out.println("--- Spring-like Scenarios ---");
        
        // Configuration property simulation
        Map<String, String> config = new HashMap<>();
        config.put("app.name", "MySpringApp");
        config.put("app.version", "1.0.0");
        config.put("database.url", "jdbc:postgresql://localhost:5432/mydb");
        
        System.out.println("Configuration loaded: " + config.size() + " properties");
        
        // Simulate template processing
        String template = "Hello {{name}}, welcome to {{app}}!";
        String processed = template
            .replace("{{name}}", "John Doe")
            .replace("{{app}}", config.get("app.name"));
        System.out.println("Processed template: " + processed);
        
        // Request/response simulation
        String requestBody = generateLargeString(10000);
        if (requestBody.length() > 5000) {
            System.out.println("Large request handled: " + requestBody.length() + " chars");
        }
        
        System.out.println();
    }
    
    /**
     * Demonstrates memory leak prevention techniques
     */
    public static void demonstrateMemoryLeakPrevention() {
        System.out.println("--- Memory Leak Prevention ---");
        
        // Thread-local StringBuilder reuse
        ThreadLocal<StringBuilder> builderCache = ThreadLocal.withInitial(
            () -> new StringBuilder(256)
        );
        
        // Reuse the same StringBuilder instance
        StringBuilder sb = builderCache.get();
        sb.setLength(0); // Clear without reallocating
        sb.append("Reused").append(" ").append("StringBuilder");
        String result = sb.toString();
        System.out.println("Reused builder result: " + result);
        
        // Proper queue management to prevent memory buildup
        Queue<String> stringQueue = new LinkedList<>();
        for (int i = 0; i < 1000; i++) {
            stringQueue.offer("Queue item " + i);
            if (stringQueue.size() > 100) {
                stringQueue.poll(); // Remove oldest
            }
        }
        System.out.println("Queue size maintained at: " + stringQueue.size());
        
        // String interning for frequently compared values
        String status1 = "ACTIVE";
        String status2 = "ACTIVE";
        boolean isSame = compareStatus(status1, status2);
        System.out.println("Status comparison result: " + isSame);
        
        System.out.println();
    }
    
    /**
     * Demonstrates performance optimization strategies
     */
    public static void demonstratePerformanceOptimizations() {
        System.out.println("--- Performance Optimizations ---");
        
        // Performance comparison: String concatenation vs StringBuilder
        int iterations = 10000;
        
        // Slow approach - string concatenation
        long startTime = System.currentTimeMillis();
        String slowResult = "";
        for (int i = 0; i < iterations; i++) {
            slowResult += "a";
        }
        long slowTime = System.currentTimeMillis() - startTime;
        
        // Fast approach - StringBuilder
        startTime = System.currentTimeMillis();
        StringBuilder fastBuilder = new StringBuilder(iterations);
        for (int i = 0; i < iterations; i++) {
            fastBuilder.append('a');
        }
        String fastResult = fastBuilder.toString();
        long fastTime = System.currentTimeMillis() - startTime;
        
        System.out.printf("String concatenation: %d ms%n", slowTime);
        System.out.printf("StringBuilder: %d ms%n", fastTime);
        System.out.printf("Performance improvement: %.2fx%n", (double) slowTime / fastTime);
        
        // Efficient string processing pipeline
        List<String> input = Arrays.asList("  HELLO  ", "  WORLD  ", "  JAVA  ", "  STRING  ");
        List<String> processed = input.parallelStream()
            .filter(Objects::nonNull)
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .map(String::toLowerCase)
            .distinct()
            .collect(Collectors.toList());
        System.out.println("Processed strings: " + processed);
        
        System.out.println();
    }
    
    /**
     * Helper method to generate a large string for testing
     */
    private static String generateLargeString(int size) {
        StringBuilder sb = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            sb.append((char) ('A' + (i % 26)));
        }
        return sb.toString();
    }
    
    /**
     * Efficient status comparison using string interning
     */
    public static boolean compareStatus(String status1, String status2) {
        // Intern frequently compared strings for fast reference comparison
        String interned1 = internString(status1);
        String interned2 = internString(status2);
        return interned1 == interned2; // Fast reference comparison
    }
    
    /**
     * Thread-safe string interning with caching
     */
    public static String internString(String input) {
        return internedCache.computeIfAbsent(input, String::intern);
    }
    
    /**
     * Demonstrates checking if a string is active using interned constants
     */
    public static boolean isUserActive(String status) {
        String internedStatus = internString(status);
        return ACTIVE == internedStatus || INACTIVE != internedStatus && status.equals(ACTIVE);
    }
}