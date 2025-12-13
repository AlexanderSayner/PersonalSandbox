package org.sandbox.workshop.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.sandbox.workshop.model.PhysicalInventory
import org.sandbox.workshop.service.PhysicalInventoryService
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
@RequestMapping("/api/physical-inventory")
@Tag(name = "Physical Inventory", description = "Physical Inventory Management API")
class PhysicalInventoryController(private val physicalInventoryService: PhysicalInventoryService) {

    @Operation(summary = "Get all physical inventory records")
    @GetMapping
    fun getAll(): List<PhysicalInventory> {
        return physicalInventoryService.getAll()
    }

    @Operation(summary = "Get physical inventory by product ID")
    @GetMapping("/{productId}")
    fun getById(@PathVariable productId: UUID): ResponseEntity<PhysicalInventory> {
        val inventory = physicalInventoryService.getById(productId)
        return if (inventory != null) {
            ResponseEntity.ok(inventory)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @Operation(summary = "Create a new physical inventory record")
    @PostMapping
    fun create(@RequestBody inventory: PhysicalInventory): ResponseEntity<PhysicalInventory> {
        try {
            val savedInventory = physicalInventoryService.create(inventory)
            return ResponseEntity.status(HttpStatus.CREATED).body(savedInventory)
        } catch (e: Exception) {
            return ResponseEntity.badRequest().build()
        }
    }

    @Operation(summary = "Update a physical inventory record")
    @PutMapping("/{productId}")
    fun update(
        @PathVariable productId: UUID,
        @RequestBody inventory: PhysicalInventory
    ): ResponseEntity<PhysicalInventory> {
        try {
            val updatedInventory = physicalInventoryService.update(productId, inventory)
            return ResponseEntity.ok(updatedInventory)
        } catch (e: Exception) {
            return ResponseEntity.badRequest().build()
        }
    }

    @Operation(summary = "Delete a physical inventory record")
    @DeleteMapping("/{productId}")
    fun delete(@PathVariable productId: UUID): ResponseEntity<Void> {
        val deleted = physicalInventoryService.delete(productId)
        return if (deleted) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}