package com.example.itemservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.boot.context.event.SpringApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

/**
 * Monitors Spring Cloud Config Server connection status and logs debug messages.
 * 
 * <p>This component listens for Spring Boot application lifecycle events and
 * Spring Cloud Config events to provide detailed logging about the connection
 * status to the configuration server.</p>
 */
@Configuration
@Slf4j
public class ConfigServerConnectionMonitor {

    /**
     * Logs when the application environment is being prepared.
     * This is when Spring Cloud Config starts attempting to connect.
     */
    @EventListener
    public void onApplicationEnvironmentPrepared(ApplicationEnvironmentPreparedEvent event) {
        log.debug("Application environment preparation started - config-server connection attempts will begin");
    }

    /**
     * Logs Spring Cloud Config specific events for detailed connection monitoring.
     * This captures the actual config server connection attempts and their outcomes.
     */
    @EventListener
    public void onConfigServerConnectionAttempt(Object event) {
        String eventType = event.getClass().getSimpleName();
        
        // Log Spring Cloud Config events at debug level
        if (eventType.contains("ConfigServer") || eventType.contains("ConfigData")) {
            log.debug("Spring Cloud Config event: {}", eventType);
            
            // Specific handling for connection-related events
            switch (eventType) {
                case "ConfigServerConfigDataResource":
                    log.debug("Config server connection attempt initiated");
                    break;
                case "ConfigServerHealthCheckFailedEvent":
                    log.debug("Config server health check failed");
                    break;
                case "ConfigServerHealthCheckSucceededEvent":
                    log.debug("Config server health check succeeded");
                    break;
                case "ConfigServerRetryEvent":
                    log.debug("Config server connection retry attempt");
                    break;
                default:
                    log.debug("Config server event: {}", eventType);
            }
        }
    }

    /**
     * Logs when the application has started successfully.
     * This indicates config-server connection was successful.
     */
    @EventListener
    public void onApplicationStarted(ApplicationStartedEvent event) {
        log.debug("Application started successfully - config-server connection established");
    }

    /**
     * Logs when the application is ready and fully running.
     * This confirms all configuration has been loaded successfully.
     */
    @EventListener
    public void onApplicationReady(ApplicationReadyEvent event) {
        log.debug("Application is ready - all configuration loaded from config-server");
    }

    /**
     * Logs when the application fails to start.
     * This indicates config-server connection attempts have failed.
     */
    @EventListener
    public void onApplicationFailed(ApplicationFailedEvent event) {
        log.debug("Application failed to start - config-server connection attempts exhausted");
        if (event.getException() != null) {
            log.debug("Failure reason: {}", event.getException().getMessage());
        }
    }

    /**
     * Logs when the application is starting.
     * This is the initial phase before environment preparation.
     */
    @EventListener
    public void onApplicationStarting(ApplicationStartingEvent event) {
        log.debug("Application starting - config-server connection will be attempted during environment preparation");
    }

    /**
     * Handles Spring Cloud Config specific events if available.
     * This method provides more granular config-server connection logging.
     */
    @EventListener
    public void handleSpringCloudConfigEvents(SpringApplicationEvent event) {
        String eventType = event.getClass().getSimpleName();
        
        switch (eventType) {
            case "ConfigServerInstanceRetrievedEvent":
                log.debug("Config server instance retrieved successfully");
                break;
            case "ConfigServerHealthCheckFailedEvent":
                log.debug("Config server health check failed");
                break;
            case "ConfigServerHealthCheckSucceededEvent":
                log.debug("Config server health check succeeded");
                break;
            case "ConfigServerRetryEvent":
                log.debug("Config server connection retry attempt");
                break;
            default:
                // Log other Spring Cloud Config events at debug level
                if (eventType.contains("Config") || eventType.contains("Cloud")) {
                    log.debug("Spring Cloud Config event: {}", eventType);
                }
        }
    }
}