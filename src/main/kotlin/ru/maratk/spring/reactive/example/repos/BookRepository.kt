package ru.maratk.spring.reactive.example.repos

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import ru.maratk.spring.reactive.example.beans.Book
import ru.maratk.spring.reactive.example.beans.Try
import ru.maratk.spring.reactive.example.dao.BookDao

class BookRepository(private val bookDao: BookDao) {
    @FlowPreview
    fun findById(id: Int): Flow<Try<Book>> { return bookDao.findById(id) }

    @FlowPreview
    fun findAll(): Flow<Try<Book>> {
        return bookDao.findAll()
    }

    @FlowPreview
    fun add(b: Book): Flow<Try<Int>> {
        return bookDao.add(b)
    }
}