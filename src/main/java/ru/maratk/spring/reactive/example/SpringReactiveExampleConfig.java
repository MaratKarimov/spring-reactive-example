package ru.maratk.spring.reactive.example;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.kotlin.KotlinModule;
import io.r2dbc.spi.ConnectionFactory;
import org.flywaydb.core.Flyway;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.transaction.ReactiveTransactionManager;
import ru.maratk.spring.reactive.example.dao.BookDao;
import ru.maratk.spring.reactive.example.handlers.BookHandlers;
import ru.maratk.spring.reactive.example.repos.BookRepository;

@Configuration
@PropertySource(value={"classpath:application.properties"})
public class SpringReactiveExampleConfig {

    @Autowired
    Environment env;

    @Autowired
    DatabaseClient databaseClient;

    @Bean
    Module javaTimeModule() { return new JavaTimeModule(); }

    @Bean
    Module kotlinModule() { return new KotlinModule(); }

    @Bean(initMethod = "migrate")
    Flyway flyway() {
        return new Flyway(
                Flyway
                        .configure()
                        .baselineOnMigrate(true)
                        .dataSource(env.getRequiredProperty("spring.flyway.url")
                                , env.getRequiredProperty("spring.flyway.username")
                                , env.getRequiredProperty("spring.flyway.password"))
        );
    }

    @Bean
    ReactiveTransactionManager transactionManager(final ConnectionFactory connectionFactory) {
        return new R2dbcTransactionManager(connectionFactory);
    }

    @Bean
    DSLContext dslContext(){ return DSL.using(SQLDialect.POSTGRES); }

    @Bean
    BookDao bookDao(){ return new BookDao(databaseClient, dslContext()); }

    @Bean
    BookRepository bookRepository(){ return new BookRepository(bookDao()); }

    @Bean
    BookHandlers bookHandlers() { return new BookHandlers(bookRepository()); }
}