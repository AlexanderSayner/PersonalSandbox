package com.example.stringdemo.controller;

import com.example.stringdemo.service.AdvancedStringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/string-demo")
public class StringDemoController {
    
    @Autowired
    private AdvancedStringService stringService;
    
    @PostMapping("/process-batch")
    public ResponseEntity<List<String>> processStringBatch(@RequestBody List<String> inputs) {
        List<String> result = stringService.processStringBatch(inputs);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/concatenate")
    public ResponseEntity<String> concatenateStrings(@RequestBody String[] parts) {
        String result = stringService.concatenateStrings(parts);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/validate-email/{email}")
    public ResponseEntity<Boolean> validateEmail(@PathVariable String email) {
        boolean isValid = stringService.isValidEmail(email);
        return ResponseEntity.ok(isValid);
    }
    
    @PostMapping("/process-large-string")
    public ResponseEntity<String> processLargeString(@RequestBody String input) {
        // Check for potentially dangerous large inputs
        if (input != null && input.length() > 1_000_000) { // 1MB limit
            return ResponseEntity.badRequest().body("String too large");
        }
        
        String result = stringService.processLargeString(input);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/status-check/{status}")
    public ResponseEntity<Boolean> checkStatus(@PathVariable String status) {
        boolean isActive = stringService.isUserActive(status);
        return ResponseEntity.ok(isActive);
    }
    
    @GetMapping("/intern-string/{input}")
    public ResponseEntity<String> internString(@PathVariable String input) {
        String interned = stringService.internString(input);
        return ResponseEntity.ok(interned);
    }
}