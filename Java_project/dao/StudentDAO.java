package dao;

import config.DatabaseConnection;
import model.Gender;
import model.GraduateStudent;
import model.Major;
import model.Student;
import model.StudentStatus;
import model.UndergraduateStudent;
import model.UserAccount;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StudentDAO {
    private static final String STUDENT_SELECT = """
            SELECT s.StudentID, s.StudentCode, s.StudentType, s.FirstName, s.LastName, s.Gender, s.DOB,
                   s.Phone, s.Email, s.Address, s.YearLevel, s.Status, s.GPA, s.PhotoPath,
                   s.CreatedAt, s.UpdatedAt,
                   m.MajorID, m.MajorCode, m.MajorName, m.Faculty, m.IsActive,
                   u.UserID, u.Username, u.FullName, u.RoleName, u.IsActive AS UserIsActive
            FROM Students s
            INNER JOIN Majors m ON s.MajorID = m.MajorID
            LEFT JOIN Users u ON s.UserID = u.UserID
            """;

    public int addStudent(Student student) throws SQLException {
        String sql = """
                INSERT INTO Students
                    (StudentCode, StudentType, FirstName, LastName, Gender, DOB, Phone, Email, Address,
                     MajorID, YearLevel, Status, GPA, PhotoPath, UserID)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            fillStudentStatement(statement, student, false);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    int studentId = keys.getInt(1);
                    student.setStudentId(studentId);
                    return studentId;
                }
            }
        }

        return 0;
    }

    public boolean updateStudent(Student student) throws SQLException {
        String sql = """
                UPDATE Students
                SET StudentCode = ?, StudentType = ?, FirstName = ?, LastName = ?, Gender = ?, DOB = ?,
                    Phone = ?, Email = ?, Address = ?, MajorID = ?, YearLevel = ?, Status = ?,
                    GPA = ?, PhotoPath = ?, UserID = ?, UpdatedAt = SYSDATETIME()
                WHERE StudentID = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            fillStudentStatement(statement, student, false);
            statement.setInt(16, student.getStudentId());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteStudent(int studentId) throws SQLException {
        String sql = "DELETE FROM Students WHERE StudentID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, studentId);
            return statement.executeUpdate() > 0;
        }
    }

    public Student getStudent(int studentId) throws SQLException {
        String sql = STUDENT_SELECT + " WHERE s.StudentID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, studentId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapStudent(resultSet) : null;
            }
        }
    }

    public Student getStudentByUserId(int userId) throws SQLException {
        String sql = STUDENT_SELECT + " WHERE s.UserID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapStudent(resultSet) : null;
            }
        }
    }

    public List<Student> getAllStudents() throws SQLException {
        String sql = STUDENT_SELECT + " ORDER BY s.StudentCode";
        List<Student> students = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                students.add(mapStudent(resultSet));
            }
        }

        return students;
    }

    public List<Student> getStudentsForTeacherUser(int userId) throws SQLException {
        String sql = STUDENT_SELECT + """
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
                ORDER BY s.StudentCode
                """;
        List<Student> students = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    students.add(mapStudent(resultSet));
                }
            }
        }

        return students;
    }

    public List<Student> searchStudents(String keyword, StudentStatus status, Integer majorId) throws SQLException {
        StringBuilder sql = new StringBuilder(STUDENT_SELECT);
        sql.append(" WHERE 1 = 1 ");
        List<Object> parameters = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("""
                     AND (s.StudentCode LIKE ? OR s.FirstName LIKE ? OR s.LastName LIKE ?
                          OR s.Email LIKE ? OR s.Phone LIKE ?)
                    """);
            String likeValue = "%" + keyword.trim() + "%";
            for (int index = 0; index < 5; index++) {
                parameters.add(likeValue);
            }
        }

        if (status != null) {
            sql.append(" AND s.Status = ? ");
            parameters.add(status.toDatabaseValue());
        }

        if (majorId != null && majorId > 0) {
            sql.append(" AND s.MajorID = ? ");
            parameters.add(majorId);
        }

        sql.append(" ORDER BY s.StudentCode");
        List<Student> students = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            for (int index = 0; index < parameters.size(); index++) {
                statement.setObject(index + 1, parameters.get(index));
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    students.add(mapStudent(resultSet));
                }
            }
        }

        return students;
    }

    public String generateStudentCode() throws SQLException {
        String sql = """
                SELECT ISNULL(MAX(TRY_CONVERT(INT, SUBSTRING(StudentCode, 3, 20))), 0) + 1 AS NextNumber
                FROM Students
                WHERE StudentCode LIKE 'ST%'
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return String.format("ST%03d", resultSet.getInt("NextNumber"));
            }
        }

        return "ST001";
    }

    public int countStudents() throws SQLException {
        String sql = "SELECT COUNT(*) AS TotalStudents FROM Students";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            return resultSet.next() ? resultSet.getInt("TotalStudents") : 0;
        }
    }

    public Map<Gender, Integer> countByGender() throws SQLException {
        String sql = "SELECT Gender, COUNT(*) AS Total FROM Students GROUP BY Gender";
        Map<Gender, Integer> counts = new LinkedHashMap<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                counts.put(Gender.fromDatabaseValue(resultSet.getString("Gender")), resultSet.getInt("Total"));
            }
        }

        return counts;
    }

    public Map<String, Integer> countByMajor() throws SQLException {
        String sql = """
                SELECT m.MajorName, COUNT(*) AS Total
                FROM Students s
                INNER JOIN Majors m ON s.MajorID = m.MajorID
                GROUP BY m.MajorName
                ORDER BY m.MajorName
                """;
        Map<String, Integer> counts = new LinkedHashMap<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                counts.put(resultSet.getString("MajorName"), resultSet.getInt("Total"));
            }
        }

        return counts;
    }

    private void fillStudentStatement(PreparedStatement statement, Student student, boolean includeId)
            throws SQLException {
        statement.setString(1, student.getStudentCode());
        statement.setString(2, student.getStudentType());
        statement.setString(3, student.getFirstName());
        statement.setString(4, student.getLastName());
        statement.setString(5, student.getGender().toDatabaseValue());
        statement.setDate(6, Date.valueOf(student.getDateOfBirth()));
        statement.setString(7, student.getPhone());
        statement.setString(8, student.getEmail());
        statement.setString(9, student.getAddress());
        statement.setInt(10, student.getMajor().getMajorId());
        statement.setInt(11, student.getYearLevel());
        statement.setString(12, student.getStatus().toDatabaseValue());
        statement.setDouble(13, student.getGpa());
        statement.setString(14, student.getPhotoPath());

        if (student.getUserAccount() == null || student.getUserAccount().getUserId() <= 0) {
            statement.setNull(15, java.sql.Types.INTEGER);
        } else {
            statement.setInt(15, student.getUserAccount().getUserId());
        }

        if (includeId) {
            statement.setInt(16, student.getStudentId());
        }
    }

    private Student mapStudent(ResultSet resultSet) throws SQLException {
        Major major = new Major(
                resultSet.getInt("MajorID"),
                resultSet.getString("MajorCode"),
                resultSet.getString("MajorName"),
                resultSet.getString("Faculty"),
                resultSet.getBoolean("IsActive")
        );

        UserAccount userAccount = null;
        int userId = resultSet.getInt("UserID");
        if (!resultSet.wasNull()) {
            userAccount = new UserAccount(
                    userId,
                    resultSet.getString("Username"),
                    resultSet.getString("FullName"),
                    resultSet.getString("RoleName"),
                    resultSet.getBoolean("UserIsActive")
            );
        }

        Student student;
        String studentType = resultSet.getString("StudentType");
        if ("GRADUATE".equalsIgnoreCase(studentType)) {
            student = new GraduateStudent();
        } else {
            student = new UndergraduateStudent();
        }

        student.setStudentId(resultSet.getInt("StudentID"));
        student.setStudentCode(resultSet.getString("StudentCode"));
        student.setFirstName(resultSet.getString("FirstName"));
        student.setLastName(resultSet.getString("LastName"));
        student.setGender(Gender.fromDatabaseValue(resultSet.getString("Gender")));
        student.setDateOfBirth(resultSet.getDate("DOB").toLocalDate());
        student.setPhone(resultSet.getString("Phone"));
        student.setEmail(resultSet.getString("Email"));
        student.setAddress(resultSet.getString("Address"));
        student.setMajor(major);
        student.setYearLevel(resultSet.getInt("YearLevel"));
        student.setStatus(StudentStatus.fromDatabaseValue(resultSet.getString("Status")));
        student.setGpa(resultSet.getDouble("GPA"));
        student.setPhotoPath(resultSet.getString("PhotoPath"));
        student.setUserAccount(userAccount);
        setDateTimes(resultSet, student);
        return student;
    }

    private void setDateTimes(ResultSet resultSet, Student student) throws SQLException {
        Timestamp createdAt = resultSet.getTimestamp("CreatedAt");
        Timestamp updatedAt = resultSet.getTimestamp("UpdatedAt");
        if (createdAt != null) {
            student.setCreatedAt(createdAt.toLocalDateTime());
        }
        if (updatedAt != null) {
            student.setUpdatedAt(updatedAt.toLocalDateTime());
        }
    }
}
