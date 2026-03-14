package com.example.itemservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories("com.example.itemservice.repository")
public class JpaConfig {
}