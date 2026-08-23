package gui;

import model.StudentScore;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ScoreTableModel extends AbstractTableModel {
    private final String[] columns={
        "Student ID",
        "Student Name",
        "Subject",
        "Attendance",
        "Assignment",
        "Project",
        "Exam",
        "Participation",
        "Remarks",
        "Date Recorded"
    };
    private List<StudentScore> scores = new ArrayList<>();

    public void setScores(List<StudentScore> scores) {
        this.scores = new ArrayList<>(scores);
        fireTableDataChanged();
    }

    public StudentScore getScoreAt(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= scores.size()) {
            return null;
        }
        return scores.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return scores.size();
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
    StudentScore score = scores.get(rowIndex);

    switch (columnIndex) {
        case 0:
            return score.getStudentCode();

        case 1:
            return score.getStudentName();

        case 2:
            return score.getSubject();

        case 3:
            return score.getAttendance();

        case 4:
            return score.getAssignment();

        case 5:
            return score.getProject();

        case 6:
            return score.getExam();

        case 7:
            return score.getParticipation();

        case 8:
            return score.getRemarks();

        case 9:
            return score.getDateRecorded();

        default:
            return null;
    }
}
}
