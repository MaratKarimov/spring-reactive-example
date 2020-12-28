package ru.maratk.spring.reactive.example.routers

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.BodyExtractors
import org.springframework.web.reactive.function.server.*
import ru.maratk.spring.reactive.example.beans.Book
import ru.maratk.spring.reactive.example.repos.BookRepository

@Configuration
class BookRouters {
    @FlowPreview
    @Bean
    fun route(bookRepository: BookRepository) = coRouter {
        GET("/") { _ ->
            ServerResponse.ok().bodyAndAwait(bookRepository.findAll())
        }
        GET("/{id}") {
                req -> ServerResponse.ok().bodyAndAwait(bookRepository.findById(req.pathVariable("id").toInt()))
        }
        POST("/") {
                req ->  bookRepository.add(req.awaitBody(Book::class)); ServerResponse.ok().build().awaitSingle()
        }
    }
}