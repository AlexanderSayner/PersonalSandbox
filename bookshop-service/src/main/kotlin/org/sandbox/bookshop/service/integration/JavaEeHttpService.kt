package org.sandbox.bookshop.service.integration

import com.bookshop.grpc.BookInfo
import org.springframework.stereotype.Service

@Service
class JavaEeHttpService {
    fun getBookById(id: Int): BookInfo {
        //TODO
        return BookInfo.newBuilder().build()
    }
}
