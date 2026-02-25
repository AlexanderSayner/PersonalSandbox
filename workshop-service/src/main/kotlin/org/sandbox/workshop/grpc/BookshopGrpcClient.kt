package org.sandbox.workshop.grpc

import org.sandbox.bookshop.grpc.ProductServiceGrpc
import org.springframework.stereotype.Service

@Service
class BookshopGrpcClient {

    private lateinit var productServiceStub: ProductServiceGrpc.ProductServiceBlockingStub

    fun validateProduct(productId: String): Boolean {
        TODO()
//        val request = BookshopProto.ValidateProductRequest.newBuilder()
//            .setProductId(productId)
//            .build()
//
//        val response = productServiceStub.validateProduct(request)
//        return response.valid
    }
//TODO: Spring Boot 4 gRPC style
//    fun getProduct(productId: String): BookshopProto.Product? {
//        val request = BookshopProto.GetProductRequest.newBuilder()
//            .setProductId(productId)
//            .build()
//
//        val response = productServiceStub.getProduct(request)
//        return if (response.found) response.product else null
//    }
}