package dao;

import config.DatabaseConnection;
import model.UserAccount;
import util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDAO {
    public UserAccount authenticate(String username, String password) throws SQLException {
        String sql = """
                SELECT UserID, Username, FullName, RoleName, IsActive, CreatedAt
                FROM Users
                WHERE Username = ? AND PasswordHash = ? AND IsActive = 1
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, PasswordUtil.sha256(password));

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }

                UserAccount account = new UserAccount(
                        resultSet.getInt("UserID"),
                        resultSet.getString("Username"),
                        resultSet.getString("FullName"),
                        resultSet.getString("RoleName"),
                        resultSet.getBoolean("IsActive")
                );
                if (resultSet.getTimestamp("CreatedAt") != null) {
                    account.setCreatedAt(resultSet.getTimestamp("CreatedAt").toLocalDateTime());
                }
                return account;
            }
        }
    }

    public UserAccount createOrGetAccount(String username, String password, String fullName, String roleName)
            throws SQLException {
        UserAccount existing = findByUsername(username);
        if (existing != null) {
            return existing;
        }

        String sql = """
                INSERT INTO Users (Username, PasswordHash, FullName, RoleName)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, username);
            statement.setString(2, PasswordUtil.sha256(password));
            statement.setString(3, fullName);
            statement.setString(4, roleName);
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return new UserAccount(generatedKeys.getInt(1), username, fullName, roleName, true);
                }
            }
        }

        return findByUsername(username);
    }

    public UserAccount findByUsername(String username) throws SQLException {
        String sql = """
                SELECT UserID, Username, FullName, RoleName, IsActive, CreatedAt
                FROM Users
                WHERE Username = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }

                UserAccount account = new UserAccount(
                        resultSet.getInt("UserID"),
                        resultSet.getString("Username"),
                        resultSet.getString("FullName"),
                        resultSet.getString("RoleName"),
                        resultSet.getBoolean("IsActive")
                );
                if (resultSet.getTimestamp("CreatedAt") != null) {
                    account.setCreatedAt(resultSet.getTimestamp("CreatedAt").toLocalDateTime());
                }
                return account;
            }
        }
    }
}
