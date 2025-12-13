package com.bookshop.graphql

import com.bookshop.dto.*
import com.bookshop.entity.ProductType
import com.bookshop.service.BookshopService
import com.bookshop.service.JavaEeHttpService
import graphql.schema.DataFetchingEnvironment
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import java.util.*

@Controller
class QueryResolver(
    private val bookshopService: BookshopService,
    private val javaEeHttpService: JavaEeHttpService
) {
    
    // Product queries
    @QueryMapping
    fun products(): List<ProductDto> {
        return bookshopService.getAllProducts()
    }
    
    @QueryMapping
    fun product(@Argument id: UUID): ProductDto? {
        return bookshopService.getProductById(id)
    }
    
    // Query to get product with extended book information from Java EE service
    @QueryMapping
    fun productWithBookInfo(@Argument id: UUID): Map<String, Any>? {
        return bookshopService.getProductWithBookInfo(id)
    }
    
    // Order queries
    @QueryMapping
    fun orders(): List<OrderDto> {
        return bookshopService.getAllOrders()
    }
    
    @QueryMapping
    fun order(@Argument id: UUID): OrderDto? {
        return bookshopService.getOrderById(id)
    }
    
    // OrderItem queries
    @QueryMapping
    fun orderItems(): List<OrderItemDto> {
        return bookshopService.getAllOrderItems()
    }
    
    @QueryMapping
    fun orderItem(@Argument id: UUID): OrderItemDto? {
        return bookshopService.getOrderItemById(id)
    }
    
    // Book query that connects to Java EE service via HTTP/GraphQL
    @QueryMapping
    fun book(@Argument id: Int): Map<String, Any>? {
        val bookInfo = javaEeHttpService.getBookById(id)
        return bookInfo?.let {
            mapOf(
                "id" to it.id,
                "title" to it.title,
                "author" to it.author,
                "year" to it.year
            )
        }
    }
}