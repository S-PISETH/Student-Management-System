package util;

import model.Student;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class CSVExporter {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private CSVExporter() {
    }

    public static void exportStudents(List<Student> students, Path filePath) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
            writer.write("StudentCode,StudentType,FirstName,LastName,Gender,DOB,Phone,Email,Address,Major,YearLevel,Status,GPA,PhotoPath");
            writer.newLine();

            for (Student student : students) {
                writer.write(String.join(",",
                        escape(student.getStudentCode()),
                        escape(student.getStudentType()),
                        escape(student.getFirstName()),
                        escape(student.getLastName()),
                        escape(student.getGender().name()),
                        escape(DATE_FORMATTER.format(student.getDateOfBirth())),
                        escape(student.getPhone()),
                        escape(student.getEmail()),
                        escape(student.getAddress()),
                        escape(student.getMajor().getMajorName()),
                        String.valueOf(student.getYearLevel()),
                        escape(student.getStatus().name()),
                        String.format("%.2f", student.getGpa()),
                        escape(student.getPhotoPath())
                ));
                writer.newLine();
            }
        }
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }
}
