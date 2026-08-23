package service;

import dao.ActivityLogDAO;
import dao.StudentDAO;
import dao.UserDAO;
import model.Major;
import model.Student;
import model.UserAccount;
import util.Validator;

import java.sql.SQLException;
import java.util.List;

public class StudentService {
    private final StudentDAO studentDAO;
    private final ActivityLogDAO activityLogDAO;
    private final UserDAO userDAO;

    public StudentService() {
        this(new StudentDAO(), new ActivityLogDAO(), new UserDAO());
    }

    public StudentService(StudentDAO studentDAO, ActivityLogDAO activityLogDAO) {
        this(studentDAO, activityLogDAO, new UserDAO());
    }

    public StudentService(StudentDAO studentDAO, ActivityLogDAO activityLogDAO, UserDAO userDAO) {
        this.studentDAO = studentDAO;
        this.activityLogDAO = activityLogDAO;
        this.userDAO = userDAO;
    }

    public void validateStudent(Student student) {
        List<String> errors = Validator.validateStudent(student);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join(System.lineSeparator(), errors));
        }
    }

    public int registerStudent(Student student, UserAccount currentUser) throws SQLException {
        validateStudent(student);
        if (Validator.isBlank(student.getStudentCode())) {
            student.setStudentCode(studentDAO.generateStudentCode());
        }
        if (student.getUserAccount() == null || student.getUserAccount().getUserId() <= 0) {
            student.setUserAccount(userDAO.createOrGetAccount(
                    student.getStudentCode(),
                    student.getStudentCode() + "123",
                    student.getFullName(),
                    "STUDENT"
            ));
        }
        int studentId = studentDAO.addStudent(student);
        activityLogDAO.log(currentUser, "CREATE", "Student", student.getStudentCode(),
                "Registered student " + student.getFullName() + ".");
        return studentId;
    }

    public boolean updateStudent(Student student, UserAccount currentUser) throws SQLException {
        validateStudent(student);
        boolean updated = studentDAO.updateStudent(student);
        if (updated) {
            activityLogDAO.log(currentUser, "UPDATE", "Student", student.getStudentCode(),
                    "Updated student " + student.getFullName() + ".");
        }
        return updated;
    }

    public boolean deleteStudent(int studentId, String studentCode, UserAccount currentUser) throws SQLException {
        boolean deleted = studentDAO.deleteStudent(studentId);
        if (deleted) {
            activityLogDAO.log(currentUser, "DELETE", "Student", studentCode,
                    "Deleted student " + studentCode + ".");
        }
        return deleted;
    }

    public boolean graduateStudent(Student student, UserAccount currentUser) throws SQLException {
        student.graduate();
        boolean updated = studentDAO.updateStudent(student);
        if (updated) {
            activityLogDAO.log(currentUser, "GRADUATE", "Student", student.getStudentCode(),
                    "Graduated student " + student.getFullName() + ".");
        }
        return updated;
    }

    public boolean suspendStudent(Student student, UserAccount currentUser) throws SQLException {
        student.suspend();
        boolean updated = studentDAO.updateStudent(student);
        if (updated) {
            activityLogDAO.log(currentUser, "SUSPEND", "Student", student.getStudentCode(),
                    "Suspended student " + student.getFullName() + ".");
        }
        return updated;
    }

    public boolean activateStudent(Student student, UserAccount currentUser) throws SQLException {
        student.activate();
        boolean updated = studentDAO.updateStudent(student);
        if (updated) {
            activityLogDAO.log(currentUser, "ACTIVATE", "Student", student.getStudentCode(),
                    "Activated student " + student.getFullName() + ".");
        }
        return updated;
    }

    public boolean changeMajor(Student student, Major newMajor, UserAccount currentUser) throws SQLException {
        student.setMajor(newMajor);
        boolean updated = studentDAO.updateStudent(student);
        if (updated) {
            activityLogDAO.log(currentUser, "CHANGE_MAJOR", "Student", student.getStudentCode(),
                    "Changed major for " + student.getFullName() + " to " + newMajor.getMajorName() + ".");
        }
        return updated;
    }
}
