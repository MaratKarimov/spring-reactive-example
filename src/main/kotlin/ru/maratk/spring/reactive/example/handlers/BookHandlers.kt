package ru.maratk.spring.reactive.example.handlers

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import org.springframework.web.reactive.function.server.*
import ru.maratk.spring.reactive.example.beans.Book
import ru.maratk.spring.reactive.example.repos.BookRepository

class BookHandlers(private val bookRepository: BookRepository) {

    @FlowPreview
    suspend fun findById(req: ServerRequest): ServerResponse {
        val bookTry = bookRepository.findById(req.pathVariable("id").toInt()).first()
        return try { ServerResponse.ok().bodyValueAndAwait(bookTry.get()) }
        catch (e: Exception) { ServerResponse.badRequest().bodyValueAndAwait(e.message!!) }
    }

    @FlowPreview
    suspend fun findAll(): ServerResponse {
        val bookTries = bookRepository.findAll().toList()
        return try { ServerResponse.ok().bodyValueAndAwait(bookTries.map { it.get() }) }
        catch (e: Exception) { ServerResponse.badRequest().bodyValueAndAwait(e.message!!) }
    }

    @FlowPreview
    suspend fun add(req: ServerRequest): ServerResponse {
        val idTry = bookRepository.add(req.awaitBody(Book::class)).first()
        return try { ServerResponse.ok().bodyValueAndAwait(idTry.get()) }
        catch (e: Exception) { ServerResponse.badRequest().bodyValueAndAwait(e.message!!) }
    }
}