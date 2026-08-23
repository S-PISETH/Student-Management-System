package dao;

import config.DatabaseConnection;
import model.Major;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MajorDAO {
    public List<Major> getAllMajors() throws SQLException {
        String sql = "SELECT MajorID, MajorCode, MajorName, Faculty, IsActive FROM Majors ORDER BY MajorName";
        List<Major> majors = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                majors.add(mapMajor(resultSet));
            }
        }

        return majors;
    }

    public Major getMajorById(int majorId) throws SQLException {
        String sql = "SELECT MajorID, MajorCode, MajorName, Faculty, IsActive FROM Majors WHERE MajorID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, majorId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapMajor(resultSet) : null;
            }
        }
    }

    private Major mapMajor(ResultSet resultSet) throws SQLException {
        return new Major(
                resultSet.getInt("MajorID"),
                resultSet.getString("MajorCode"),
                resultSet.getString("MajorName"),
                resultSet.getString("Faculty"),
                resultSet.getBoolean("IsActive")
        );
    }
}
