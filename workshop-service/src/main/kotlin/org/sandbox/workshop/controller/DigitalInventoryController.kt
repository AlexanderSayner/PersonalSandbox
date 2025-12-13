package org.sandbox.workshop.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.sandbox.workshop.model.DigitalInventory
import org.sandbox.workshop.service.DigitalInventoryService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/digital-inventory")
@Tag(name = "Digital Inventory", description = "Digital Inventory Management API")
class DigitalInventoryController(private val digitalInventoryService: DigitalInventoryService) {

    @Operation(summary = "Get all digital inventory records")
    @GetMapping
    fun getAll(): List<DigitalInventory> {
        return digitalInventoryService.getAll()
    }

    @Operation(summary = "Get digital inventory by product ID")
    @GetMapping("/{productId}")
    fun getById(@PathVariable productId: UUID): ResponseEntity<DigitalInventory> {
        val inventory = digitalInventoryService.getById(productId)
        return if (inventory != null) {
            ResponseEntity.ok(inventory)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @Operation(summary = "Create a new digital inventory record")
    @PostMapping
    fun create(@RequestBody inventory: DigitalInventory): ResponseEntity<DigitalInventory> {
        try {
            val savedInventory = digitalInventoryService.create(inventory)
            return ResponseEntity.status(HttpStatus.CREATED).body(savedInventory)
        } catch (e: Exception) {
            return ResponseEntity.badRequest().build()
        }
    }

    @Operation(summary = "Update a digital inventory record")
    @PutMapping("/{productId}")
    fun update(
        @PathVariable productId: UUID,
        @RequestBody inventory: DigitalInventory
    ): ResponseEntity<DigitalInventory> {
        try {
            val updatedInventory = digitalInventoryService.update(productId, inventory)
            return ResponseEntity.ok(updatedInventory)
        } catch (e: Exception) {
            return ResponseEntity.badRequest().build()
        }
    }

    @Operation(summary = "Delete a digital inventory record")
    @DeleteMapping("/{productId}")
    fun delete(@PathVariable productId: UUID): ResponseEntity<Void> {
        val deleted = digitalInventoryService.delete(productId)
        return if (deleted) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}