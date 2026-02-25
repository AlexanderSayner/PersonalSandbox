package org.sandbox.pricing.client

import org.springframework.stereotype.Service
import java.math.BigDecimal

//TODO: make sure to support for Spring Boot 4 gRPC style
@Service
class BookshopGrpcClient {
    fun getProductPrice(productId: String): String {
        TODO("Not yet implemented")
    }

//    @GrpcClient("bookshop-service")
//    private lateinit var bookshopServiceStub: BookshopServiceGrpcKt.BookshopServiceCoroutineStub
//
//    suspend fun getProductPrice(productId: String): Double {
//        val request = GetProductPriceRequest.newBuilder()
//            .setProductId(productId)
//            .build()
//
//        val response = try {
//            bookshopServiceStub.getProductPrice(request)
//        } catch (e: Exception) {
//            throw RuntimeException("Failed to get product price from bookshop service", e)
//        }
//
//        return response.price
//    }
}