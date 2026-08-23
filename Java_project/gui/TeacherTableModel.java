package gui;

import model.Teacher;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class TeacherTableModel extends AbstractTableModel {
    private final String[] columns = {
            "Code", "Name", "Gender", "DOB", "Phone", "Email", "Department", "Qualification", "Hire Date", "Status", "Salary"
    };
    private List<Teacher> teachers = new ArrayList<>();

    public void setTeachers(List<Teacher> teachers) {
        this.teachers = new ArrayList<>(teachers);
        fireTableDataChanged();
    }

    public Teacher getTeacherAt(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= teachers.size()) {
            return null;
        }
        return teachers.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return teachers.size();
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
        Teacher teacher = teachers.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> teacher.getTeacherCode();
            case 1 -> teacher.getFullName();
            case 2 -> teacher.getGender();
            case 3 -> teacher.getDateOfBirth();
            case 4 -> teacher.getPhone();
            case 5 -> teacher.getEmail();
            case 6 -> teacher.getDepartment();
            case 7 -> teacher.getQualification();
            case 8 -> teacher.getHireDate();
            case 9 -> teacher.getStatus();
            case 10 -> String.format("$%.2f", teacher.getSalary());
            default -> "";
        };
    }
}
