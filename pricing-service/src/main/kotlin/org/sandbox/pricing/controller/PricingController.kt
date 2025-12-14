package org.sandbox.pricing.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.sandbox.pricing.dto.PricingRequest
import org.sandbox.pricing.dto.PricingResponse
import org.sandbox.pricing.service.PricingService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/pricing")
@Tag(name = "Pricing API", description = "API for calculating product pricing with delivery costs")
class PricingController(
    private val pricingService: PricingService
) {

    // TODO: handle coroutines
    @PostMapping("/calculate")
    @Operation(
        summary = "Calculate pricing",
        description = "Calculates the total price including delivery cost based on distance and weight"
    )
    suspend fun calculatePricing(@RequestBody pricingRequest: PricingRequest): PricingResponse /*= runBlocking */ {
        pricingService.calculatePricing(pricingRequest)
        TODO("Provide the return value")
    }

    @GetMapping("/health")
    fun health(): String {
        return "Pricing service is running"
    }
}