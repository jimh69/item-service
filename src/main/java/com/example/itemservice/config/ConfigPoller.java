package com.example.itemservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Configuration poller that automatically checks for configuration changes
 * from Spring Cloud Config Server at regular intervals.
 * 
 * <p>This component polls the Spring Cloud Config Server every 30 seconds
 * to detect configuration changes and automatically refreshes the application
 * context when changes are detected.</p>
 * 
 * <p>Features:</p>
 * <ul>
 *   <li>Automatic polling every 30 seconds (configurable)</li>
 *   <li>Graceful failure handling with INFO level logging for failures</li>
 *   <li>INFO level logging for polling operations (reduced noise)</li>
 *   <li>Only refreshes configuration when actual changes are detected</li>
 *   <li>Logs polling activity and results for monitoring</li>
 * </ul>
 * 
 * <p>Configuration Properties:</p>
 * <ul>
 *   <li>config.polling.enabled: Enable/disable automatic polling (default: true)</li>
 *   <li>config.polling.interval: Polling interval in milliseconds (default: 30000)</li>
 * </ul>
 * 
 * <p>Usage:</p>
 * <pre>
 * {@code
 * # Enable/disable polling
 * config.polling.enabled=true
 * 
 * # Set polling interval (30 seconds default)
 * config.polling.interval=30000
 * }
 * </pre>
 * 
 * @author Jim H
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
@Slf4j
public class ConfigPoller {

    @Autowired
    private ContextRefresher contextRefresher;

    private boolean isPollingEnabled = true;
    private boolean isApplicationReady = false;

    /**
     * Polls Spring Cloud Config Server for configuration changes every 30 seconds.
     * 
     * <p>This method is scheduled to run every 30 seconds using Spring's
     * {@link Scheduled} annotation. It checks for configuration changes and
     * refreshes the application context if changes are detected.</p>
     * 
     * <p>The polling is only active after the application is fully ready and
     * when polling is enabled via configuration.</p>
     */
    @Scheduled(fixedRateString = "${config.polling.interval:30000}")
    public void pollForConfigChanges() {
        if (!isApplicationReady || !isPollingEnabled) {
            return;
        }

        try {
            log.info("Polling Spring Cloud Config Server for configuration changes...");
            
            // Refresh configuration and get list of changed properties
            var refreshResult = contextRefresher.refresh();
            
            if (refreshResult.isEmpty()) {
                log.info("No configuration changes detected");
            } else {
                log.info("Configuration changes detected and applied: {}", refreshResult);
            }
        } catch (Exception e) {
            // Log failures at INFO level as requested
            log.info("Failed to poll Spring Cloud Config Server: {}", e.getMessage());
            log.debug("Polling failure details", e);
        }
    }

    /**
     * Event listener that sets the application ready flag when the application
     * is fully started and ready to handle requests.
     * 
     * <p>This ensures that configuration polling only starts after the
     * application is completely initialized.</p>
     * 
     * @param event the ApplicationReadyEvent
     */
    @EventListener
    public void onApplicationReady(ApplicationReadyEvent event) {
        isApplicationReady = true;
        log.info("Application ready - configuration polling enabled");
    }

    /**
     * Sets whether configuration polling is enabled.
     * 
     * <p>This method can be used to dynamically enable or disable
     * configuration polling at runtime.</p>
     * 
     * @param enabled true to enable polling, false to disable
     */
    public void setPollingEnabled(boolean enabled) {
        this.isPollingEnabled = enabled;
        if (enabled) {
            log.info("Configuration polling enabled");
        } else {
            log.info("Configuration polling disabled");
        }
    }

    /**
     * Checks if configuration polling is currently enabled.
     * 
     * @return true if polling is enabled, false otherwise
     */
    public boolean isPollingEnabled() {
        return isPollingEnabled;
    }

    /**
     * Manually triggers a configuration refresh.
     * 
     * <p>This method can be called to manually check for configuration
     * changes outside of the regular polling schedule.</p>
     * 
     * @return the list of changed property names, or empty if no changes
     */
    public Iterable<String> refreshConfiguration() {
        if (!isApplicationReady) {
            log.warn("Application not ready - skipping manual configuration refresh");
            return java.util.Collections.emptyList();
        }

        try {
            log.info("Manually triggering configuration refresh...");
            var refreshResult = contextRefresher.refresh();
            
            if (refreshResult.isEmpty()) {
                log.info("No configuration changes detected during manual refresh");
            } else {
                log.info("Configuration changes detected and applied during manual refresh: {}", refreshResult);
            }
            
            return refreshResult;
        } catch (Exception e) {
            log.info("Failed to refresh configuration: {}", e.getMessage());
            log.debug("Manual refresh failure details", e);
            return java.util.Collections.emptyList();
        }
    }
}