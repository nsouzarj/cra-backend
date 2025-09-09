package br.adv.cra.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class DatabaseConnectionService implements CommandLineRunner {
    
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    
    @Override
    public void run(String... args) throws Exception {
        testDatabaseConnection();
    }
    
    public void testDatabaseConnection() {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            
            log.info("=== DATABASE CONNECTION TEST ===");
            log.info("Database Product: {}", metaData.getDatabaseProductName());
            log.info("Database Version: {}", metaData.getDatabaseProductVersion());
            log.info("Driver Name: {}", metaData.getDriverName());
            log.info("Driver Version: {}", metaData.getDriverVersion());
            log.info("URL: {}", metaData.getURL());
            log.info("Username: {}", metaData.getUserName());
            log.info("Schema: {}", connection.getSchema());
            log.info("Catalog: {}", connection.getCatalog());
            
            // Test simple query
            String currentTime = jdbcTemplate.queryForObject("SELECT CURRENT_TIMESTAMP", String.class);
            log.info("Current Database Time: {}", currentTime);
            
            // Test if we can create tables (will be handled by Hibernate)
            log.info("Database connection successful! ✅");
            
        } catch (SQLException e) {
            log.error("❌ Database connection failed!", e);
            log.error("Please check:");
            log.error("1. PostgreSQL server is running on 192.168.1.105:5432");
            log.error("2. Database 'dbcra' exists");
            log.error("3. User 'postgres' has access permissions");
            log.error("4. Password 'nso1810' is correct");
            log.error("5. Network connectivity to 192.168.1.105");
        }
    }
    
    public Map<String, Object> getDatabaseInfo() {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            
            return Map.of(
                "productName", metaData.getDatabaseProductName(),
                "productVersion", metaData.getDatabaseProductVersion(),
                "driverName", metaData.getDriverName(),
                "driverVersion", metaData.getDriverVersion(),
                "url", metaData.getURL(),
                "username", metaData.getUserName(),
                "schema", connection.getSchema() != null ? connection.getSchema() : "N/A",
                "catalog", connection.getCatalog() != null ? connection.getCatalog() : "N/A",
                "connected", true
            );
        } catch (SQLException e) {
            log.error("Failed to get database info", e);
            return Map.of(
                "connected", false,
                "error", e.getMessage()
            );
        }
    }
}