package ru.maratk.spring.reactive.example.repos

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import ru.maratk.spring.reactive.example.beans.Book
import ru.maratk.spring.reactive.example.dao.BookDao

class BookRepository(private val bookDao: BookDao) {
    @FlowPreview
    fun findById(id: Int): Flow<Book> { return bookDao.findById(id) }

    @FlowPreview
    fun findAll(): Flow<Book> { return bookDao.findAll() }

    @FlowPreview
    fun add(b: Book): Flow<Int> { return bookDao.add(b); }
}