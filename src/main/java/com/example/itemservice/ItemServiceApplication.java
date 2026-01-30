package com.example.itemservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Item Service.
 * 
 * This Spring Boot application provides a RESTful API for managing items
 * with in-memory storage that can be easily replaced with a database.
 */
@SpringBootApplication
public class ItemServiceApplication {

    /**
     * Main entry point for the Spring Boot application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(ItemServiceApplication.class, args);
    }
}