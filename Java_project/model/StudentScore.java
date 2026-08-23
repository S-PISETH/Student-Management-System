package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class StudentScore {
    private int scoreId;
    private int studentId;
    private int teacherId;
    private int subjectId;
    private double attendance;
    private double assignment;
    private double project;
    private double exam;
    private double participation;
    private String subject;
    private String remarks;
    private LocalDate dateRecorded;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Student and Teacher info for display
    private String studentCode;
    private String studentName;
    private String teacherCode;
    private String teacherName;

    public StudentScore() {
    }

    public StudentScore(int studentId, int teacherId, String scoreType, String subject, 
                       double score, String remarks) {
        this.studentId = studentId;
        this.teacherId = teacherId;
        this.attendance = scoreType.equals("ATTENDANCE") ? score : 0;
        this.assignment = scoreType.equals("ASSIGNMENT") ? score : 0;
        this.project = scoreType.equals("PROJECT") ? score : 0;
        this.exam = scoreType.equals("EXAM") ? score : 0;
        this.participation = scoreType.equals("PARTICIPATION") ? score : 0;
        this.subject = subject;
        this.remarks = remarks;
        this.dateRecorded = LocalDate.now();
    }

    public int getScoreId() {
        return scoreId;
    }

    public void setScoreId(int scoreId) {
        this.scoreId = scoreId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
    public double getAttendance() {
    return attendance;
}

    public void setAttendance(double attendance) {
        this.attendance = attendance;
    }

    public double getAssignment() {
        return assignment;
    }

    public void setAssignment(double assignment) {
        this.assignment = assignment;
    }

    public double getProject() {
        return project;
    }

    public void setProject(double project) {
        this.project = project;
    }

    public double getExam() {
        return exam;
    }

    public void setExam(double exam) {
        this.exam = exam;
    }

    public double getParticipation() {
        return participation;
    }

    public void setParticipation(double participation) {
        this.participation = participation;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDate getDateRecorded() {
        return dateRecorded;
    }

    public void setDateRecorded(LocalDate dateRecorded) {
        this.dateRecorded = dateRecorded;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getTeacherCode() {
        return teacherCode;
    }

    public void setTeacherCode(String teacherCode) {
        this.teacherCode = teacherCode;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }
}
