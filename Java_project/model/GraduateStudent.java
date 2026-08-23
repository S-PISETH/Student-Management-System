package model;

import java.time.LocalDate;

/**
 * Concrete student type for graduate programs.
 */
public class GraduateStudent extends Student {
    public GraduateStudent() {
        super();
    }

    public GraduateStudent(String studentCode, String firstName, String lastName, Gender gender,
                           LocalDate dateOfBirth, String phone, String email, String address,
                           Major major, int yearLevel, StudentStatus status, double gpa,
                           String photoPath, UserAccount userAccount) {
        super(studentCode, firstName, lastName, gender, dateOfBirth, phone, email, address,
                major, yearLevel, status, gpa, photoPath, userAccount);
    }

    @Override
    public String getStudentType() {
        return "GRADUATE";
    }

    @Override
    public String getAcademicDescription() {
        return "Graduate level " + getYearLevel();
    }
}
