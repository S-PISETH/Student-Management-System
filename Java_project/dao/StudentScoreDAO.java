package dao;

import config.DatabaseConnection;
import model.StudentScore;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class StudentScoreDAO {
    private static final String SCORE_SELECT = """
            SELECT
                ss.ScoreID,
                ss.StudentID,
                ss.SubjectID,
                ss.Attendance,
                ss.Assignment,
                ss.Project,
                ss.Exam,
                ss.Participation,
                ss.Remarks,
                ss.DateRecorded,
                ss.CreatedAt,
                ss.UpdatedAt,
                s.StudentCode,
                CONCAT(s.FirstName, ' ', s.LastName) AS StudentName,
                sub.SubjectName
            FROM StudentScores ss
            LEFT JOIN Students s ON ss.StudentID = s.StudentID
            LEFT JOIN Subjects sub ON ss.SubjectID = sub.SubjectID
            LEFT JOIN Majors m ON s.MajorID = m.MajorID
            """;

    public int addScore(StudentScore score) throws SQLException {
        String sql = """
                INSERT INTO StudentScores
                    (StudentID, SubjectID, Attendance, Assignment, Project, Exam, Participation, Remarks, DateRecorded)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            fillScoreStatement(statement, score);
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int scoreId = generatedKeys.getInt(1);
                    score.setScoreId(scoreId);
                    return scoreId;
                }
            }
        }

        return 0;
    }

    public boolean updateScore(StudentScore score) throws SQLException {
        String sql = """
                UPDATE StudentScores
                SET StudentID = ?,
                    SubjectID = ?,
                    Attendance = ?,
                    Assignment = ?,
                    Project = ?,
                    Exam = ?,
                    Participation = ?,
                    Remarks = ?,
                    DateRecorded = ?,
                    UpdatedAt = SYSDATETIME()
                WHERE ScoreID = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            fillScoreStatement(statement, score);
            statement.setInt(10, score.getScoreId());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteScore(int scoreId) throws SQLException {
        String sql = "DELETE FROM StudentScores WHERE ScoreID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, scoreId);
            return statement.executeUpdate() > 0;
        }
    }

    public StudentScore getScore(int scoreId) throws SQLException {
        String sql = SCORE_SELECT + " WHERE ss.ScoreID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, scoreId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapScore(resultSet) : null;
            }
        }
    }

    public List<StudentScore> getAllScores() throws SQLException {
        String sql = SCORE_SELECT + " ORDER BY ss.DateRecorded DESC, s.StudentCode, sub.SubjectName";
        List<StudentScore> scores = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                scores.add(mapScore(resultSet));
            }
        }

        return scores;
    }

    public List<StudentScore> getScoresByTeacher(int teacherId) throws SQLException {
        // The current database table has no TeacherID column, so teacher filtering is unavailable.
        return getAllScores();
    }

    public List<StudentScore> getScoresByStudentUser(int userId) throws SQLException {
        String sql = SCORE_SELECT + " WHERE s.UserID = ? ORDER BY ss.DateRecorded DESC, sub.SubjectName";
        List<StudentScore> scores = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    scores.add(mapScore(resultSet));
                }
            }
        }

        return scores;
    }

    public List<StudentScore> getScoresForTeacherUser(int userId) throws SQLException {
        String sql = SCORE_SELECT + """
                WHERE EXISTS (
                    SELECT 1
                    FROM Teachers t
                    WHERE t.UserID = ?
                      AND (
                          UPPER(LTRIM(RTRIM(t.Department))) = UPPER(m.MajorCode)
                          OR UPPER(LTRIM(RTRIM(t.Department))) = UPPER(m.MajorName)
                          OR UPPER(m.Faculty) LIKE '%' + UPPER(LTRIM(RTRIM(t.Department))) + '%'
                      )
                )
                ORDER BY ss.DateRecorded DESC, s.StudentCode, sub.SubjectName
                """;
        List<StudentScore> scores = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    scores.add(mapScore(resultSet));
                }
            }
        }

        return scores;
    }

    public List<StudentScore> searchScores(String keyword, String scoreType, Integer studentId, Integer teacherId) throws SQLException {
        StringBuilder sql = new StringBuilder(SCORE_SELECT);
        sql.append(" WHERE 1 = 1 ");
        List<Object> parameters = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("""
                    AND (s.StudentCode LIKE ? OR s.FirstName LIKE ? OR s.LastName LIKE ?
                         OR CONCAT(s.FirstName, ' ', s.LastName) LIKE ?
                         OR sub.SubjectName LIKE ? OR ss.Remarks LIKE ?)
                    """);
            String likeValue = "%" + keyword.trim() + "%";
            for (int i = 0; i < 6; i++) {
                parameters.add(likeValue);
            }
        }

        if (studentId != null && studentId > 0) {
            sql.append(" AND ss.StudentID = ? ");
            parameters.add(studentId);
        }

        sql.append(" ORDER BY ss.DateRecorded DESC, s.StudentCode, sub.SubjectName");
        List<StudentScore> scores = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < parameters.size(); i++) {
                statement.setObject(i + 1, parameters.get(i));
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    scores.add(mapScore(resultSet));
                }
            }
        }

        return scores;
    }

    public int countScoresByStudent(int studentId) throws SQLException {
        String sql = "SELECT COUNT(*) AS Total FROM StudentScores WHERE StudentID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, studentId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt("Total") : 0;
            }
        }
    }

    public double getAverageScoreByStudent(int studentId) throws SQLException {
        String sql = """
                SELECT AVG((Attendance + Assignment + Project + Exam + Participation) / 5.0) AS AvgScore
                FROM StudentScores
                WHERE StudentID = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, studentId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    double avg = resultSet.getDouble("AvgScore");
                    return resultSet.wasNull() ? 0.0 : avg;
                }
            }
        }

        return 0.0;
    }

    public List<StudentScore> getScoresByStudentAndSubject(int studentId, int subjectId) {
        String sql = SCORE_SELECT + " WHERE ss.StudentID = ? AND ss.SubjectID = ? ORDER BY ss.DateRecorded DESC";
        List<StudentScore> scores = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, studentId);
            statement.setInt(2, subjectId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    scores.add(mapScore(resultSet));
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return scores;
    }

    private void fillScoreStatement(PreparedStatement statement, StudentScore score) throws SQLException {
        statement.setInt(1, score.getStudentId());
        statement.setInt(2, score.getSubjectId());
        statement.setDouble(3, score.getAttendance());
        statement.setDouble(4, score.getAssignment());
        statement.setDouble(5, score.getProject());
        statement.setDouble(6, score.getExam());
        statement.setDouble(7, score.getParticipation());
        statement.setString(8, score.getRemarks());
        statement.setDate(9, Date.valueOf(score.getDateRecorded()));
    }

    private StudentScore mapScore(ResultSet resultSet) throws SQLException {
        StudentScore score = new StudentScore();
        score.setScoreId(resultSet.getInt("ScoreID"));
        score.setStudentId(resultSet.getInt("StudentID"));
        score.setTeacherId(0);
        score.setSubjectId(resultSet.getInt("SubjectID"));
        score.setAttendance(resultSet.getDouble("Attendance"));
        score.setAssignment(resultSet.getDouble("Assignment"));
        score.setProject(resultSet.getDouble("Project"));
        score.setExam(resultSet.getDouble("Exam"));
        score.setParticipation(resultSet.getDouble("Participation"));
        score.setSubject(resultSet.getString("SubjectName"));
        score.setRemarks(resultSet.getString("Remarks"));

        Date dateRecorded = resultSet.getDate("DateRecorded");
        if (dateRecorded != null) {
            score.setDateRecorded(dateRecorded.toLocalDate());
        }

        Timestamp createdAt = resultSet.getTimestamp("CreatedAt");
        if (createdAt != null) {
            score.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = resultSet.getTimestamp("UpdatedAt");
        if (updatedAt != null) {
            score.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        score.setStudentCode(resultSet.getString("StudentCode"));
        score.setStudentName(resultSet.getString("StudentName"));
        score.setTeacherCode("");
        score.setTeacherName("");

        return score;
    }
}
