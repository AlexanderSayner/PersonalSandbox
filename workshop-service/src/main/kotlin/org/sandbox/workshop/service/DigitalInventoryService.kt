package org.sandbox.workshop.service

import org.sandbox.workshop.exception.ProductNotFoundException
import org.sandbox.workshop.grpc.BookshopGrpcClient
import org.sandbox.workshop.model.DigitalInventory
import org.sandbox.workshop.repository.DigitalInventoryRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class DigitalInventoryService(
    private val digitalInventoryRepository: DigitalInventoryRepository,
    private val bookshopGrpcClient: BookshopGrpcClient
) {

    fun getAll(): List<DigitalInventory> {
        return digitalInventoryRepository.findAll().toList()
    }

    fun getById(productId: UUID): DigitalInventory? {
        return digitalInventoryRepository.findById(productId).orElse(null)
    }

    fun create(inventory: DigitalInventory): DigitalInventory {
        // Validate product exists in bookshop service via gRPC
        if (!bookshopGrpcClient.validateProduct(inventory.productId.toString())) {
            throw ProductNotFoundException("Product ${inventory.productId} does not exist in Bookshop service")
        }
        
        return digitalInventoryRepository.save(inventory)
    }

    fun update(productId: UUID, inventory: DigitalInventory): DigitalInventory {
        // Validate product exists in bookshop service via gRPC
        if (!bookshopGrpcClient.validateProduct(productId.toString())) {
            throw ProductNotFoundException("Product $productId does not exist in Bookshop service")
        }
        
        // Ensure the product ID in the entity matches the path parameter
        if (inventory.productId != productId) {
            throw IllegalArgumentException("Product ID in body does not match path parameter")
        }
        
        return digitalInventoryRepository.save(inventory)
    }

    fun delete(productId: UUID): Boolean {
        if (!digitalInventoryRepository.existsById(productId)) {
            return false
        }
        digitalInventoryRepository.deleteById(productId)
        return true
    }
}