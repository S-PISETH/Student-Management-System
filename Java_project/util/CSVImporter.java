package util;

import model.Gender;
import model.GraduateStudent;
import model.Major;
import model.Student;
import model.StudentStatus;
import model.UndergraduateStudent;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CSVImporter {
    private CSVImporter() {
    }

    public static List<Student> importStudents(Path filePath, List<Major> availableMajors) throws IOException {
        Map<String, Major> majorLookup = buildMajorLookup(availableMajors);
        List<Student> students = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    if (line.toLowerCase().startsWith("studentcode,")) {
                        continue;
                    }
                }

                List<String> columns = parseCsvLine(line);
                if (columns.size() < 14) {
                    continue;
                }

                String type = columns.get(1).trim();
                Student student = "GRADUATE".equalsIgnoreCase(type) ? new GraduateStudent() : new UndergraduateStudent();
                student.setStudentCode(columns.get(0).trim());
                student.setFirstName(columns.get(2).trim());
                student.setLastName(columns.get(3).trim());
                student.setGender(Gender.fromDatabaseValue(columns.get(4)));
                student.setDateOfBirth(LocalDate.parse(columns.get(5).trim()));
                student.setPhone(columns.get(6).trim());
                student.setEmail(columns.get(7).trim());
                student.setAddress(columns.get(8).trim());
                student.setMajor(majorLookup.get(columns.get(9).trim().toLowerCase()));
                student.setYearLevel(Integer.parseInt(columns.get(10).trim()));
                student.setStatus(StudentStatus.fromDatabaseValue(columns.get(11)));
                student.setGpa(Double.parseDouble(columns.get(12).trim()));
                student.setPhotoPath(columns.get(13).trim());
                students.add(student);
            }
        }

        return students;
    }

    private static Map<String, Major> buildMajorLookup(List<Major> availableMajors) {
        Map<String, Major> lookup = new HashMap<>();
        for (Major major : availableMajors) {
            lookup.put(major.getMajorName().toLowerCase(), major);
            lookup.put(major.getMajorCode().toLowerCase(), major);
        }
        return lookup;
    }

    private static List<String> parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;

        for (int index = 0; index < line.length(); index++) {
            char character = line.charAt(index);
            if (character == '"') {
                if (quoted && index + 1 < line.length() && line.charAt(index + 1) == '"') {
                    current.append('"');
                    index++;
                } else {
                    quoted = !quoted;
                }
            } else if (character == ',' && !quoted) {
                values.add(current.toString());
                current.setLength(0);
            } else {
                current.append(character);
            }
        }
        values.add(current.toString());
        return values;
    }
}
