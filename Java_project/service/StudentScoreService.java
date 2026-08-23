package service;

import dao.ActivityLogDAO;
import dao.StudentScoreDAO;
import model.GradeSummary;
import model.StudentScore;
import model.UserAccount;
import util.Validator;

import java.sql.SQLException;
// import java.util.ArrayList;
import java.util.List;

public class StudentScoreService {
    private final StudentScoreDAO scoreDAO;
    private final ActivityLogDAO activityLogDAO;

    public StudentScoreService() {
        this(new StudentScoreDAO(), new ActivityLogDAO());
    }

    public StudentScoreService(StudentScoreDAO scoreDAO, ActivityLogDAO activityLogDAO) {
        this.scoreDAO = scoreDAO;
        this.activityLogDAO = activityLogDAO;
    }

    public void validateScore(StudentScore score) {
        List<String> errors = Validator.validateScore(score);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join(System.lineSeparator(), errors));
        }
    }

    public int recordScore(StudentScore score, UserAccount currentUser) throws SQLException {
        validateScore(score);
        int scoreId = scoreDAO.addScore(score);
        activityLogDAO.log(currentUser, "CREATE", "StudentScore", String.valueOf(scoreId),
                "Recorded " + score.getAttendance() + " score for student ID " + score.getStudentId() + ".");
        return scoreId;
    }

    public boolean updateScore(StudentScore score, UserAccount currentUser) throws SQLException {
        validateScore(score);
        boolean updated = scoreDAO.updateScore(score);
        if (updated) {
            activityLogDAO.log(currentUser, "UPDATE", "StudentScore", String.valueOf(score.getScoreId()),
                    "Updated " + score.getAttendance() + " score for student ID " + score.getStudentId() + ".");
        }
        return updated;
    }

    public boolean deleteScore(int scoreId, UserAccount currentUser) throws SQLException {
        StudentScore score = scoreDAO.getScore(scoreId);
        boolean deleted = scoreDAO.deleteScore(scoreId);
        if (deleted && score != null) {
            activityLogDAO.log(currentUser, "DELETE", "StudentScore", String.valueOf(scoreId),
                    "Deleted score record " + scoreId + ".");
        }
        return deleted;
    }

    // public List<StudentScore> getScoresByStudent(int studentId) throws SQLException {
    //     return scoreDAO.getScoresByStudent(studentId);
    // }

    public List<StudentScore> getScoresByTeacher(int teacherId) throws SQLException {
        return scoreDAO.getScoresByTeacher(teacherId);
    }

    public List<StudentScore> searchScores(String keyword, String scoreType, Integer studentId, Integer teacherId) throws SQLException {
        return scoreDAO.searchScores(keyword, scoreType, studentId, teacherId);
    }

    public double getAverageScoreByStudent(int studentId) throws SQLException {
        return scoreDAO.getAverageScoreByStudent(studentId);
    }

    public int countScoresByStudent(int studentId) throws SQLException {
        return scoreDAO.countScoresByStudent(studentId);
    }

    public GradeSummary calculateSubjectGrade(StudentScore score) {
    double attendance = score.getAttendance();

    double coursework =
        (score.getAssignment()
        + score.getProject()
        + score.getParticipation()) / 3.0;

    double exam = score.getExam();

    double finalGrade =
        attendance * 0.10 +
        coursework * 0.30 +
        exam * 0.60;

    String letter = determineLetterGrade(finalGrade);

    return new GradeSummary(
        attendance,
        coursework,
        exam,
        finalGrade,
        letter
    );
}

    public GradeSummary calculateSubjectGrade(List<StudentScore> scores) {
        if (scores == null || scores.isEmpty()) {
            return new GradeSummary(0, 0, 0, 0, "F");
        }

        double totalAttendance = 0;
        double totalCoursework = 0;
        double totalExam = 0;

        for (StudentScore score : scores) {
            totalAttendance += score.getAttendance();
            totalCoursework += (score.getAssignment() + score.getProject() + score.getParticipation()) / 3.0;
            totalExam += score.getExam();
        }

        int count = scores.size();
        double attendanceAvg = totalAttendance / count;
        double courseworkAvg = totalCoursework / count;
        double examAvg = totalExam / count;

        double finalWeightedGrade =
            attendanceAvg * 0.10 +
            courseworkAvg * 0.30 +
            examAvg * 0.60;

        String letterGrade = determineLetterGrade(finalWeightedGrade);

        return new GradeSummary(attendanceAvg, courseworkAvg, examAvg, finalWeightedGrade, letterGrade);
    }

    private String determineLetterGrade(double score) {
        if (score >= 90) return "A";
        if (score >= 80) return "B";
        if (score >= 70) return "C";
        if (score >= 60) return "D";
        return "F";
    }
}

