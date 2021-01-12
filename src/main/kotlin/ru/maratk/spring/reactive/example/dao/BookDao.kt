package ru.maratk.spring.reactive.example.dao

import io.r2dbc.spi.Row
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import org.jooq.DSLContext
import org.jooq.conf.ParamType
import org.jooq.impl.DSL
import org.springframework.r2dbc.core.DatabaseClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ru.maratk.spring.reactive.example.beans.Book
import ru.maratk.spring.reactive.example.beans.Failure
import ru.maratk.spring.reactive.example.beans.Success
import ru.maratk.spring.reactive.example.beans.Try
import java.lang.ClassCastException
import kotlin.jvm.Throws

class BookDao(private val client: DatabaseClient, private val dslContext: DSLContext) {

    @FlowPreview
    fun findById(id: Int): Flow<Try<Book>> {
        val sql = dslContext
            .select(DSL.field("*"))
            .from(DSL.table("reactive.book"))
            .where(DSL.field("id").eq(id))
            .getSQL(ParamType.INLINED)
        return client.sql(sql)
            .map { row -> readTry(row) }
            .first()
            .onErrorResume { e -> Mono.just(Failure(e)) }
            .asFlow()
    }

    @FlowPreview
    fun findAll(): Flow<Try<Book>> {
        val sql = dslContext
            .select(DSL.field("*"))
            .from(DSL.table("reactive.book"))
            .sql
        return client.sql(sql)
            .map { row -> readTry(row) }
            .all()
            .onErrorResume { e -> Flux.just(Failure(e)) }
            .asFlow()
    }

    @FlowPreview
    fun add(b: Book): Flow<Try<Int>> {
        val sql = dslContext
            .insertInto(DSL.table("reactive.book"))
            .set(DSL.field("author"), b.author)
            .set(DSL.field("title"), b.title)
            .returning(DSL.field("id"))
            .getSQL(ParamType.INLINED)
        return client.sql(sql)
            .map { row -> Success(row.get("id") as Int) as Try<Int> }
            .one()
            .onErrorResume { e -> Mono.just(Failure(e)) }
            .asFlow()
    }

    private fun readTry(row: Row) : Try<Book> {
        return try { Success(read(row)) }
        catch(e: Exception) { Failure(e) }
    }

    @Throws(ClassCastException::class)
    private fun read(row: Row) : Book {
        return Book(row.get("id") as Int
            , row.get("version") as Int
            , row.get("author") as String
            , row.get("title") as String)
    }
}