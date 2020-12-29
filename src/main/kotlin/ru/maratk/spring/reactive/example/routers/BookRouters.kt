package ru.maratk.spring.reactive.example.routers

import kotlinx.coroutines.FlowPreview
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.*
import ru.maratk.spring.reactive.example.handlers.BookHandlers
import ru.maratk.spring.reactive.example.repos.BookRepository

@Configuration
class BookRouters(private val bookHandlers: BookHandlers) {
    @FlowPreview
    @Bean
    fun route(bookRepository: BookRepository) = coRouter {
        GET("/") { bookHandlers.findAll() }
        POST("/") { req -> bookHandlers.add(req) }
        GET("/{id}") { req -> bookHandlers.findById(req) }
    }
}