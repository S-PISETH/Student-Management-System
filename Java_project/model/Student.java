package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Base student class. It demonstrates inheritance from Person and composition
 * through Major and UserAccount references.
 */
public abstract class Student extends Person implements Manageable {
    private int studentId;
    private String studentCode;
    private Major major;
    private int yearLevel;
    private StudentStatus status;
    private double gpa;
    private String photoPath;
    private UserAccount userAccount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    protected Student() {
        this.status = StudentStatus.ACTIVE;
    }

    protected Student(String studentCode, String firstName, String lastName, Gender gender,
                      LocalDate dateOfBirth, String phone, String email, String address,
                      Major major, int yearLevel, StudentStatus status, double gpa,
                      String photoPath, UserAccount userAccount) {
        super(firstName, lastName, gender, dateOfBirth, phone, email, address);
        this.studentCode = studentCode;
        this.major = major;
        this.yearLevel = yearLevel;
        this.status = status == null ? StudentStatus.ACTIVE : status;
        this.gpa = gpa;
        this.photoPath = photoPath;
        this.userAccount = userAccount;
    }

    public abstract String getStudentType();

    public abstract String getAcademicDescription();

    @Override
    public String getDisplayRole() {
        return getStudentType();
    }

    @Override
    public void activate() {
        status = StudentStatus.ACTIVE;
    }

    @Override
    public void suspend() {
        status = StudentStatus.SUSPENDED;
    }

    @Override
    public void graduate() {
        status = StudentStatus.GRADUATED;
    }

    @Override
    public boolean isActive() {
        return status == StudentStatus.ACTIVE;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public Major getMajor() {
        return major;
    }

    public void setMajor(Major major) {
        this.major = major;
    }

    public int getYearLevel() {
        return yearLevel;
    }

    public void setYearLevel(int yearLevel) {
        this.yearLevel = yearLevel;
    }

    public StudentStatus getStatus() {
        return status;
    }

    public void setStatus(StudentStatus status) {
        this.status = status;
    }

    public double getGpa() {
        return gpa;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
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

    @Override
    public String toString() {
        return (studentCode != null ? studentCode + " - " : "") + getFullName();
    }
}
