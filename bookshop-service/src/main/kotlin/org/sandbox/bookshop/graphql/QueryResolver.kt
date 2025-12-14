package org.sandbox.bookshop.graphql

import org.sandbox.bookshop.graphql.integration.JavaEeHttpService
import org.sandbox.bookshop.service.BookshopService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import java.util.*

@Controller
class QueryResolver(
    val bookshopService: BookshopService,
    val javaEeHttpService: JavaEeHttpService
) {

    // Product queries
    @QueryMapping
    fun products(): List<org.sandbox.bookshop.dto.ProductDto> {
        return bookshopService.getAllProducts()
    }

    @QueryMapping
    fun product(@Argument id: UUID): org.sandbox.bookshop.dto.ProductDto? {
        return bookshopService.getProductById(id)
    }

    // Query to get product with extended book information from Java EE service
    @QueryMapping
    fun productWithBookInfo(@Argument id: UUID): Map<String, Any>? {
        return bookshopService.getProductWithBookInfo(id)
    }

    // Order queries
    @QueryMapping
    fun orders(): List<org.sandbox.bookshop.dto.OrderDto> {
        return bookshopService.getAllOrders()
    }

    @QueryMapping
    fun order(@Argument id: UUID): org.sandbox.bookshop.dto.OrderDto? {
        return bookshopService.getOrderById(id)
    }

    // OrderItem queries
    @QueryMapping
    fun orderItems(): List<org.sandbox.bookshop.dto.OrderItemDto> {
        return bookshopService.getAllOrderItems()
    }

    @QueryMapping
    fun orderItem(@Argument id: UUID): org.sandbox.bookshop.dto.OrderItemDto? {
        return bookshopService.getOrderItemById(id)
    }

    // Book query that connects to Java EE service via HTTP/GraphQL
    @QueryMapping
    fun book(@Argument id: Int): Map<String, Any>? {
        val bookInfo = javaEeHttpService.getBookById(id)
        return bookInfo.let {
            mapOf(
                "id" to it.id,
                "title" to it.title,
                "author" to it.author,
                "year" to it.year
            )
        }
    }
}