package gui;

import dao.StudentDAO;
import dao.StudentScoreDAO;
import dao.SubjectDAO;
import model.GradeSummary;
import model.Student;
import model.StudentScore;
import model.Subject;
import model.UserAccount;
import service.StudentScoreService;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.table.TableRowSorter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ScorePanel extends JPanel {
    private final UserAccount currentUser;
    private final StudentDAO studentDAO = new StudentDAO();
    private final StudentScoreDAO scoreDAO = new StudentScoreDAO();
    private final StudentScoreService scoreService = new StudentScoreService();
    private final ScoreTableModel tableModel = new ScoreTableModel();
    private final JTable scoreTable = new JTable(tableModel);
    private final TableRowSorter<ScoreTableModel> sorter = new TableRowSorter<>(tableModel);
    
    private final JLabel lblAttendance = new JLabel("Attendance (10%): N/A");
    private final JLabel lblCoursework = new JLabel("Coursework (30%): N/A");
    private final JLabel lblExam = new JLabel("Exam (60%): N/A");
    private final JLabel lblFinalGrade = new JLabel("Final Weighted Grade: N/A");
    private final JLabel lblLetterGrade = new JLabel("Letter Grade: N/A");
    private final JLabel lblResult = new JLabel("Result: N/A");

    private final JTextField searchField = new JTextField();
    private final JComboBox<Student> studentBox = new JComboBox<>();
    private final SubjectDAO subjectDAO = new SubjectDAO();
    private final JComboBox<Subject> subjectBox = new JComboBox<>();
    private final JTextField attendanceField = new JTextField();

    private final JTextField assignmentField = new JTextField();

    private final JTextField projectField = new JTextField();

    private final JTextField examField = new JTextField();

    private final JTextField participationField = new JTextField();
    private final JTextField remarksField = new JTextField();
    private final JTextField dateField = new JTextField();
    
    private StudentScore selectedScore;
    private boolean dataLoaded = false;
    private final boolean canEditScores;
    private final boolean canDeleteScores;
    private void enableEnterToNextField() {

    java.util.List<java.awt.Component> order = java.util.Arrays.asList(
            studentBox,
            subjectBox,
            attendanceField,
            assignmentField,
            projectField,
            examField,
            participationField,
            remarksField,
            dateField
    );

    for (int i = 0; i < order.size(); i++) {
        final int nextIndex = (i + 1) % order.size();
        java.awt.Component current = order.get(i);

        javax.swing.InputMap im = ((javax.swing.JComponent) current)
                .getInputMap(javax.swing.JComponent.WHEN_FOCUSED);
        javax.swing.ActionMap am = ((javax.swing.JComponent) current)
                .getActionMap();

        im.put(javax.swing.KeyStroke.getKeyStroke("ENTER"), "nextField");

        am.put("nextField", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                order.get(nextIndex).requestFocus();
            }
        });
    }
}
    public ScorePanel(UserAccount currentUser) {
        this.currentUser = currentUser;
        this.canEditScores = currentUser.canManageRecords() || currentUser.isTeacher();
        this.canDeleteScores = currentUser.canManageRecords();
        setLayout(new BorderLayout(16, 16));
        setBackground(AppTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        buildLayout();
        enableEnterToNextField();
        
        // Defer data loading until panel is shown
        addHierarchyListener(event -> {
            if ((event.getChangeFlags() & java.awt.event.HierarchyEvent.SHOWING_CHANGED) != 0 
                    && isShowing() && !dataLoaded) {
                dataLoaded = true;
                loadInitialData();
            }
        });
    }

    private void buildLayout() {
        JPanel header = new JPanel(new BorderLayout(12, 12));
        header.setOpaque(false);

        JPanel titleWrap = new JPanel(new BorderLayout(0, 4));
        titleWrap.setOpaque(false);
        titleWrap.add(AppTheme.title("Student Scoring"), BorderLayout.NORTH);
        JLabel subtitle = new JLabel(canEditScores
                ? "Record and manage student scores for attendance, assignments, exams, and more"
                : "View your score results");
        subtitle.setForeground(AppTheme.MUTED);
        titleWrap.add(subtitle, BorderLayout.CENTER);
        header.add(titleWrap, BorderLayout.WEST);

        add(header, BorderLayout.NORTH);

        JPanel main = new JPanel(new BorderLayout(16, 16));
        main.setOpaque(false);
        main.add(createFormPanel(), BorderLayout.WEST);
        main.add(createTablePanel(), BorderLayout.CENTER);
        
        // CREATE AND ADD THE SUMMARY PANEL TO THE BOTTOM (SOUTH)
        JPanel summaryPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Student Grade Summary"));
        summaryPanel.add(lblAttendance);
        summaryPanel.add(lblCoursework); 
        summaryPanel.add(lblExam);
        summaryPanel.add(lblFinalGrade);
        summaryPanel.add(lblLetterGrade);
        summaryPanel.add(lblResult);

        this.add(summaryPanel, BorderLayout.SOUTH);
        add(main, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel formCard = AppTheme.cardPanel();
        formCard.setPreferredSize(new Dimension(340, 0));
        formCard.setLayout(new BorderLayout(8, 8));
        formCard.add(AppTheme.sectionTitle("Score Form"), BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        int row = 0;
        row = addField(fields, gbc, row, "Student", studentBox);
        row = addField(fields, gbc, row, "Subject", subjectBox);
        row = addField(fields, gbc, row, "Attendance", attendanceField);
        row = addField(fields, gbc, row, "Assignment", assignmentField);
        row = addField(fields, gbc, row, "Project", projectField);
        row = addField(fields, gbc, row, "Exam", examField);
        row = addField(fields, gbc, row, "Participation", participationField);
        row = addField(fields, gbc, row, "Remarks", remarksField);
        row = addField(fields, gbc, row, "Date (yyyy-mm-dd)", dateField);

        formCard.add(new JScrollPane(fields), BorderLayout.CENTER);

        JPanel actions = new JPanel(new GridLayout(3, 3, 8, 8));
        actions.setOpaque(false);
        JButton refreshButton = AppTheme.neutralButton("Refresh");
        refreshButton.addActionListener(event -> loadScore());
        if (canEditScores) {
            JButton addButton = AppTheme.primaryButton("Record");
            addButton.addActionListener(event -> recordScore());
            JButton updateButton = AppTheme.neutralButton("Update");
            updateButton.addActionListener(event -> updateScore());
            JButton clearButton = AppTheme.neutralButton("Clear");
            clearButton.addActionListener(event -> clearForm());
            actions.add(addButton);
            actions.add(updateButton);
            if (canDeleteScores) {
                JButton deleteButton = AppTheme.dangerButton("Delete");
                deleteButton.addActionListener(event -> deleteScore());
                actions.add(deleteButton);
            }
            actions.add(clearButton);
        }
        actions.add(refreshButton);
        formCard.add(actions, BorderLayout.SOUTH);

        AppTheme.styleFormControls(fields);
        if (!canEditScores) {
            setEditable(fields, false);
        }

        return formCard;
    }

    private int addField(JPanel panel, GridBagConstraints gbc, int row, String label, java.awt.Component component) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setForeground(AppTheme.MUTED);
        panel.add(fieldLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(component, gbc);
        return row + 1;
    }

    private JPanel createTablePanel() {
        JPanel tableCard = AppTheme.cardPanel();
        tableCard.setLayout(new BorderLayout(8, 8));

        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.setOpaque(false);
        top.add(AppTheme.sectionTitle("Score Records"), BorderLayout.NORTH);

        JPanel filters = new JPanel(new GridBagLayout());
        filters.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        filters.add(searchField, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0;
        // typeFilter.setPreferredSize(new Dimension(150, 30));
        // filters.add(typeFilter, gbc);

        JButton refreshButton = AppTheme.neutralButton("Refresh");
        refreshButton.addActionListener(event -> loadScore());
        gbc.gridx = 2;
        filters.add(refreshButton, gbc);

        searchField.putClientProperty("JTextField.placeholderText", "Search by student or subject");
        searchField.getDocument().addDocumentListener((SimpleDocumentListener) event -> applyLocalFilter());
        AppTheme.styleFormControls(filters);
        top.add(filters, BorderLayout.CENTER);
        tableCard.add(top, BorderLayout.NORTH);

        scoreTable.setAutoCreateRowSorter(false);
        scoreTable.setRowSorter(sorter);
        scoreTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        AppTheme.styleTable(scoreTable);
        scoreTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                fillFormFromSelection();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(scoreTable);
        AppTheme.styleScrollPane(scrollPane);
        tableCard.add(scrollPane, BorderLayout.CENTER);
        return tableCard;
    }

    private void loadInitialData() {
        try {
            // setupFilters();
            initializeDateField();
            loadStudents();
            loadSubjectsForStudent();
            loadScore();
        } catch (Exception e) {
            showError("Failed to load initial data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadStudents() {
        try {
            List<Student> students;
            if (currentUser.isStudent()) {
                Student student = studentDAO.getStudentByUserId(currentUser.getUserId());
                students = student == null ? List.of() : List.of(student);
            } else if (currentUser.isTeacher()) {
                students = studentDAO.getStudentsForTeacherUser(currentUser.getUserId());
            } else {
                students = studentDAO.getAllStudents();
            }
            if (students.isEmpty()) {
                showError(currentUser.isStudent()
                        ? "No student profile is linked to this login."
                        : "No students found for this login.");
            }
            studentBox.setModel(new DefaultComboBoxModel<>(students.toArray(new Student[0])));
            studentBox.addActionListener(e -> loadSubjectsForStudent());
        } catch (SQLException exception) {
            showError("Could not load students: " + exception.getMessage());
            exception.printStackTrace();
        }
    }

    private void loadSubjectsForStudent() {
        Student student = (Student) studentBox.getSelectedItem();
        if (student == null) {
            subjectBox.removeAllItems();
            return;
        }
        try {
            int majorId = student.getMajor().getMajorId();
            List<Subject> subjects = subjectDAO.getSubjectsByMajor(majorId);
            DefaultComboBoxModel<Subject> model = new DefaultComboBoxModel<>();
            for (Subject s : subjects) {
                model.addElement(s);
            }
            subjectBox.setModel(model);
        } catch (SQLException ex) {
            showError("Could not load subjects : " + ex.getMessage());
        }
    }

    // private void setupFilters() {
    //     typeFilter.removeAllItems();
    //     typeFilter.addItem("All Types");
    //     typeFilter.addItem("ATTENDANCE");
    //     typeFilter.addItem("ASSIGNMENT");
    //     typeFilter.addItem("EXAM");
    //     typeFilter.addItem("PARTICIPATION");
    //     typeFilter.addItem("PROJECT");
    // }

    private void initializeDateField() {
        dateField.setText(LocalDate.now().toString());
    }

    // ==========================================
    // THE FIXED LOAD SCORE METHOD (Proper MVC)
    // ==========================================
    public void loadScore() {
        try {
            List<StudentScore> scores;
            if (currentUser.isStudent()) {
                scores = scoreDAO.getScoresByStudentUser(currentUser.getUserId());
            } else if (currentUser.isTeacher()) {
                scores = scoreDAO.getScoresForTeacherUser(currentUser.getUserId());
            } else {
                scores = scoreDAO.getAllScores();
            }
            // Give the data to your custom ScoreTableModel
            tableModel.setScores(scores);
            applyLocalFilter();

            SwingUtilities.invokeLater(() -> resizeColumns());
            
            resizeColumns();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load scores: " + e.getMessage());
        }
    }

    private void applyLocalFilter() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(keyword)));
        }
    }

    private void resizeColumns() {
    int[] widths = {
        80,   // Student ID
        150,  // Student Name
        150,  // Subject
        90,   // Attendance
        90,   // Assignment
        90,   // Project
        90,   // Exam
        100,  // Participation
        150,  // Remark
        120   // Date
    };

    int columnCount = scoreTable.getColumnModel().getColumnCount();

    for (int i = 0; i < Math.min(widths.length, columnCount); i++) {
        if (i < columnCount) {
            scoreTable.getColumnModel()
                    .getColumn(i)
                    .setPreferredWidth(widths[i]);
        }
    }
}

    private void fillFormFromSelection() {
        int selectedRow = scoreTable.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }

        int modelRow = scoreTable.convertRowIndexToModel(selectedRow);
        selectedScore = tableModel.getScoreAt(modelRow); // This works again because we kept ScoreTableModel!
        
        if (selectedScore == null) {
            return;
        }

        try {
            Student student = studentDAO.getStudent(selectedScore.getStudentId());
            if (student != null) {
                studentBox.setSelectedItem(student);
            }
        } catch (SQLException e) {
            // Student not found, continue
        }
        attendanceField.setText(String.valueOf(selectedScore.getAttendance()));

        assignmentField.setText(String.valueOf(selectedScore.getAssignment()));

        projectField.setText(String.valueOf(selectedScore.getProject()));

        examField.setText(String.valueOf(selectedScore.getExam()));

        participationField.setText(String.valueOf(selectedScore.getParticipation()));
        loadSubjectsForStudent();

        for (int i = 0; i < subjectBox.getItemCount(); i++) {
            Subject s = subjectBox.getItemAt(i);
            if (s.getSubjectName().equalsIgnoreCase(selectedScore.getSubject())) {
                subjectBox.setSelectedIndex(i);
                break;
            }
        }
        attendanceField.setText(String.valueOf(selectedScore.getAttendance()));
        assignmentField.setText(String.valueOf(selectedScore.getAssignment()));
        projectField.setText(String.valueOf(selectedScore.getProject()));
        examField.setText(String.valueOf(selectedScore.getExam()));
        participationField.setText(String.valueOf(selectedScore.getParticipation()));

        remarksField.setText(selectedScore.getRemarks());
        dateField.setText(selectedScore.getDateRecorded().toString());

        Subject subject = (Subject) subjectBox.getSelectedItem();

        if (subject != null) {
            refreshSummary(selectedScore.getStudentId(), subject.getSubjectId());
        }
        System.out.println("Row: " + selectedRow);
        System.out.println("Model size: " + tableModel.getRowCount());
        System.out.println("Column count: " + scoreTable.getColumnCount());
    }

    private StudentScore readScoreFromForm() {
        StudentScore score = new StudentScore();
        if (selectedScore != null) {
            score.setScoreId(selectedScore.getScoreId());
        }

        Student student = (Student) studentBox.getSelectedItem();
        score.setStudentId(student != null ? student.getStudentId() : -1);
        score.setTeacherId(currentUser.getUserId());
        
        Subject subject = (Subject) subjectBox.getSelectedItem();
        if (subject == null) {
            throw new IllegalArgumentException("Please select a subject.");
        }

        score.setSubject(subject.getSubjectName());
        score.setSubjectId(subject.getSubjectId());

        score.setAttendance(parseScoreField(attendanceField, "Attendance"));
        score.setAssignment(parseScoreField(assignmentField, "Assignment"));
        score.setProject(parseScoreField(projectField, "Project"));
        score.setExam(parseScoreField(examField, "Exam"));
        score.setParticipation(parseScoreField(participationField, "Participation"));
        
        double rawScore = (score.getAttendance() + score.getAssignment() + score.getProject() + score.getExam() + score.getParticipation()) / 5.0;
        
        String autoRemarks = rawScore >= 50 ? "PASS" : "FAIL";
        String input = remarksField.getText().trim();

        score.setRemarks(input.isBlank() ? autoRemarks : input);
        score.setDateRecorded(parseDateField(dateField));

        return score;
    }

    private double parseScoreField(JTextField field, String label) {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            throw new IllegalArgumentException(label + " is required.");
        }

        try {
            double value = Double.parseDouble(text);
            if (value < 0 || value > 100) {
                throw new IllegalArgumentException(label + " must be between 0 and 100.");
            }
            return value;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(label + " must be a valid number.");
        }
    }

    private LocalDate parseDateField(JTextField field) {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            throw new IllegalArgumentException("Date is required.");
        }

        try {
            LocalDate date = LocalDate.parse(text);
            if (date.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("Date cannot be in the future.");
            }
            return date;
        } catch (Exception e) {
            throw new IllegalArgumentException("Date must be in yyyy-mm-dd format.");
        }
    }

    private void recordScore() {
        if (!canEditScores) {
            showInfo("You do not have permission to record scores.");
            return;
        }
        try {
            selectedScore = null;
            StudentScore score = readScoreFromForm();
            scoreService.recordScore(score, currentUser);
            afterSuccessfulChange("Score recorded successfully.");
        } catch (Exception exception) {
            showError(exception.getMessage());
        }
    }

    private void updateScore() {
        if (!canEditScores) {
            showInfo("You do not have permission to update scores.");
            return;
        }
        if (selectedScore == null) {
            showInfo("Select a score record to update.");
            return;
        }

        try {
            StudentScore score = readScoreFromForm();
            scoreService.updateScore(score, currentUser);
            afterSuccessfulChange("Score updated successfully.");
        } catch (Exception exception) {
            showError(exception.getMessage());
        }
    }

    private void deleteScore() {
        if (!canDeleteScores) {
            showInfo("You do not have permission to delete scores.");
            return;
        }
        if (selectedScore == null) {
            showInfo("Select a score record to delete.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
                "Delete this score record?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            scoreService.deleteScore(selectedScore.getScoreId(), currentUser);
            afterSuccessfulChange("Score deleted successfully.");
        } catch (SQLException exception) {
            showError(exception.getMessage());
        }
    }

    private void clearForm() {
        selectedScore = null;
        if(studentBox.getItemCount() > 0) studentBox.setSelectedIndex(0);
        loadSubjectsForStudent();
        attendanceField.setText("");
        assignmentField.setText("");
        projectField.setText("");
        examField.setText("");
        participationField.setText("");
        remarksField.setText("");
        initializeDateField();
        scoreTable.clearSelection();
    }

    private void afterSuccessfulChange(String message) {
        loadScore();
        clearForm();
        showInfo(message);
        if (selectedScore != null) {
            Subject subject = (Subject) subjectBox.getSelectedItem();
            if (subject != null) {
                refreshSummary(selectedScore.getStudentId(), subject.getSubjectId());
            }
        }
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Score Management", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void setEditable(java.awt.Container container, boolean editable) {
        for (java.awt.Component component : container.getComponents()) {
            if (component instanceof JTextField textField) {
                textField.setEditable(editable);
            } else if (component instanceof JComboBox<?> comboBox) {
                comboBox.setEnabled(editable);
            } else if (component instanceof JButton button) {
                button.setEnabled(editable);
            } else if (component instanceof java.awt.Container child) {
                setEditable(child, editable);
            }
        }
    }

    @FunctionalInterface
    private interface SimpleDocumentListener extends javax.swing.event.DocumentListener {
        void update(javax.swing.event.DocumentEvent event);
        @Override default void insertUpdate(javax.swing.event.DocumentEvent event) { update(event); }
        @Override default void removeUpdate(javax.swing.event.DocumentEvent event) { update(event); }
        @Override default void changedUpdate(javax.swing.event.DocumentEvent event) { update(event); }
    }

    public void refreshSummary(int studentId, int subjectId) {
    List<StudentScore> scores = scoreDAO.getScoresByStudentAndSubject(studentId, subjectId);
    GradeSummary summary = scoreService.calculateSubjectGrade(scores);

    lblAttendance.setText(String.format("Attendance (10%%): %.2f", summary.getAttendanceAvg()));
    lblCoursework.setText(String.format("Coursework (30%%): %.2f", summary.getCourseworkAvg()));
    lblExam.setText(String.format("Exam (60%%): %.2f", summary.getExamScore()));
    lblFinalGrade.setText(String.format("Final Weighted Grade: %.2f", summary.getFinalWeightedGrade()));
    lblResult.setText(summary.getFinalWeightedGrade() >= 50 ? "Result: PASSED" : "Result: FAILED");
}
}
