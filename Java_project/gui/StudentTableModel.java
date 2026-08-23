package gui;

import model.Student;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class StudentTableModel extends AbstractTableModel {
    private final String[] columns = {
    "Student ID",
    "Student Name",
    "Subject",
    "Attendance",
    "Assignment",
    "Project",
    "Exam",
    "Participation",
    "Remark",
    "Date"
};
    private List<Student> students = new ArrayList<>();

    public void setStudents(List<Student> students) {
        this.students = new ArrayList<>(students);
        fireTableDataChanged();
    }

    public Student getStudentAt(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= students.size()) {
            return null;
        }
        return students.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return students.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Student student = students.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> student.getStudentCode();
            case 1 -> student.getFullName();
            case 2 -> student.getStudentType();
            case 3 -> student.getGender();
            case 4 -> student.getDateOfBirth();
            case 5 -> student.getPhone();
            case 6 -> student.getEmail();
            case 7 -> student.getMajor() == null ? "" : student.getMajor().getMajorName();
            case 8 -> student.getYearLevel();
            case 9 -> student.getStatus();
            case 10 -> String.format("%.2f", student.getGpa());
            default -> "";
        };
    }
}
