package com.bookshop.service

import com.bookshop.dto.*
import com.bookshop.entity.*
import com.bookshop.repository.*
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

@Service
class BookshopService(
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    val javaEeHttpService: JavaEeHttpService  // Changed from private to public (val) for gRPC access
) {
    
    // Product methods
    fun getAllProducts(): List<ProductDto> {
        return productRepository.findAll().map { mapToProductDto(it) }
    }
    
    fun getProductById(productId: UUID): ProductDto? {
        val product = productRepository.findById(productId).orElse(null)
        return product?.let { mapToProductDto(it) }
    }
    
    fun createProduct(productDto: ProductDto): ProductDto {
        val product = Product(
            title = productDto.title,
            description = productDto.description,
            price = productDto.price,
            productType = enumValueOf<ProductType>(productDto.productType.uppercase()),
            libraryBookId = productDto.libraryBookId
        )
        val savedProduct = productRepository.save(product)
        return mapToProductDto(savedProduct)
    }
    
    fun updateProduct(productId: UUID, productDto: ProductDto): ProductDto? {
        val existingProduct = productRepository.findById(productId).orElse(null)
        return if (existingProduct != null) {
            existingProduct.apply {
                title = productDto.title
                description = productDto.description
                price = productDto.price
                productType = enumValueOf<ProductType>(productDto.productType.uppercase())
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
    fun getAllOrders(): List<OrderDto> {
        return orderRepository.findAll().map { mapToOrderDto(it) }
    }
    
    fun getOrderById(orderId: UUID): OrderDto? {
        val order = orderRepository.findById(orderId).orElse(null)
        return order?.let { mapToOrderDto(it) }
    }
    
    fun createOrder(orderDto: OrderDto): OrderDto {
        val order = Order(
            userId = orderDto.userId,
            status = enumValueOf<OrderStatus>(orderDto.status.uppercase()),
            totalAmount = orderDto.totalAmount
        )
        val savedOrder = orderRepository.save(order)
        return mapToOrderDto(savedOrder)
    }
    
    fun updateOrder(orderId: UUID, orderDto: OrderDto): OrderDto? {
        val existingOrder = orderRepository.findById(orderId).orElse(null)
        return if (existingOrder != null) {
            existingOrder.apply {
                userId = orderDto.userId
                status = enumValueOf<OrderStatus>(orderDto.status.uppercase())
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
    fun getAllOrderItems(): List<OrderItemDto> {
        return orderItemRepository.findAll().map { mapToOrderItemDto(it) }
    }
    
    fun getOrderItemById(orderItemId: UUID): OrderItemDto? {
        val orderItem = orderItemRepository.findById(orderItemId).orElse(null)
        return orderItem?.let { mapToOrderItemDto(it) }
    }
    
    fun createOrderItem(orderItemDto: OrderItemDto): OrderItemDto {
        val orderItem = OrderItem(
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
        return product?.let { prod ->
            val baseInfo = mapOf(
                "productId" to prod.productId,
                "title" to prod.title,
                "description" to prod.description,
                "price" to prod.price,
                "productType" to prod.productType.name.lowercase(),
                "libraryBookId" to prod.libraryBookId,
                "createdAt" to prod.createdAt,
                "updatedAt" to prod.updatedAt
            )
            
            // If it's a book type and has a libraryBookId, try to get extended book info
            if (prod.productType == ProductType.BOOK && prod.libraryBookId != null) {
                val bookId = prod.libraryBookId.toString().toIntOrNull() ?: prod.productId.hashCode() % 1000
                val bookInfo = javaEeHttpService.getBookById(bookId)
                
                if (bookInfo != null) {
                    baseInfo + mapOf(
                        "bookDetails" to mapOf(
                            "id" to bookInfo.id,
                            "title" to bookInfo.title,
                            "author" to bookInfo.author,
                            "year" to bookInfo.year
                        )
                    )
                } else {
                    baseInfo
                }
            } else {
                baseInfo
            }
        }
    }
    
    // Helper mapping functions
    private fun mapToProductDto(product: Product): ProductDto {
        return ProductDto(
            productId = product.productId,
            title = product.title,
            description = product.description,
            price = product.price,
            productType = product.productType.name.lowercase(),
            libraryBookId = product.libraryBookId
        )
    }
    
    private fun mapToOrderDto(order: Order): OrderDto {
        return OrderDto(
            orderId = order.orderId,
            userId = order.userId,
            status = order.status.name.lowercase(),
            totalAmount = order.totalAmount
        )
    }
    
    private fun mapToOrderItemDto(orderItem: OrderItem): OrderItemDto {
        return OrderItemDto(
            orderItemId = orderItem.orderItemId,
            orderId = orderItem.order?.orderId,
            productId = orderItem.product?.productId,
            quantity = orderItem.quantity,
            price = orderItem.price
        )
    }
}