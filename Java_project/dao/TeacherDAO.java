package dao;

import config.DatabaseConnection;
import model.Gender;
import model.Teacher;
import model.TeacherStatus;
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

public class TeacherDAO {
    private static final String TEACHER_SELECT = """
            SELECT t.TeacherID, t.TeacherCode, t.FirstName, t.LastName, t.Gender, t.DOB,
                   t.Phone, t.Email, t.Address, t.Department, t.Qualification, t.HireDate,
                   t.Status, t.Salary, t.PhotoPath, t.CreatedAt, t.UpdatedAt,
                   u.UserID, u.Username, u.FullName, u.RoleName, u.IsActive AS UserIsActive
            FROM Teachers t
            LEFT JOIN Users u ON t.UserID = u.UserID
            """;

    public int addTeacher(Teacher teacher) throws SQLException {
        String sql = """
                INSERT INTO Teachers
                    (TeacherCode, FirstName, LastName, Gender, DOB, Phone, Email, Address,
                     Department, Qualification, HireDate, Status, Salary, PhotoPath, UserID)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            fillTeacherStatement(statement, teacher);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    int teacherId = keys.getInt(1);
                    teacher.setTeacherId(teacherId);
                    return teacherId;
                }
            }
        }

        return 0;
    }

    public boolean updateTeacher(Teacher teacher) throws SQLException {
        String sql = """
                UPDATE Teachers
                SET TeacherCode = ?, FirstName = ?, LastName = ?, Gender = ?, DOB = ?,
                    Phone = ?, Email = ?, Address = ?, Department = ?, Qualification = ?,
                    HireDate = ?, Status = ?, Salary = ?, PhotoPath = ?, UserID = ?,
                    UpdatedAt = SYSDATETIME()
                WHERE TeacherID = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            fillTeacherStatement(statement, teacher);
            statement.setInt(16, teacher.getTeacherId());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteTeacher(int teacherId) throws SQLException {
        String sql = "DELETE FROM Teachers WHERE TeacherID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, teacherId);
            return statement.executeUpdate() > 0;
        }
    }

