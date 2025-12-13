package org.sandbox.reviewer.controller;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiDocsController {

    private final OpenAPI openAPI;

    public ApiDocsController(OpenAPI openAPI) {
        this.openAPI = openAPI;
    }

    @GetMapping("/v3/api-docs")
    public OpenAPI getApiDocs() {
        return openAPI; // Serve the OpenAPI spec
    }
}
