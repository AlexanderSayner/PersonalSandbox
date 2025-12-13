package com.bookshop

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BookshopApplication

fun main(args: Array<String>) {
    runApplication<BookshopApplication>(*args)
}