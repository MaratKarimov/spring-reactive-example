package ru.maratk.spring.reactive.example;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.kotlin.KotlinModule;
import io.r2dbc.spi.ConnectionFactory;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;

@Configuration
@PropertySource(value={"classpath:application.properties"})
@ComponentScan({"ru.maratk.spring.reactive.example.routers"})
public class SpringReactiveExampleConfig {

    @Autowired
    Environment env;

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
}