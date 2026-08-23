package model;

public class GradeSummary {
    private double attendanceAvg;
    private double courseworkAvg; // Changed from midtermClusterAvg
    private double examScore;     // Changed from finalExam
    private double finalWeightedGrade;
    private String letterGrade;

    // Constructor
    public GradeSummary(double attendanceAvg, double courseworkAvg, double examScore, double finalWeightedGrade, String letterGrade) {
        this.attendanceAvg = attendanceAvg;
        this.courseworkAvg = courseworkAvg;
        this.examScore = examScore;
        this.finalWeightedGrade = finalWeightedGrade;
        this.letterGrade = letterGrade;
    }

    // Getters
    public double getAttendanceAvg() { return attendanceAvg; }
    public double getCourseworkAvg() { return courseworkAvg; }
    public double getExamScore() { return examScore; }
    public double getFinalWeightedGrade() { return finalWeightedGrade; }
    public String getLetterGrade() { return letterGrade; }
}