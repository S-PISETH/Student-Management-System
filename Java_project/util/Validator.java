package util;

import model.Student;
import model.StudentScore;
import model.Teacher;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class Validator {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^[0-9+()\\-\\s]{6,25}$");

    private Validator() {
    }

    public static List<String> validateStudent(Student student) {
        List<String> errors = new ArrayList<>();

        if (isBlank(student.getFirstName())) {
            errors.add("First name is required.");
        }
        if (isBlank(student.getLastName())) {
            errors.add("Last name is required.");
        }
        if (student.getGender() == null) {
            errors.add("Gender is required.");
        }
        if (student.getDateOfBirth() == null || !student.getDateOfBirth().isBefore(LocalDate.now())) {
            errors.add("Date of birth must be before today.");
        } else if (AgeCalculator.calculateAge(student.getDateOfBirth()) < 15) {
            errors.add("Student must be at least 15 years old.");
        }
        if (isBlank(student.getPhone()) || !PHONE_PATTERN.matcher(student.getPhone()).matches()) {
            errors.add("A valid phone number is required.");
        }
        if (isBlank(student.getEmail()) || !EMAIL_PATTERN.matcher(student.getEmail()).matches()) {
            errors.add("A valid email address is required.");
        }
        if (isBlank(student.getAddress())) {
            errors.add("Address is required.");
        }
        if (student.getMajor() == null || student.getMajor().getMajorId() <= 0) {
            errors.add("Major is required.");
        }
        if (student.getYearLevel() < 1 || student.getYearLevel() > 8) {
            errors.add("Year level must be between 1 and 8.");
        }
        if (student.getStatus() == null) {
            errors.add("Status is required.");
        }
        if (student.getGpa() < 0.0 || student.getGpa() > 4.0) {
            errors.add("GPA must be between 0.00 and 4.00.");
        }

        return errors;
    }

    public static List<String> validateTeacher(Teacher teacher) {
        List<String> errors = new ArrayList<>();

        if (isBlank(teacher.getFirstName())) {
            errors.add("First name is required.");
        }
        if (isBlank(teacher.getLastName())) {
            errors.add("Last name is required.");
        }
        if (teacher.getGender() == null) {
            errors.add("Gender is required.");
        }
        if (teacher.getDateOfBirth() == null || !teacher.getDateOfBirth().isBefore(LocalDate.now())) {
            errors.add("Date of birth must be before today.");
        } else if (AgeCalculator.calculateAge(teacher.getDateOfBirth()) < 21) {
            errors.add("Teacher must be at least 21 years old.");
        }
        if (isBlank(teacher.getPhone()) || !PHONE_PATTERN.matcher(teacher.getPhone()).matches()) {
            errors.add("A valid phone number is required.");
        }
        if (isBlank(teacher.getEmail()) || !EMAIL_PATTERN.matcher(teacher.getEmail()).matches()) {
            errors.add("A valid email address is required.");
        }
        if (isBlank(teacher.getAddress())) {
            errors.add("Address is required.");
        }
        if (isBlank(teacher.getDepartment())) {
            errors.add("Department is required.");
        }
        if (isBlank(teacher.getQualification())) {
            errors.add("Qualification is required.");
        }
        if (teacher.getHireDate() == null || !teacher.getHireDate().isBefore(LocalDate.now().plusDays(1))) {
            errors.add("Hire date must be today or in the past.");
        }
        if (teacher.getStatus() == null) {
            errors.add("Status is required.");
        }
        if (teacher.getSalary() <= 0) {
            errors.add("Salary must be greater than 0.");
        }

        return errors;
    }

    public static List<String> validateScore(StudentScore score) {
    List<String> errors = new ArrayList<>();

    if (score.getStudentId() <= 0) {
        errors.add("Valid student is required.");
    }

    if (score.getTeacherId() <= 0) {
        errors.add("Valid teacher is required.");
    }

    if (score.getSubjectId() <= 0) {
        errors.add("Valid subject is required.");
    }

    if (isBlank(score.getSubject())) {
        errors.add("Subject is required.");
    }

    if (score.getAttendance() < 0 || score.getAttendance() > 100) {
        errors.add("Attendance must be between 0 and 100.");
    }

    if (score.getAssignment() < 0 || score.getAssignment() > 100) {
        errors.add("Assignment must be between 0 and 100.");
    }

    if (score.getProject() < 0 || score.getProject() > 100) {
        errors.add("Project must be between 0 and 100.");
    }

    if (score.getExam() < 0 || score.getExam() > 100) {
        errors.add("Exam must be between 0 and 100.");
    }

    if (score.getParticipation() < 0 || score.getParticipation() > 100) {
        errors.add("Participation must be between 0 and 100.");
    }

    if (score.getDateRecorded() == null) {
        errors.add("Date recorded is required.");
    } else if (score.getDateRecorded().isAfter(LocalDate.now())) {
        errors.add("Date recorded cannot be in the future.");
    }

    return errors;
}

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
