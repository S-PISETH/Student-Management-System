package model;

import java.time.LocalDate;

/**
 * Concrete student type for undergraduate programs.
 */
public class UndergraduateStudent extends Student {
    public UndergraduateStudent() {
        super();
    }

    public UndergraduateStudent(String studentCode, String firstName, String lastName, Gender gender,
                                LocalDate dateOfBirth, String phone, String email, String address,
                                Major major, int yearLevel, StudentStatus status, double gpa,
                                String photoPath, UserAccount userAccount) {
        super(studentCode, firstName, lastName, gender, dateOfBirth, phone, email, address,
                major, yearLevel, status, gpa, photoPath, userAccount);
    }

    @Override
    public String getStudentType() {
        return "UNDERGRADUATE";
    }

    @Override
    public String getAcademicDescription() {
        return "Undergraduate year " + getYearLevel();
    }
}
