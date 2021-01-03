package ru.maratk.spring.reactive.example

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.junit4.SpringRunner
import org.testcontainers.containers.PostgreSQLContainer
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import ru.maratk.spring.reactive.example.beans.Book


@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AppTest {

    internal class KPostgreSQLContainer(image: String) : PostgreSQLContainer<KPostgreSQLContainer>(image)

    companion object {
        private val postgreSQLContainer = KPostgreSQLContainer("postgres:12").apply {
            withDatabaseName("reactive")
            withUsername("reactive")
            withPassword("reactive")
            start()
        }

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            // r2dbc
            registry.add("spring.r2dbc.url") { postgreSQLContainer.jdbcUrl.replace("jdbc", "r2dbc") }
            registry.add("spring.r2dbc.username", postgreSQLContainer::getUsername)
            registry.add("spring.r2dbc.password", postgreSQLContainer::getPassword)
            // flyway
            registry.add("spring.flyway.url", postgreSQLContainer::getJdbcUrl)
            registry.add("spring.flyway.username", postgreSQLContainer::getUsername)
            registry.add("spring.flyway.password", postgreSQLContainer::getPassword)
        }
    }

    @Autowired
    private lateinit var webClient: WebTestClient

    @Test
    fun test(){
        val book = Book(id = 1, version = 1, author = "Leo Tolstoy", title = "Sunday")
        println(webClient
            .post()
            .uri("/")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(book))
            .exchange()
            .returnResult(Int::class.java))
    }
}