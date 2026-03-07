package com.example.itemservice.config;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan("com.example.itemservice.model")
@EnableJpaRepositories("com.example.itemservice.repository")
public class JpaConfig {
}