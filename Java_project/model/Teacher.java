package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Teacher is another concrete Person type, giving the project a richer
 * inheritance and polymorphism example beyond the Student hierarchy.
 */
public class Teacher extends Person {
    private int teacherId;
    private String teacherCode;
    private String department;
    private String qualification;
    private LocalDate hireDate;
    private TeacherStatus status;
    private double salary;
    private String photoPath;
    private UserAccount userAccount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Teacher() {
        this.status = TeacherStatus.ACTIVE;
    }

    public Teacher(String teacherCode, String firstName, String lastName, Gender gender,
                   LocalDate dateOfBirth, String phone, String email, String address,
                   String department, String qualification, LocalDate hireDate,
                   TeacherStatus status, double salary, String photoPath, UserAccount userAccount) {
        super(firstName, lastName, gender, dateOfBirth, phone, email, address);
        this.teacherCode = teacherCode;
        this.department = department;
        this.qualification = qualification;
        this.hireDate = hireDate;
        this.status = status == null ? TeacherStatus.ACTIVE : status;
        this.salary = salary;
        this.photoPath = photoPath;
        this.userAccount = userAccount;
    }

    @Override
    public String getDisplayRole() {
        return "Teacher";
    }

    public boolean isActive() {
        return status == TeacherStatus.ACTIVE;
    }

    public void activate() {
        status = TeacherStatus.ACTIVE;
    }

    public void suspend() {
        status = TeacherStatus.SUSPENDED;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherCode() {
        return teacherCode;
    }

    public void setTeacherCode(String teacherCode) {
        this.teacherCode = teacherCode;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public TeacherStatus getStatus() {
        return status;
    }

    public void setStatus(TeacherStatus status) {
        this.status = status;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
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
}
