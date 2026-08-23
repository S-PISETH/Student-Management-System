package dao;
import config.DatabaseConnection;
import model.Major;
import model.Subject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SubjectDAO {
    
    public List<Subject> getAllSubjects() throws SQLException {
        // REMOVED: s.SubjectCode and s.IsActive to prevent SQL crashes
        String sql = "SELECT s.SubjectID, s.SubjectName, s.MajorID, m.MajorCode, m.MajorName, m.Faculty, m.IsActive " +
                "FROM Subjects s " +
                "LEFT JOIN Majors m ON s.MajorID = m.MajorID " +
                "ORDER BY s.SubjectName";
        List<Subject> subjects = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                subjects.add(mapSubject(resultSet));
            }
        }

        return subjects;
    }

    public List<Subject> getSubjectsByMajor(int majorId) throws SQLException {
        // REMOVED: s.SubjectCode and s.IsActive from the query to match your DB schema
        String sql = "SELECT s.SubjectID, s.SubjectName, s.MajorID, m.MajorCode, m.MajorName, m.Faculty, m.IsActive " +
                "FROM Subjects s " +
                "LEFT JOIN Majors m ON s.MajorID = m.MajorID " +
                "WHERE s.MajorID = ? " + 
                "ORDER BY s.SubjectName";
        List<Subject> subjects = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, majorId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    subjects.add(mapSubject(resultSet));
                }
            }
        }

        return subjects;
    }

    private Subject mapSubject(ResultSet resultSet) throws SQLException {
        Major major = new Major(
                resultSet.getInt("MajorID"),
                resultSet.getString("MajorCode"),
                resultSet.getString("MajorName"),
                resultSet.getString("Faculty"),
                resultSet.getBoolean("IsActive")
        );

        // We construct the Subject object. 
        // We pass "" for SubjectCode because it doesn't exist in the DB right now.
        return new Subject(
                resultSet.getInt("SubjectID"),
                "N/A", // Replaced getString("SubjectCode") with a safe default string
                resultSet.getString("SubjectName"),
                major,
                true // Defaulting IsActive to true since it's missing in the DB
        );
    }
}