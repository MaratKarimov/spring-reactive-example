package ru.maratk.spring.reactive.example.handlers

import kotlinx.coroutines.FlowPreview
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyAndAwait
import ru.maratk.spring.reactive.example.beans.Book
import ru.maratk.spring.reactive.example.repos.BookRepository

class BookHandlers(private val bookRepository: BookRepository) {

    @FlowPreview
    suspend fun findById(req: ServerRequest): ServerResponse {
        return ServerResponse.ok()
            .bodyAndAwait(bookRepository.findById(req.pathVariable("id").toInt()))
    }

    @FlowPreview
    suspend fun findAll(): ServerResponse {
        return ServerResponse.ok()
            .bodyAndAwait(bookRepository.findAll())
    }

    @FlowPreview
    suspend fun add(req: ServerRequest): ServerResponse {
        return ServerResponse.ok()
            .bodyAndAwait(bookRepository.add(req.awaitBody(Book::class)))
    }
}