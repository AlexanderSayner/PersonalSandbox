package org.sandbox.workshop.service

import org.sandbox.workshop.exception.ProductNotFoundException
import org.sandbox.workshop.grpc.BookshopGrpcClient
import org.sandbox.workshop.model.PhysicalInventory
import org.sandbox.workshop.repository.PhysicalInventoryRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class PhysicalInventoryService(
    private val physicalInventoryRepository: PhysicalInventoryRepository,
    private val bookshopGrpcClient: BookshopGrpcClient
) {

    fun getAll(): List<PhysicalInventory> {
        return physicalInventoryRepository.findAll().toList()
    }

    fun getById(productId: UUID): PhysicalInventory? {
        return physicalInventoryRepository.findById(productId).orElse(null)
    }

    fun create(inventory: PhysicalInventory): PhysicalInventory {
        // Validate product exists in bookshop service via gRPC
        if (!bookshopGrpcClient.validateProduct(inventory.productId.toString())) {
            throw ProductNotFoundException("Product ${inventory.productId} does not exist in Bookshop service")
        }
        
        return physicalInventoryRepository.save(inventory)
    }

    fun update(productId: UUID, inventory: PhysicalInventory): PhysicalInventory {
        // Validate product exists in bookshop service via gRPC
        if (!bookshopGrpcClient.validateProduct(productId.toString())) {
            throw ProductNotFoundException("Product $productId does not exist in Bookshop service")
        }
        
        // Ensure the product ID in the entity matches the path parameter
        if (inventory.productId != productId) {
            throw IllegalArgumentException("Product ID in body does not match path parameter")
        }
        
        return physicalInventoryRepository.save(inventory)
    }

    fun delete(productId: UUID): Boolean {
        if (!physicalInventoryRepository.existsById(productId)) {
            return false
        }
        physicalInventoryRepository.deleteById(productId)
        return true
    }
}