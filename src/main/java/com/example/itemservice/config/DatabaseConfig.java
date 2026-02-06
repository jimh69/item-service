package com.example.itemservice.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
@Slf4j
public class DatabaseConfig {

    @Bean
    @Primary
    public DataSource dataSource(
            @Value("${database.host:localhost}") String host,
            @Value("${database.port:5432}") String port,
            @Value("${database.name:testbed}") String name,
            @Value("${database.username:postgres}") String username,
            @Value("${database.password:}") String password,
            @Value("${database.driver-class-name:org.postgresql.Driver}") String driverClassName,
            @Value("${database.pool-size:5}") int poolSize,
            @Value("${database.timeout:10000}") int timeout) {
        
        log.info("Creating DataSource for PostgreSQL at {}:{}", host, port);
        
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:postgresql://" + host + ":" + port + "/" + name);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setDriverClassName(driverClassName);
        ds.setMaximumPoolSize(poolSize);
        ds.setConnectionTimeout(timeout);
        
        return ds;
    }

    @PostConstruct
    public void logStartup() {
        log.info("DatabaseConfig initialized");
    }
}