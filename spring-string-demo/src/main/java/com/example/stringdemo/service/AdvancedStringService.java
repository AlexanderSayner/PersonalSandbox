package com.example.stringdemo.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AdvancedStringService {
    
    // Interned constants for fast comparison
    private static final String ACTIVE = "ACTIVE".intern();
    private static final String INACTIVE = "INACTIVE".intern();
    
    // Cache for frequently used strings
    private final Map<String, String> internedCache = new ConcurrentHashMap<>();
    
    // Pre-compiled patterns for efficiency
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
    );
    
    // Thread-local StringBuilder for efficient string building
    private static final ThreadLocal<StringBuilder> STRING_BUILDER_CACHE = 
        ThreadLocal.withInitial(() -> new StringBuilder(256));
    
    /**
     * Process a large batch of strings efficiently
     */
    public List<String> processStringBatch(List<String> inputs) {
        return inputs.parallelStream()
            .filter(Objects::nonNull)
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .map(String::toLowerCase)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }
    
    /**
     * Efficient string concatenation using cached StringBuilder
     */
    public String concatenateStrings(String... parts) {
        StringBuilder sb = STRING_BUILDER_CACHE.get();
        sb.setLength(0); // Clear without reallocating
        
        for (String part : parts) {
            sb.append(part);
        }
        
        return sb.toString();
    }
    
    /**
     * Validate email efficiently using cached pattern
     */
    public boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Efficient status comparison using string interning
     */
    public boolean isUserActive(String status) {
        if (status == null) return false;
        
        String internedStatus = internString(status);
        return ACTIVE == internedStatus;
    }
    
    /**
     * Thread-safe string interning with caching
     */
    public String internString(String input) {
        if (input == null) return null;
        return internedCache.computeIfAbsent(input, String::intern);
    }
    
    /**
     * Process large string with memory considerations
     */
    public String processLargeString(String input) {
        if (input == null) return null;
        
        // For very large strings, be careful about creating substrings
        // In Java 8+, substring creates new char[] so memory usage is optimized
        if (input.length() > 10000) {
            // Process in chunks to avoid memory issues
            return input.substring(0, Math.min(input.length(), 10000))
                      .trim()
                      .toLowerCase();
        }
        
        return input.trim().toLowerCase();
    }
    
    /**
     * Memory-efficient string storage for similar strings
     */
    public static class CompressedStringStore {
        private final Map<String, List<String>> prefixMap = new HashMap<>();
        
        public void store(String fullString) {
            if (fullString.length() < 3) {
                prefixMap.computeIfAbsent("", l -> new ArrayList<>()).add(fullString);
                return;
            }
            
            String prefix = fullString.substring(0, 3);
            String suffix = fullString.substring(3);
            prefixMap.computeIfAbsent(prefix, l -> new ArrayList<>()).add(suffix);
        }
        
        public Collection<String> getAllStrings() {
            List<String> result = new ArrayList<>();
            for (Map.Entry<String, List<String>> entry : prefixMap.entrySet()) {
                for (String suffix : entry.getValue()) {
                    result.add(entry.getKey() + suffix);
                }
            }
            return result;
        }
    }
}