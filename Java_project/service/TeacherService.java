package service;

import dao.ActivityLogDAO;
import dao.TeacherDAO;
import dao.UserDAO;
import model.Teacher;
import model.UserAccount;
import util.Validator;

import java.sql.SQLException;
import java.util.List;

public class TeacherService {
    private final TeacherDAO teacherDAO;
    private final ActivityLogDAO activityLogDAO;
    private final UserDAO userDAO;

    public TeacherService() {
        this(new TeacherDAO(), new ActivityLogDAO(), new UserDAO());
    }

    public TeacherService(TeacherDAO teacherDAO, ActivityLogDAO activityLogDAO) {
        this(teacherDAO, activityLogDAO, new UserDAO());
    }

    public TeacherService(TeacherDAO teacherDAO, ActivityLogDAO activityLogDAO, UserDAO userDAO) {
        this.teacherDAO = teacherDAO;
        this.activityLogDAO = activityLogDAO;
        this.userDAO = userDAO;
    }

    public void validateTeacher(Teacher teacher) {
        List<String> errors = Validator.validateTeacher(teacher);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join(System.lineSeparator(), errors));
        }
    }

    public int registerTeacher(Teacher teacher, UserAccount currentUser) throws SQLException {
        validateTeacher(teacher);
        if (Validator.isBlank(teacher.getTeacherCode())) {
            teacher.setTeacherCode(teacherDAO.generateTeacherCode());
        }
        if (teacher.getUserAccount() == null || teacher.getUserAccount().getUserId() <= 0) {
            teacher.setUserAccount(userDAO.createOrGetAccount(
                    teacher.getTeacherCode(),
                    teacher.getTeacherCode() + "123",
                    teacher.getFullName(),
                    "TEACHER"
            ));
        }
        int teacherId = teacherDAO.addTeacher(teacher);
        activityLogDAO.log(currentUser, "CREATE", "Teacher", teacher.getTeacherCode(),
                "Registered teacher " + teacher.getFullName() + ".");
        return teacherId;
    }

    public boolean updateTeacher(Teacher teacher, UserAccount currentUser) throws SQLException {
        validateTeacher(teacher);
        boolean updated = teacherDAO.updateTeacher(teacher);
        if (updated) {
            activityLogDAO.log(currentUser, "UPDATE", "Teacher", teacher.getTeacherCode(),
                    "Updated teacher " + teacher.getFullName() + ".");
        }
        return updated;
    }

    public boolean deleteTeacher(int teacherId, String teacherCode, UserAccount currentUser) throws SQLException {
        boolean deleted = teacherDAO.deleteTeacher(teacherId);
        if (deleted) {
            activityLogDAO.log(currentUser, "DELETE", "Teacher", teacherCode,
                    "Deleted teacher " + teacherCode + ".");
        }
        return deleted;
    }

    public boolean activateTeacher(Teacher teacher, UserAccount currentUser) throws SQLException {
        teacher.activate();
        boolean updated = teacherDAO.updateTeacher(teacher);
        if (updated) {
            activityLogDAO.log(currentUser, "ACTIVATE", "Teacher", teacher.getTeacherCode(),
                    "Activated teacher " + teacher.getFullName() + ".");
        }
        return updated;
    }

    public boolean suspendTeacher(Teacher teacher, UserAccount currentUser) throws SQLException {
        teacher.suspend();
        boolean updated = teacherDAO.updateTeacher(teacher);
        if (updated) {
            activityLogDAO.log(currentUser, "SUSPEND", "Teacher", teacher.getTeacherCode(),
                    "Suspended teacher " + teacher.getFullName() + ".");
        }
        return updated;
    }

    public boolean changeDepartment(Teacher teacher, String department, UserAccount currentUser) throws SQLException {
        teacher.setDepartment(department);
        boolean updated = teacherDAO.updateTeacher(teacher);
        if (updated) {
            activityLogDAO.log(currentUser, "CHANGE_DEPARTMENT", "Teacher", teacher.getTeacherCode(),
                    "Changed department for " + teacher.getFullName() + " to " + department + ".");
        }
        return updated;
    }
}
