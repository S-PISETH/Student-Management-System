package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Central JDBC connection factory for the Student Management System.
 */
public final class DatabaseConnection {
    private static final String DRIVER_CLASS = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    private static final String DEFAULT_URL =
            "jdbc:sqlserver://localhost:1433;"
                    + "databaseName=UniversityStudentManagementDB;"
                    + "encrypt=true;"
                    + "trustServerCertificate=true";

    private static final String DEFAULT_USERNAME = "yumiena";
    private static final String DEFAULT_PASSWORD = "1234";

    private DatabaseConnection() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated.");
    }

    static {
        try {
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException exception) {
            throw new ExceptionInInitializerError(
                    "SQL Server JDBC driver was not found. Add the Microsoft mssql-jdbc driver to the classpath.");
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(getDatabaseUrl(), getDatabaseUsername(), getDatabasePassword());
    }

    public static boolean testConnection() {
        try (Connection connection = getConnection()) {
            return connection != null && !connection.isClosed();
        } catch (SQLException exception) {
            System.err.println("Database connection failed: " + exception.getMessage());
            return false;
        }
    }

    public static String getDatabaseUrl() {
        return firstNonBlank(
                System.getProperty("sms.db.url"),
                System.getenv("SMS_DB_URL"),
                DEFAULT_URL
        );
    }

    public static String getDatabaseUsername() {
        return firstNonBlank(
                System.getProperty("sms.db.username"),
                System.getenv("SMS_DB_USERNAME"),
                DEFAULT_USERNAME
        );
    }

    public static String getDatabasePassword() {
        return firstNonBlank(
                System.getProperty("sms.db.password"),
                System.getenv("SMS_DB_PASSWORD"),
                DEFAULT_PASSWORD
        );
    }

    private static String firstNonBlank(String firstValue, String secondValue, String fallbackValue) {
        if (firstValue != null && !firstValue.trim().isEmpty()) {
            return firstValue.trim();
        }

        if (secondValue != null && !secondValue.trim().isEmpty()) {
            return secondValue.trim();
        }

        return fallbackValue;
    }
}
