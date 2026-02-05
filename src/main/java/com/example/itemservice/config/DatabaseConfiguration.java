package com.example.itemservice.config;

import com.example.itemservice.repository.DatabaseItemRepository;
import com.example.itemservice.repository.InMemoryItemRepository;
import com.example.itemservice.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Configuration class for database setup and failover strategy.
 * 
 * <p>This configuration manages the connection to PostgreSQL database and provides
 * automatic failover to in-memory storage when the database is unavailable.
 * It uses Spring's conditional annotations to enable/disable database features
 * based on configuration and availability.</p>
 * 
 * <p>Note: Database migrations are now handled by Flyway, not DataSourceInitializer.</p>
 */
@Configuration
@ConditionalOnProperty(name = "database.enabled", havingValue = "true", matchIfMissing = true)
public class DatabaseConfiguration {
    
    /**
     * Creates a DataSource bean using database configuration from Spring Cloud Config.
     * 
     * @param databaseProperties the database configuration properties
     * @return configured DataSource for PostgreSQL
     */
    @Bean
    @ConditionalOnProperty(name = "database.enabled", havingValue = "true")
    public DataSource dataSource(DatabaseProperties databaseProperties) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(databaseProperties.getDriverClassName());
        dataSource.setUrl(String.format("jdbc:postgresql://%s:%d/%s", 
                databaseProperties.getHost(), 
                databaseProperties.getPort(), 
                databaseProperties.getName()));
        dataSource.setUsername(databaseProperties.getUsername());
        dataSource.setPassword(databaseProperties.getPassword());
        return dataSource;
    }
    
    /**
     * Creates a JdbcTemplate for database health checking.
     * 
     * @param dataSource the configured DataSource
     * @return JdbcTemplate instance
     */
    @Bean
    @ConditionalOnBean(DataSource.class)
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
    
    /**
     * Creates the database repository when DataSource is available.
     * 
     * @return DatabaseItemRepository instance
     */
    @Bean
    @Primary
    @ConditionalOnBean(DataSource.class)
    public ItemRepository databaseItemRepository(DatabaseItemRepository databaseItemRepository) {
        return databaseItemRepository;
    }
    
    /**
     * Creates the in-memory repository as fallback when database is unavailable.
     * 
     * @return InMemoryItemRepository instance
     */
    @Bean
    @ConditionalOnMissingBean(name = "databaseItemRepository")
    public ItemRepository inMemoryItemRepository() {
        return new InMemoryItemRepository();
    }
    
    /**
     * Creates database health checker for startup validation.
     * 
     * @param jdbcTemplate the JdbcTemplate for health checks
     * @param databaseProperties the database configuration
     * @return DatabaseHealthChecker instance
     */
    @Bean
    @ConditionalOnBean(JdbcTemplate.class)
    public DatabaseHealthChecker databaseHealthChecker(JdbcTemplate jdbcTemplate, 
                                                      DatabaseProperties databaseProperties) {
        return new DatabaseHealthChecker(jdbcTemplate, databaseProperties);
    }
    
    /**
     * Creates database properties from Spring Cloud Config.
     * 
     * @param environment the Spring environment
     * @return DatabaseProperties instance
     */
    @Bean
    public DatabaseProperties databaseProperties(org.springframework.core.env.Environment environment) {
        Map<String, Object> databaseConfig = environment.getProperty("database", Map.class);
        return new DatabaseProperties(databaseConfig != null ? databaseConfig : Map.of());
    }
}

/**
 * Configuration properties for database connection.
 * 
 * <p>Uses @Value annotations to inject database configuration from Spring Cloud Config.
 * This approach provides direct access to configuration values without requiring
 * a separate configuration class file.</p>
 */
@Configuration
class DatabaseProperties {
    
    private final String host;
    private final int port;
    private final String name;
    private final String username;
    private final String password;
    private final String driverClassName;
    
    public DatabaseProperties(Map<String, Object> databaseConfig) {
        this.host = (String) databaseConfig.getOrDefault("host", "localhost");
        this.port = ((Number) databaseConfig.getOrDefault("port", 5432)).intValue();
        this.name = (String) databaseConfig.getOrDefault("name", "testbed");
        this.username = (String) databaseConfig.getOrDefault("username", "postgres");
        this.password = (String) databaseConfig.getOrDefault("password", "falcon69");
        this.driverClassName = (String) databaseConfig.getOrDefault("driver-class-name", "org.postgresql.Driver");
    }
    
    public String getHost() { return host; }
    public int getPort() { return port; }
    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getDriverClassName() { return driverClassName; }
}

/**
 * Health checker for database connectivity.
 * 
 * <p>Performs database connectivity checks during application startup and provides
 * logging for connection status. Used to determine if the application should use
 * database storage or fall back to in-memory storage.</p>
 */
class DatabaseHealthChecker {
    
    private final JdbcTemplate jdbcTemplate;
    private final DatabaseProperties databaseProperties;
    
    public DatabaseHealthChecker(JdbcTemplate jdbcTemplate, DatabaseProperties databaseProperties) {
        this.jdbcTemplate = jdbcTemplate;
        this.databaseProperties = databaseProperties;
        checkDatabaseConnection();
    }
    
    /**
     * Checks database connectivity and logs the result.
     * If connection fails, logs warning and continues with in-memory storage.
     */
    private void checkDatabaseConnection() {
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            System.out.println("Database connection successful: " + 
                String.format("postgresql://%s:%d/%s", 
                    databaseProperties.getHost(), 
                    databaseProperties.getPort(), 
                    databaseProperties.getName()));
        } catch (Exception e) {
            System.err.println("Database connection failed: " + e.getMessage());
            System.err.println("Falling back to in-memory storage");
        }
    }
}