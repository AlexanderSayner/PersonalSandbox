package org.sandbox.bookshop.service

import org.sandbox.bookshop.dto.*
import org.sandbox.bookshop.entity.*
import org.sandbox.bookshop.repository.*
import org.springframework.stereotype.Service
import java.util.*

@Service
class BookshopService(
    private val productRepository: org.sandbox.bookshop.repository.ProductRepository,
    private val orderRepository: org.sandbox.bookshop.repository.OrderRepository,
    private val orderItemRepository: org.sandbox.bookshop.repository.OrderItemRepository
) {

    // Product methods
    fun getAllProducts(): List<org.sandbox.bookshop.dto.ProductDto> {
        return productRepository.findAll().map { mapToProductDto(it) }
    }

    fun getProductById(productId: UUID): org.sandbox.bookshop.dto.ProductDto? {
        val product = productRepository.findById(productId).orElse(null)
        return product?.let { mapToProductDto(it) }
    }

    fun createProduct(productDto: org.sandbox.bookshop.dto.ProductDto): org.sandbox.bookshop.dto.ProductDto {
        val product = _root_ide_package_.org.sandbox.bookshop.entity.Product(
            title = productDto.title,
            description = productDto.description,
            price = productDto.price,
            productType = enumValueOf<org.sandbox.bookshop.entity.ProductType>(productDto.productType.uppercase()),
            libraryBookId = productDto.libraryBookId
        )
        val savedProduct = productRepository.save(product)
        return mapToProductDto(savedProduct)
    }

    fun updateProduct(
        productId: UUID,
        productDto: org.sandbox.bookshop.dto.ProductDto
    ): org.sandbox.bookshop.dto.ProductDto? {
        val existingProduct = productRepository.findById(productId).orElse(null)
        return if (existingProduct != null) {
            existingProduct.apply {
                //TODO
//                HtmlStyles.title = productDto.title
//                PropertyInfo.Name.description = productDto.description
                price = productDto.price
                productType = enumValueOf<org.sandbox.bookshop.entity.ProductType>(productDto.productType.uppercase())
                libraryBookId = productDto.libraryBookId
            }
            val updatedProduct = productRepository.save(existingProduct)
            mapToProductDto(updatedProduct)
        } else {
            null
        }
    }

    fun deleteProduct(productId: UUID): Boolean {
        return if (productRepository.existsById(productId)) {
            productRepository.deleteById(productId)
            true
        } else {
            false
        }
    }

    // Order methods
    fun getAllOrders(): List<org.sandbox.bookshop.dto.OrderDto> {
        return orderRepository.findAll().map { mapToOrderDto(it) }
    }

    fun getOrderById(orderId: UUID): org.sandbox.bookshop.dto.OrderDto? {
        val order = orderRepository.findById(orderId).orElse(null)
        return order?.let { mapToOrderDto(it) }
    }

    fun createOrder(orderDto: org.sandbox.bookshop.dto.OrderDto): org.sandbox.bookshop.dto.OrderDto {
        val order = _root_ide_package_.org.sandbox.bookshop.entity.Order(
            userId = orderDto.userId,
            status = enumValueOf<org.sandbox.bookshop.entity.OrderStatus>(orderDto.status.uppercase()),
            totalAmount = orderDto.totalAmount
        )
        val savedOrder = orderRepository.save(order)
        return mapToOrderDto(savedOrder)
    }

    fun updateOrder(orderId: UUID, orderDto: org.sandbox.bookshop.dto.OrderDto): org.sandbox.bookshop.dto.OrderDto? {
        val existingOrder = orderRepository.findById(orderId).orElse(null)
        return if (existingOrder != null) {
            existingOrder.apply {
                userId = orderDto.userId
                status = enumValueOf<org.sandbox.bookshop.entity.OrderStatus>(orderDto.status.uppercase())
                totalAmount = orderDto.totalAmount
            }
            val updatedOrder = orderRepository.save(existingOrder)
            mapToOrderDto(updatedOrder)
        } else {
            null
        }
    }

    fun deleteOrder(orderId: UUID): Boolean {
        return if (orderRepository.existsById(orderId)) {
            orderRepository.deleteById(orderId)
            true
        } else {
            false
        }
    }

    // OrderItem methods
    fun getAllOrderItems(): List<org.sandbox.bookshop.dto.OrderItemDto> {
        return orderItemRepository.findAll().map { mapToOrderItemDto(it) }
    }

    fun getOrderItemById(orderItemId: UUID): org.sandbox.bookshop.dto.OrderItemDto? {
        val orderItem = orderItemRepository.findById(orderItemId).orElse(null)
        return orderItem?.let { mapToOrderItemDto(it) }
    }

    fun createOrderItem(orderItemDto: org.sandbox.bookshop.dto.OrderItemDto): org.sandbox.bookshop.dto.OrderItemDto {
        val orderItem = _root_ide_package_.org.sandbox.bookshop.entity.OrderItem(
            quantity = orderItemDto.quantity,
            price = orderItemDto.price
        )

        // Set relationships if IDs exist
        orderItemDto.orderId?.let { orderId ->
            val order = orderRepository.findById(orderId).orElse(null)
            order?.let { orderItem.order = it }
        }

        orderItemDto.productId?.let { productId ->
            val product = productRepository.findById(productId).orElse(null)
            product?.let { orderItem.product = it }
        }

        val savedOrderItem = orderItemRepository.save(orderItem)
        return mapToOrderItemDto(savedOrderItem)
    }

    // Method to get extended book information from Java EE service if the product is a book
    fun getProductWithBookInfo(productId: UUID): Map<String, Any>? {
        val product = productRepository.findById(productId).orElse(null)
        //TODO: If it's a book type and has a libraryBookId, try to get extended book info
        return mapOf()
    }

    // Helper mapping functions
    private fun mapToProductDto(product: org.sandbox.bookshop.entity.Product): org.sandbox.bookshop.dto.ProductDto {
        return _root_ide_package_.org.sandbox.bookshop.dto.ProductDto(
            productId = product.productId,
            title = product.title,
            description = product.description,
            price = product.price,
            productType = product.productType.name.lowercase(),
            libraryBookId = product.libraryBookId
        )
    }

    private fun mapToOrderDto(order: org.sandbox.bookshop.entity.Order): org.sandbox.bookshop.dto.OrderDto {
        return _root_ide_package_.org.sandbox.bookshop.dto.OrderDto(
            orderId = order.orderId,
            userId = order.userId,
            status = order.status.name.lowercase(),
            totalAmount = order.totalAmount
        )
    }

    private fun mapToOrderItemDto(orderItem: org.sandbox.bookshop.entity.OrderItem): org.sandbox.bookshop.dto.OrderItemDto {
        return _root_ide_package_.org.sandbox.bookshop.dto.OrderItemDto(
            orderItemId = orderItem.orderItemId,
            orderId = orderItem.order?.orderId,
            productId = orderItem.product?.productId,
            quantity = orderItem.quantity,
            price = orderItem.price
        )
    }
}