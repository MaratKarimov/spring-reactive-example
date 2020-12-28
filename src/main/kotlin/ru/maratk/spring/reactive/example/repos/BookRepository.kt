package ru.maratk.spring.reactive.example.repos

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import ru.maratk.spring.reactive.example.beans.Book

@Repository
class BookRepository(private val client: DatabaseClient) {

    @FlowPreview
    fun findById(id: Int): Flow<Book> {
        return client.sql("SELECT * FROM reactive.book WHERE id = $1")
            .bind(0, id)
            .map { row -> Book(row.get("id", Int::class.java)
                , row.get("version", Int::class.java)
                , row.get("author", String::class.java)
                , row.get("title", String::class.java)) }
            .all()
            .asFlow()
    }

    @FlowPreview
    fun findAll(): Flow<Book> {
        return client.sql("SELECT * FROM reactive.book")
            .map { row -> Book(row.get("id", Int::class.java)
                , row.get("version", Int::class.java)
                , row.get("author", String::class.java)
                , row.get("title", String::class.java)) }
            .all()
            .asFlow()
    }

    suspend fun add(b: Book) =
        client.sql("INSERT INTO reactive.book (author, title) VALUES($1, $2)")
            .bind(0, b.author)
            .bind(1, b.title)
            //.map { row -> row.get("id", Int::class.java)}
            //.one()
            //.asFlow()
            .then().awaitFirstOrNull()
}