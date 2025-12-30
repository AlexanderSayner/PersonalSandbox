package org.sandbox.bookshop.graphql

import org.sandbox.bookshop.dto.*
import org.sandbox.bookshop.service.BookshopService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.stereotype.Controller
import java.util.*

@Controller
class MutationResolver(
    private val bookshopService: BookshopService
) {

    // Product mutations
    @MutationMapping
    fun createProduct(@Argument input: Map<String, Any>): org.sandbox.bookshop.dto.ProductDto {
        val productDto = _root_ide_package_.org.sandbox.bookshop.dto.ProductDto(
            title = input["title"] as String,
            description = input["description"] as? String,
            price = input["price"] as? java.math.BigDecimal,
            productType = input["productType"] as String,
            libraryBookId = input["libraryBookId"] as? UUID
        )
        return bookshopService.createProduct(productDto)
    }

    @MutationMapping
    fun updateProduct(@Argument id: UUID, @Argument input: Map<String, Any>): org.sandbox.bookshop.dto.ProductDto? {
        val productDto = _root_ide_package_.org.sandbox.bookshop.dto.ProductDto(
            title = input["title"] as? String ?: "",
            description = input["description"] as? String,
            price = input["price"] as? java.math.BigDecimal,
            productType = input["productType"] as? String ?: "",
            libraryBookId = input["libraryBookId"] as? UUID
        )
        return bookshopService.updateProduct(id, productDto)
    }

    @MutationMapping
    fun deleteProduct(@Argument id: UUID): Boolean {
        return bookshopService.deleteProduct(id)
    }

    // Order mutations
    @MutationMapping
    fun createOrder(@Argument input: Map<String, Any>): org.sandbox.bookshop.dto.OrderDto {
        val orderDto = _root_ide_package_.org.sandbox.bookshop.dto.OrderDto(
            userId = input["userId"] as UUID,
            status = input["status"] as String,
            totalAmount = input["totalAmount"] as java.math.BigDecimal
        )
        return bookshopService.createOrder(orderDto)
    }

    @MutationMapping
    fun updateOrder(@Argument id: UUID, @Argument input: Map<String, Any>): org.sandbox.bookshop.dto.OrderDto? {
        val orderDto = _root_ide_package_.org.sandbox.bookshop.dto.OrderDto(
            userId = input["userId"] as? UUID,
            status = input["status"] as? String ?: "",
            totalAmount = input["totalAmount"] as? java.math.BigDecimal
        )
        return bookshopService.updateOrder(id, orderDto)
    }

    @MutationMapping
    fun deleteOrder(@Argument id: UUID): Boolean {
        return bookshopService.deleteOrder(id)
    }

    // OrderItem mutations
    @MutationMapping
    fun createOrderItem(@Argument input: Map<String, Any>): org.sandbox.bookshop.dto.OrderItemDto {
        val orderItemDto = _root_ide_package_.org.sandbox.bookshop.dto.OrderItemDto(
            orderId = input["orderId"] as UUID,
            productId = input["productId"] as UUID,
            quantity = (input["quantity"] as? Number)?.toInt() ?: 0,
            price = input["price"] as java.math.BigDecimal
        )
        return bookshopService.createOrderItem(orderItemDto)
    }

    @MutationMapping
    fun updateOrderItem(@Argument id: UUID, @Argument input: Map<String, Any>): org.sandbox.bookshop.dto.OrderItemDto? {
        val orderItemDto = _root_ide_package_.org.sandbox.bookshop.dto.OrderItemDto(
            orderId = input["orderId"] as? UUID,
            productId = input["productId"] as? UUID,
            quantity = (input["quantity"] as? Number)?.toInt() ?: 0,
            price = input["price"] as? java.math.BigDecimal
        )
//TODO        return bookshopService.updateOrderItem(id, orderItemDto)
        return orderItemDto
    }

    @MutationMapping
    fun deleteOrderItem(@Argument id: UUID): Boolean {
//TODO        return bookshopService.deleteOrderItem(id)
        return false
    }
}