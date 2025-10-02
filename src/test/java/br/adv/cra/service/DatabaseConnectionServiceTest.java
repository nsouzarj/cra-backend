package br.adv.cra.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class DatabaseConnectionServiceTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private Connection connection;

    @Mock
    private DatabaseMetaData databaseMetaData;

    private DatabaseConnectionService databaseConnectionService;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        databaseConnectionService = new DatabaseConnectionService(dataSource, jdbcTemplate);

        // Setup database connection mocks
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.getMetaData()).thenReturn(databaseMetaData);
        when(databaseMetaData.getDatabaseProductName()).thenReturn("PostgreSQL");
        when(databaseMetaData.getDatabaseProductVersion()).thenReturn("13.3");
        when(databaseMetaData.getDriverName()).thenReturn("PostgreSQL JDBC Driver");
        when(databaseMetaData.getDriverVersion()).thenReturn("42.2.23");
        when(databaseMetaData.getURL()).thenReturn("jdbc:postgresql://192.168.1.105:5432/dbcra");
        when(databaseMetaData.getUserName()).thenReturn("postgres");
        when(connection.getSchema()).thenReturn("public");
        when(connection.getCatalog()).thenReturn("dbcra");
        when(jdbcTemplate.queryForObject(anyString(), eq(String.class))).thenReturn("2023-01-01 12:00:00");
    }

    @Test
    void testGetDatabaseInfo_SuccessfulConnection_ShouldReturnDatabaseInfo() throws SQLException {
        // When
        Map<String, Object> result = databaseConnectionService.getDatabaseInfo();

        // Then
        assertNotNull(result);
        assertTrue((Boolean) result.get("connected"));
        assertEquals("PostgreSQL", result.get("productName"));
        assertEquals("13.3", result.get("productVersion"));
        assertEquals("PostgreSQL JDBC Driver", result.get("driverName"));
        assertEquals("42.2.23", result.get("driverVersion"));
        assertEquals("jdbc:postgresql://192.168.1.105:5432/dbcra", result.get("url"));
        assertEquals("postgres", result.get("username"));
        assertEquals("public", result.get("schema"));
        assertEquals("dbcra", result.get("catalog"));

        // Verify interactions
        verify(dataSource, times(1)).getConnection();
        verify(connection, times(1)).getMetaData();
        verify(connection, times(1)).close();
    }

    @Test
    void testGetDatabaseInfo_ConnectionFailure_ShouldReturnErrorInfo() throws SQLException {
        // Given
        when(dataSource.getConnection()).thenThrow(new SQLException("Connection failed"));

        // When
        Map<String, Object> result = databaseConnectionService.getDatabaseInfo();

        // Then
        assertNotNull(result);
        assertFalse((Boolean) result.get("connected"));
        assertNotNull(result.get("error"));
        assertTrue(((String) result.get("error")).contains("Connection failed"));

        // Verify interactions
        verify(dataSource, times(1)).getConnection();
    }

    @Test
    void testGetDatabaseInfo_NullSchemaAndCatalog_ShouldHandleGracefully() throws SQLException {
        // Given
        when(connection.getSchema()).thenReturn(null);
        when(connection.getCatalog()).thenReturn(null);

        // When
        Map<String, Object> result = databaseConnectionService.getDatabaseInfo();

        // Then
        assertNotNull(result);
        assertTrue((Boolean) result.get("connected"));
        assertEquals("N/A", result.get("schema"));
        assertEquals("N/A", result.get("catalog"));

        // Verify interactions
        verify(dataSource, times(1)).getConnection();
        verify(connection, times(1)).getMetaData();
        verify(connection, times(1)).close();
    }

    @Test
    void testRun_ShouldCallTestDatabaseConnection() throws Exception {
        // When
        databaseConnectionService.run();

        // Then
        // Verify that the testDatabaseConnection method was called
        // We can verify this by checking that the dataSource.getConnection() was called
        verify(dataSource, times(1)).getConnection();
        verify(connection, times(1)).getMetaData();
        verify(connection, times(1)).close();
    }

    @Test
    void testTestDatabaseConnection_SuccessfulConnection_ShouldNotThrowException() {
        // When & Then
        assertDoesNotThrow(() -> databaseConnectionService.testDatabaseConnection());

        // Verify interactions
        try {
            verify(dataSource, times(1)).getConnection();
            verify(connection, times(1)).getMetaData();
            verify(jdbcTemplate, times(1)).queryForObject(anyString(), eq(String.class));
            verify(connection, times(1)).close();
        } catch (Exception e) {
            fail("Verification failed: " + e.getMessage());
        }
    }

    @Test
    void testTestDatabaseConnection_ConnectionFailure_ShouldHandleSQLException() throws SQLException {
        // Given
        when(dataSource.getConnection()).thenThrow(new SQLException("Connection failed"));

        // When & Then
        assertDoesNotThrow(() -> databaseConnectionService.testDatabaseConnection());

        // Verify interactions
        verify(dataSource, times(1)).getConnection();
    }
}