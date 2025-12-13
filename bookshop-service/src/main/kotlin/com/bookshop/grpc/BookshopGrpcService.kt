package com.bookshop.grpc

import com.bookshop.service.BookshopService
import io.grpc.stub.StreamObserver
import net.devh.boot.grpc.server.service.GrpcService
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

@GrpcService
class BookshopGrpcService : BookshopServiceGrpc.BookshopServiceImplBase() {

    @Autowired
    private lateinit var bookshopService: BookshopService

    override fun getProduct(request: GetProductRequest, responseObserver: StreamObserver<GetProductResponse>) {
        try {
            val productId = UUID.fromString(request.productId)
            val productDto = bookshopService.getProductById(productId)

            val response = if (productDto != null) {
                val product = Product.newBuilder()
                    .setProductId(productDto.productId.toString())
                    .setTitle(productDto.title)
                    .setDescription(productDto.description)
                    .setPrice(productDto.price.toDouble())
                    .setProductTypeValue(productDto.productType.uppercase().hashCode() % 4) // Map to enum
                    .setLibraryBookId(productDto.libraryBookId?.toString() ?: "")
                    .build()

                GetProductResponse.newBuilder()
                    .setProduct(product)
                    .setSuccess(true)
                    .build()
            } else {
                GetProductResponse.newBuilder()
                    .setSuccess(false)
                    .setErrorMessage("Product not found")
                    .build()
            }

            responseObserver.onNext(response)
            responseObserver.onCompleted()
        } catch (e: Exception) {
            val response = GetProductResponse.newBuilder()
                .setSuccess(false)
                .setErrorMessage(e.message ?: "Unknown error")
                .build()
            
            responseObserver.onNext(response)
            responseObserver.onCompleted()
        }
    }

    override fun createProduct(request: CreateProductRequest, responseObserver: StreamObserver<CreateProductResponse>) {
        try {
            val productDto = com.bookshop.dto.ProductDto(
                productId = UUID.randomUUID(),
                title = request.title,
                description = request.description,
                price = java.math.BigDecimal.valueOf(request.price),
                productType = mapProductTypeFromGrpc(request.productType),
                libraryBookId = if (request.hasLibraryBookId() && request.libraryBookId.isNotEmpty()) 
                    UUID.fromString(request.libraryBookId) else null
            )

            val createdProductDto = bookshopService.createProduct(productDto)

            val product = Product.newBuilder()
                .setProductId(createdProductDto.productId.toString())
                .setTitle(createdProductDto.title)
                .setDescription(createdProductDto.description)
                .setPrice(createdProductDto.price.toDouble())
                .setProductTypeValue(createdProductDto.productType.uppercase().hashCode() % 4) // Map to enum
                .setLibraryBookId(createdProductDto.libraryBookId?.toString() ?: "")
                .build()

            val response = CreateProductResponse.newBuilder()
                .setProduct(product)
                .setSuccess(true)
                .build()

            responseObserver.onNext(response)
            responseObserver.onCompleted()
        } catch (e: Exception) {
            val response = CreateProductResponse.newBuilder()
                .setSuccess(false)
                .setErrorMessage(e.message ?: "Unknown error")
                .build()
            
            responseObserver.onNext(response)
            responseObserver.onCompleted()
        }
    }

    override fun getOrder(request: GetOrderRequest, responseObserver: StreamObserver<GetOrderResponse>) {
        try {
            val orderId = UUID.fromString(request.orderId)
            val orderDto = bookshopService.getOrderById(orderId)

            val response = if (orderDto != null) {
                val order = Order.newBuilder()
                    .setOrderId(orderDto.orderId.toString())
                    .setUserId(orderDto.userId.toString())
                    .setStatusValue(orderDto.status.uppercase().hashCode() % 3) // Map to enum
                    .setTotalAmount(orderDto.totalAmount.toDouble())
                    .build()

                GetOrderResponse.newBuilder()
                    .setOrder(order)
                    .setSuccess(true)
                    .build()
            } else {
                GetOrderResponse.newBuilder()
                    .setSuccess(false)
                    .setErrorMessage("Order not found")
                    .build()
            }

            responseObserver.onNext(response)
            responseObserver.onCompleted()
        } catch (e: Exception) {
            val response = GetOrderResponse.newBuilder()
                .setSuccess(false)
                .setErrorMessage(e.message ?: "Unknown error")
                .build()
            
            responseObserver.onNext(response)
            responseObserver.onCompleted()
        }
    }

    override fun createOrder(request: CreateOrderRequest, responseObserver: StreamObserver<CreateOrderResponse>) {
        try {
            val orderDto = com.bookshop.dto.OrderDto(
                orderId = UUID.randomUUID(),
                userId = UUID.fromString(request.userId),
                status = mapOrderStatusFromGrpc(request.status),
                totalAmount = java.math.BigDecimal.valueOf(request.totalAmount)
            )

            val createdOrderDto = bookshopService.createOrder(orderDto)

            val order = Order.newBuilder()
                .setOrderId(createdOrderDto.orderId.toString())
                .setUserId(createdOrderDto.userId.toString())
                .setStatusValue(createdOrderDto.status.uppercase().hashCode() % 3) // Map to enum
                .setTotalAmount(createdOrderDto.totalAmount.toDouble())
                .build()

            val response = CreateOrderResponse.newBuilder()
                .setOrder(order)
                .setSuccess(true)
                .build()

            responseObserver.onNext(response)
            responseObserver.onCompleted()
        } catch (e: Exception) {
            val response = CreateOrderResponse.newBuilder()
                .setSuccess(false)
                .setErrorMessage(e.message ?: "Unknown error")
                .build()
            
            responseObserver.onNext(response)
            responseObserver.onCompleted()
        }
    }

    override fun getBookFromLibrary(request: GetBookFromLibraryRequest, responseObserver: StreamObserver<GetBookFromLibraryResponse>) {
        try {
            val bookInfo = bookshopService.javaEeHttpService.getBookById(request.bookId)

            val response = if (bookInfo != null) {
                val bookInfoProto = BookInfo.newBuilder()
                    .setId(bookInfo.id)
                    .setTitle(bookInfo.title)
                    .setAuthor(bookInfo.author)
                    .setYear(bookInfo.year)
                    .build()

                GetBookFromLibraryResponse.newBuilder()
                    .setBookInfo(bookInfoProto)
                    .setSuccess(true)
                    .build()
            } else {
                GetBookFromLibraryResponse.newBuilder()
                    .setSuccess(false)
                    .setErrorMessage("Book not found in Java EE service")
                    .build()
            }

            responseObserver.onNext(response)
            responseObserver.onCompleted()
        } catch (e: Exception) {
            val response = GetBookFromLibraryResponse.newBuilder()
                .setSuccess(false)
                .setErrorMessage(e.message ?: "Unknown error")
                .build()
            
            responseObserver.onNext(response)
            responseObserver.onCompleted()
        }
    }

    // Helper methods to map gRPC enums to internal enums
    private fun mapProductTypeFromGrpc(productType: ProductType): String {
        return when (productType) {
            ProductType.BOOK -> "book"
            ProductType.DIGITAL_BOOK -> "digital_book"
            ProductType.PHYSICAL_GOOD -> "physical_good"
            ProductType.DIGITAL_GOOD -> "digital_good"
            else -> "book" // default
        }
    }

    private fun mapOrderStatusFromGrpc(orderStatus: OrderStatus): String {
        return when (orderStatus) {
            OrderStatus.PENDING -> "pending"
            OrderStatus.COMPLETED -> "completed"
            OrderStatus.CANCELLED -> "cancelled"
            else -> "pending" // default
        }
    }
}