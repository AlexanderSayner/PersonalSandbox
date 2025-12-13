package com.bookshop.service

import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.beans.factory.annotation.Value
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

// Interface for Java EE service
interface JavaEeService {
    fun getBookById(id: Int): BookInfo?
}

// Data class for book info from Java EE service
data class BookInfo(
    val id: Int,
    val title: String,
    val author: String,
    val year: Int
)

/**
 * Service to handle HTTP/GraphQL communication with Java EE service
 */
@Service
class JavaEeHttpService : JavaEeService {
    
    @Value("\${java.ee.service.url:http://java-ee-service:8080}")
    private lateinit var javaEeServiceUrl: String

    private val webClient = WebClient.builder().build()
    private val objectMapper = ObjectMapper()

    override fun getBookById(id: Int): BookInfo? {
        // GraphQL query to Java EE service
        val graphQlQuery = """
            query {
                book(id: $id) {
                    id
                    title
                    author
                    year
                }
            }
        """.trimIndent()

        val requestBody = mapOf("query" to graphQlQuery)

        return try {
            val response = webClient.post()
                .uri("$javaEeServiceUrl/graphql")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String::class.java)
                .block()

            // Parse the JSON response
            val jsonNode = objectMapper.readTree(response)
            val bookData = jsonNode.get("data").get("book")

            if (bookData != null && !bookData.isNull) {
                BookInfo(
                    id = bookData.get("id").asInt(),
                    title = bookData.get("title").asText(),
                    author = bookData.get("author").asText(),
                    year = bookData.get("year").asInt()
                )
            } else {
                null
            }
        } catch (e: Exception) {
            // Log error in real implementation
            null
        }
    }
}