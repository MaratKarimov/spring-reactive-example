package ru.maratk.spring.reactive.example.dao

import io.r2dbc.spi.Row
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import org.jooq.DSLContext
import org.jooq.conf.ParamType
import org.jooq.impl.DSL
import org.springframework.r2dbc.core.DatabaseClient
import ru.maratk.spring.reactive.example.beans.Book

class BookDao(private val client: DatabaseClient, private val dslContext: DSLContext) {

    @FlowPreview
    fun findById(id: Int): Flow<Book> {
        val sql = dslContext
            .select(DSL.field("*"))
            .from(DSL.table("reactive.book"))
            .where(DSL.field("id").eq(id))
            .getSQL(ParamType.INLINED)
        return client.sql(sql)
            .map { row -> read(row) }
            .first()
            .asFlow()
    }

    @FlowPreview
    fun findAll(): Flow<Book> {
        val sql = dslContext
            .select(DSL.field("*"))
            .from(DSL.table("reactive.book"))
            .sql
        return client.sql(sql)
            .map { row -> read(row) }
            .all()
            .asFlow()
    }

    @FlowPreview
    fun add(b: Book): Flow<Int> {
        val sql = dslContext
            .insertInto(DSL.table("reactive.book"))
            .set(DSL.field("author"), b.author)
            .set(DSL.field("title"), b.title)
            .returning(DSL.field("id"))
            .getSQL(ParamType.INLINED)
        return client.sql(sql)
            .map { row -> row.get("id") as Int }
            .one()
            .asFlow()
    }

    private fun read(row: Row) : Book {
        return Book(row.get("id") as Int
            , row.get("version") as Int
            , row.get("author") as String
            , row.get("title") as String)
    }
}