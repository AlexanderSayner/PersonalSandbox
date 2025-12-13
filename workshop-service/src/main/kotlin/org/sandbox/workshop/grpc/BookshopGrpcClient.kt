package org.sandbox.workshop.grpc

import io.grpc.ManagedChannel
import net.devh.boot.grpc.client.inject.GrpcClient
import org.sandbox.bookshop.grpc.BookshopProto
import org.sandbox.bookshop.grpc.ProductServiceGrpc
import org.springframework.stereotype.Service
import java.util.*

@Service
class BookshopGrpcClient {

    @GrpcClient("bookshop-service")
    private lateinit var productServiceStub: ProductServiceGrpc.ProductServiceBlockingStub

    fun validateProduct(productId: String): Boolean {
        val request = BookshopProto.ValidateProductRequest.newBuilder()
            .setProductId(productId)
            .build()

        val response = productServiceStub.validateProduct(request)
        return response.valid
    }

    fun getProduct(productId: String): BookshopProto.Product? {
        val request = BookshopProto.GetProductRequest.newBuilder()
            .setProductId(productId)
            .build()

        val response = productServiceStub.getProduct(request)
        return if (response.found) response.product else null
    }
}