    public Teacher getTeacher(int teacherId) throws SQLException {
        String sql = TEACHER_SELECT + " WHERE t.TeacherID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, teacherId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapTeacher(resultSet) : null;
            }
        }
    }

    public Teacher getTeacherByUserId(int userId) throws SQLException {
        String sql = TEACHER_SELECT + " WHERE t.UserID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapTeacher(resultSet) : null;
            }
        }
    }

    public List<Teacher> getAllTeachers() throws SQLException {
        String sql = TEACHER_SELECT + " ORDER BY t.TeacherCode";
        List<Teacher> teachers = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                teachers.add(mapTeacher(resultSet));
            }
        }

        return teachers;
    }

    public List<Teacher> searchTeachers(String keyword, TeacherStatus status, String department) throws SQLException {
        StringBuilder sql = new StringBuilder(TEACHER_SELECT);
        sql.append(" WHERE 1 = 1 ");
        List<Object> parameters = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("""
                     AND (t.TeacherCode LIKE ? OR t.FirstName LIKE ? OR t.LastName LIKE ?
                          OR t.Email LIKE ? OR t.Phone LIKE ? OR t.Department LIKE ?)
                    """);
            String likeValue = "%" + keyword.trim() + "%";
            for (int index = 0; index < 6; index++) {
                parameters.add(likeValue);
            }
        }

        if (status != null) {
            sql.append(" AND t.Status = ? ");
            parameters.add(status.toDatabaseValue());
        }

        if (department != null && !department.trim().isEmpty()) {
            sql.append(" AND t.Department = ? ");
            parameters.add(department.trim());
        }

        sql.append(" ORDER BY t.TeacherCode");
        List<Teacher> teachers = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            for (int index = 0; index < parameters.size(); index++) {
                statement.setObject(index + 1, parameters.get(index));
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    teachers.add(mapTeacher(resultSet));
                }
            }
        }

        return teachers;
    }

    public String generateTeacherCode() throws SQLException {
        String sql = """
                SELECT ISNULL(MAX(TRY_CONVERT(INT, SUBSTRING(TeacherCode, 3, 20))), 0) + 1 AS NextNumber
                FROM Teachers
                WHERE TeacherCode LIKE 'TC%'
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return String.format("TC%03d", resultSet.getInt("NextNumber"));
            }
        }

        return "TC001";
    }

    public int countTeachers() throws SQLException {
        String sql = "SELECT COUNT(*) AS TotalTeachers FROM Teachers";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            return resultSet.next() ? resultSet.getInt("TotalTeachers") : 0;
        }
    }

    public Map<String, Integer> countByDepartment() throws SQLException {
        String sql = """
                SELECT Department, COUNT(*) AS Total
                FROM Teachers
                GROUP BY Department
                ORDER BY Department
                """;
        Map<String, Integer> counts = new LinkedHashMap<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                counts.put(resultSet.getString("Department"), resultSet.getInt("Total"));
            }
        }

        return counts;
    }

    private void fillTeacherStatement(PreparedStatement statement, Teacher teacher) throws SQLException {
        statement.setString(1, teacher.getTeacherCode());
        statement.setString(2, teacher.getFirstName());
        statement.setString(3, teacher.getLastName());
        statement.setString(4, teacher.getGender().toDatabaseValue());
        statement.setDate(5, Date.valueOf(teacher.getDateOfBirth()));
        statement.setString(6, teacher.getPhone());
        statement.setString(7, teacher.getEmail());
        statement.setString(8, teacher.getAddress());
        statement.setString(9, teacher.getDepartment());
        statement.setString(10, teacher.getQualification());
        statement.setDate(11, Date.valueOf(teacher.getHireDate()));
        statement.setString(12, teacher.getStatus().toDatabaseValue());
        statement.setDouble(13, teacher.getSalary());
        statement.setString(14, teacher.getPhotoPath());

        if (teacher.getUserAccount() == null || teacher.getUserAccount().getUserId() <= 0) {
            statement.setNull(15, java.sql.Types.INTEGER);
        } else {
            statement.setInt(15, teacher.getUserAccount().getUserId());
        }
    }

    private Teacher mapTeacher(ResultSet resultSet) throws SQLException {
        Teacher teacher = new Teacher();
        teacher.setTeacherId(resultSet.getInt("TeacherID"));
        teacher.setTeacherCode(resultSet.getString("TeacherCode"));
        teacher.setFirstName(resultSet.getString("FirstName"));
        teacher.setLastName(resultSet.getString("LastName"));
        teacher.setGender(Gender.fromDatabaseValue(resultSet.getString("Gender")));
        teacher.setDateOfBirth(resultSet.getDate("DOB").toLocalDate());
        teacher.setPhone(resultSet.getString("Phone"));
        teacher.setEmail(resultSet.getString("Email"));
        teacher.setAddress(resultSet.getString("Address"));
        teacher.setDepartment(resultSet.getString("Department"));
        teacher.setQualification(resultSet.getString("Qualification"));
        teacher.setHireDate(resultSet.getDate("HireDate").toLocalDate());
        teacher.setStatus(TeacherStatus.fromDatabaseValue(resultSet.getString("Status")));
        teacher.setSalary(resultSet.getDouble("Salary"));
        teacher.setPhotoPath(resultSet.getString("PhotoPath"));

        int userId = resultSet.getInt("UserID");
        if (!resultSet.wasNull()) {
            teacher.setUserAccount(new UserAccount(
                    userId,
                    resultSet.getString("Username"),
                    resultSet.getString("FullName"),
                    resultSet.getString("RoleName"),
                    resultSet.getBoolean("UserIsActive")
            ));
        }

        Timestamp createdAt = resultSet.getTimestamp("CreatedAt");
        Timestamp updatedAt = resultSet.getTimestamp("UpdatedAt");
        if (createdAt != null) {
            teacher.setCreatedAt(createdAt.toLocalDateTime());
        }
        if (updatedAt != null) {
            teacher.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return teacher;
    }
}
