package dao;

import config.DatabaseConnection;
import model.UserAccount;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogDAO {
    public void log(UserAccount user, String actionName, String entityName, String entityId, String description)
            throws SQLException {
        String sql = """
                INSERT INTO ActivityLogs (UserID, Username, ActionName, EntityName, EntityID, Description)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (user == null || user.getUserId() <= 0) {
                statement.setNull(1, java.sql.Types.INTEGER);
                statement.setString(2, "system");
            } else {
                statement.setInt(1, user.getUserId());
                statement.setString(2, user.getUsername());
            }
            statement.setString(3, actionName);
            statement.setString(4, entityName);
            statement.setString(5, entityId);
            statement.setString(6, description);
            statement.executeUpdate();
        }
    }

    public List<String[]> getRecentLogs(int limit) throws SQLException {
        String sql = """
                SELECT TOP (?) Username, ActionName, EntityName, EntityID, Description, CreatedAt
                FROM ActivityLogs
                ORDER BY CreatedAt DESC
                """;
        List<String[]> logs = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    LocalDateTime createdAt = resultSet.getTimestamp("CreatedAt").toLocalDateTime();
                    logs.add(new String[]{
                            createdAt.toString(),
                            resultSet.getString("Username"),
                            resultSet.getString("ActionName"),
                            resultSet.getString("EntityName"),
                            resultSet.getString("EntityID"),
                            resultSet.getString("Description")
                    });
                }
            }
        }

        return logs;
    }
}
