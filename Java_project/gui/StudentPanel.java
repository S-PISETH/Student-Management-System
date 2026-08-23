package gui;

import dao.MajorDAO;
import dao.StudentDAO;
import model.Gender;
import model.GraduateStudent;
import model.Major;
import model.Student;
import model.StudentStatus;
import model.UndergraduateStudent;
import model.UserAccount;
import service.StudentService;
import util.CambodiaLocations;
import util.CSVExporter;
import util.CSVImporter;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
// import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.io.File;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StudentPanel extends JPanel {
    private final UserAccount currentUser;
    private final Runnable afterChange;
    private final StudentDAO studentDAO = new StudentDAO();
    private final MajorDAO majorDAO = new MajorDAO();
    private final StudentService studentService = new StudentService();
    private final StudentTableModel tableModel = new StudentTableModel();
    private final JTable studentTable = new JTable(tableModel);
    private final TableRowSorter<StudentTableModel> sorter = new TableRowSorter<>(tableModel);

    private final JTextField searchField = new JTextField();
    private final JComboBox<Object> statusFilter = new JComboBox<>();
    private final JComboBox<Object> majorFilter = new JComboBox<>();
    private final JTextField codeField = new JTextField();
    private final JTextField firstNameField = new JTextField();
    private final JTextField lastNameField = new JTextField();
    private final JComboBox<Gender> genderBox = new JComboBox<>(Gender.values());
    private final JTextField dobField = new JTextField();
    private final JTextField phoneField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JComboBox<String> addressBox = new JComboBox<>(CambodiaLocations.PROVINCES_AND_CAPITAL);
    private final JComboBox<Major> majorBox = new JComboBox<>();
    private final JComboBox<String> typeBox = new JComboBox<>(new String[]{"UNDERGRADUATE", "GRADUATE"});
    private final JTextField yearLevelField = new JTextField();
    private final JComboBox<StudentStatus> statusBox = new JComboBox<>(StudentStatus.values());
    private final JTextField gpaField = new JTextField();
    private final JTextField photoPathField = new JTextField();
    private final JLabel photoPreview = new JLabel("No Photo", JLabel.CENTER);

    private Student selectedStudent;
    private final boolean canManageStudents;

    public StudentPanel(UserAccount currentUser, Runnable afterChange) {
        this.currentUser = currentUser;
        this.afterChange = afterChange;
        this.canManageStudents = currentUser.canManageRecords();
        setLayout(new BorderLayout(16, 16));
        setBackground(AppTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        buildLayout();
        loadInitialData();
    }

    private void buildLayout() {
        JPanel header = new JPanel(new BorderLayout(12, 12));
        header.setOpaque(false);

        JPanel titleWrap = new JPanel(new BorderLayout(0, 4));
        titleWrap.setOpaque(false);
        titleWrap.add(AppTheme.title("Student Management"), BorderLayout.NORTH);
        JLabel subtitle = new JLabel(canManageStudents
                ? "Create, update, search, filter, import, and export student records"
                : "View permitted student information");
        subtitle.setForeground(AppTheme.MUTED);
        titleWrap.add(subtitle, BorderLayout.CENTER);
        header.add(titleWrap, BorderLayout.WEST);

        JPanel tools = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        tools.setOpaque(false);
        JButton importButton = AppTheme.neutralButton("Import CSV");
        importButton.addActionListener(event -> importCsv());
        JButton exportButton = AppTheme.neutralButton("Export CSV");
        exportButton.addActionListener(event -> exportCsv());
        if (canManageStudents) {
            tools.add(importButton);
            tools.add(exportButton);
        }
        header.add(tools, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JPanel main = new JPanel(new BorderLayout(16, 16));
        main.setOpaque(false);
        main.add(createFormPanel(), BorderLayout.WEST);
        main.add(createTablePanel(), BorderLayout.CENTER);
        add(main, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel formCard = AppTheme.cardPanel();
        formCard.setPreferredSize(new Dimension(360, 0));
        formCard.setLayout(new BorderLayout(8, 8));
        formCard.add(AppTheme.sectionTitle("Student Form"), BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        int row = 0;
        codeField.setEditable(false);
        row = addField(fields, gbc, row, "Student ID", codeField);
        row = addField(fields, gbc, row, "Type", typeBox);
        row = addField(fields, gbc, row, "First Name", firstNameField);
        row = addField(fields, gbc, row, "Last Name", lastNameField);
        row = addField(fields, gbc, row, "Gender", genderBox);
        row = addField(fields, gbc, row, "DOB yyyy-mm-dd", dobField);
        row = addField(fields, gbc, row, "Phone", phoneField);
        row = addField(fields, gbc, row, "Email", emailField);
        row = addField(fields, gbc, row, "Major", majorBox);
        row = addField(fields, gbc, row, "Year Level", yearLevelField);
        row = addField(fields, gbc, row, "Status", statusBox);
        row = addField(fields, gbc, row, "GPA", gpaField);

        row = addField(fields, gbc, row, "Province / Capital", addressBox);

        JButton photoButton = AppTheme.neutralButton("Choose Photo");
        photoButton.addActionListener(event -> choosePhoto());
        row = addField(fields, gbc, row, "Photo", photoButton);

        photoPreview.setPreferredSize(new Dimension(120, 120));
        photoPreview.setOpaque(true);
        photoPreview.setBackground(AppTheme.FIELD);
        photoPreview.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        fields.add(photoPreview, gbc);

        formCard.add(new JScrollPane(fields), BorderLayout.CENTER);

        JPanel actions = new JPanel(new GridLayout(3, 3, 8, 8));
        actions.setOpaque(false);
        JButton refreshButton = AppTheme.neutralButton("Refresh");
        refreshButton.addActionListener(event -> loadStudents());
        if (canManageStudents) {
            JButton addButton = AppTheme.primaryButton("Add");
            addButton.addActionListener(event -> addStudent());
            JButton updateButton = AppTheme.neutralButton("Update");
            updateButton.addActionListener(event -> updateStudent());
            JButton deleteButton = AppTheme.dangerButton("Delete");
            deleteButton.addActionListener(event -> deleteStudent());
            JButton clearButton = AppTheme.neutralButton("Clear");
            clearButton.addActionListener(event -> clearForm());
            JButton graduateButton = AppTheme.neutralButton("Graduate");
            graduateButton.addActionListener(event -> changeStatus("graduate"));
            JButton suspendButton = AppTheme.neutralButton("Suspend");
            suspendButton.addActionListener(event -> changeStatus("suspend"));
            JButton activateButton = AppTheme.neutralButton("Activate");
            activateButton.addActionListener(event -> changeStatus("activate"));
            JButton idButton = AppTheme.neutralButton("New ID");
            idButton.addActionListener(event -> generateStudentCode());
            actions.add(addButton);
            actions.add(updateButton);
            actions.add(deleteButton);
            actions.add(clearButton);
            actions.add(graduateButton);
            actions.add(suspendButton);
            actions.add(activateButton);
            actions.add(idButton);
        }
        actions.add(refreshButton);
        formCard.add(actions, BorderLayout.SOUTH);

        AppTheme.styleFormControls(fields);
        if (!canManageStudents) {
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
        top.add(AppTheme.sectionTitle("Student Records"), BorderLayout.NORTH);

        JPanel filters = new JPanel(new GridBagLayout());
        filters.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        filters.add(searchField, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0;
        statusFilter.setPreferredSize(new Dimension(150, 30));
        filters.add(statusFilter, gbc);

        gbc.gridx = 2;
        majorFilter.setPreferredSize(new Dimension(190, 30));
        filters.add(majorFilter, gbc);

        JButton refreshButton = AppTheme.neutralButton("Refresh");
        refreshButton.addActionListener(event -> loadStudents());
        gbc.gridx = 3;
        filters.add(refreshButton, gbc);

        searchField.putClientProperty("JTextField.placeholderText", "Search students");
        searchField.getDocument().addDocumentListener((SimpleDocumentListener) event -> applyLocalFilter());
        statusFilter.addActionListener(event -> loadStudents());
        majorFilter.addActionListener(event -> loadStudents());
        statusFilter.setEnabled(canManageStudents);
        majorFilter.setEnabled(canManageStudents);
        AppTheme.styleFormControls(filters);
        top.add(filters, BorderLayout.CENTER);
        tableCard.add(top, BorderLayout.NORTH);

        studentTable.setAutoCreateRowSorter(false);
        studentTable.setRowSorter(sorter);
        studentTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        AppTheme.styleTable(studentTable);
        studentTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                fillFormFromSelection();
            }
        });
        JScrollPane scrollPane = new JScrollPane(studentTable);
        AppTheme.styleScrollPane(scrollPane);
        tableCard.add(scrollPane, BorderLayout.CENTER);
        return tableCard;
    }

    private void loadInitialData() {
        loadMajors();
        setupFilters();
        if (canManageStudents) {
            generateStudentCode();
        }
        loadStudents();
    }

    private void loadMajors() {
        try {
            List<Major> majors = majorDAO.getAllMajors();
            majorBox.setModel(new DefaultComboBoxModel<>(majors.toArray(new Major[0])));
        } catch (SQLException exception) {
            showError("Could not load majors: " + exception.getMessage());
        }
    }

    private void setupFilters() {
        statusFilter.removeAllItems();
        statusFilter.addItem("All Statuses");
        for (StudentStatus status : StudentStatus.values()) {
            statusFilter.addItem(status);
        }

        majorFilter.removeAllItems();
        majorFilter.addItem("All Majors");
        try {
            for (Major major : majorDAO.getAllMajors()) {
                majorFilter.addItem(major);
            }
        } catch (SQLException exception) {
            showError("Could not load filters: " + exception.getMessage());
        }
    }

    private void loadStudents() {
        try {
            StudentStatus status = statusFilter.getSelectedItem() instanceof StudentStatus selectedStatus
                    ? selectedStatus : null;
            Integer majorId = majorFilter.getSelectedItem() instanceof Major selectedMajor
                    ? selectedMajor.getMajorId() : null;
            List<Student> students;
            if (currentUser.isStudent()) {
                Student student = studentDAO.getStudentByUserId(currentUser.getUserId());
                students = student == null ? List.of() : List.of(student);
            } else if (currentUser.isTeacher()) {
                students = studentDAO.getStudentsForTeacherUser(currentUser.getUserId());
            } else {
                students = studentDAO.searchStudents(searchField.getText(), status, majorId);
            }
            tableModel.setStudents(students);
            applyLocalFilter();
            resizeColumns();
        } catch (SQLException exception) {
            showError("Could not load students: " + exception.getMessage());
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
        int[] widths = {80, 160, 120, 80, 100, 120, 190, 170, 60, 100, 60};
        for (int index = 0; index < widths.length && index < studentTable.getColumnModel().getColumnCount(); index++) {
            studentTable.getColumnModel().getColumn(index).setPreferredWidth(widths[index]);
        }
    }

    private void generateStudentCode() {
        try {
            codeField.setText(studentDAO.generateStudentCode());
        } catch (SQLException exception) {
            codeField.setText("ST001");
        }
    }

    private void fillFormFromSelection() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }

        int modelRow = studentTable.convertRowIndexToModel(selectedRow);
        selectedStudent = tableModel.getStudentAt(modelRow);
        if (selectedStudent == null) {
            return;
        }

        codeField.setText(selectedStudent.getStudentCode());
        typeBox.setSelectedItem(selectedStudent.getStudentType());
        firstNameField.setText(selectedStudent.getFirstName());
        lastNameField.setText(selectedStudent.getLastName());
        genderBox.setSelectedItem(selectedStudent.getGender());
        dobField.setText(selectedStudent.getDateOfBirth().toString());
        phoneField.setText(selectedStudent.getPhone());
        emailField.setText(selectedStudent.getEmail());
        selectAddress(selectedStudent.getAddress());
        majorBox.setSelectedItem(selectedStudent.getMajor());
        yearLevelField.setText(String.valueOf(selectedStudent.getYearLevel()));
        statusBox.setSelectedItem(selectedStudent.getStatus());
        gpaField.setText(String.format("%.2f", selectedStudent.getGpa()));
        photoPathField.setText(selectedStudent.getPhotoPath());
        updatePhotoPreview(selectedStudent.getPhotoPath());
    }

    private Student readStudentFromForm() {
        String type = (String) typeBox.getSelectedItem();
        Student student = "GRADUATE".equals(type) ? new GraduateStudent() : new UndergraduateStudent();
        if (selectedStudent != null) {
            student.setStudentId(selectedStudent.getStudentId());
            student.setUserAccount(selectedStudent.getUserAccount());
        }
        student.setStudentCode(codeField.getText().trim());
        student.setFirstName(firstNameField.getText().trim());
        student.setLastName(lastNameField.getText().trim());
        student.setGender((Gender) genderBox.getSelectedItem());
        student.setDateOfBirth(LocalDate.parse(dobField.getText().trim()));
        student.setPhone(phoneField.getText().trim());
        student.setEmail(emailField.getText().trim());
        student.setAddress(String.valueOf(addressBox.getSelectedItem()).trim());
        student.setMajor((Major) majorBox.getSelectedItem());
        student.setYearLevel(Integer.parseInt(yearLevelField.getText().trim()));
        student.setStatus((StudentStatus) statusBox.getSelectedItem());
        student.setGpa(Double.parseDouble(gpaField.getText().trim()));
        student.setPhotoPath(photoPathField.getText().trim());
        return student;
    }

    private void addStudent() {
        if (!canManageStudents) {
            showInfo("You do not have permission to add students.");
            return;
        }
        try {
            selectedStudent = null;
            Student student = readStudentFromForm();
            studentService.registerStudent(student, currentUser);
            afterSuccessfulChange("Student added successfully.");
        } catch (Exception exception) {
            showError(exception.getMessage());
        }
    }

    private void updateStudent() {
        if (!canManageStudents) {
            showInfo("You do not have permission to update students.");
            return;
        }
        if (selectedStudent == null) {
            showInfo("Select a student to update.");
            return;
        }

        try {
            Student student = readStudentFromForm();
            studentService.updateStudent(student, currentUser);
            afterSuccessfulChange("Student updated successfully.");
        } catch (Exception exception) {
            showError(exception.getMessage());
        }
    }

    private void deleteStudent() {
        if (!canManageStudents) {
            showInfo("You do not have permission to delete students.");
            return;
        }
        if (selectedStudent == null) {
            showInfo("Select a student to delete.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
                "Delete " + selectedStudent.getFullName() + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            studentService.deleteStudent(selectedStudent.getStudentId(), selectedStudent.getStudentCode(), currentUser);
            afterSuccessfulChange("Student deleted successfully.");
        } catch (SQLException exception) {
            showError(exception.getMessage());
        }
    }

    private void changeStatus(String action) {
        if (!canManageStudents) {
            showInfo("You do not have permission to change student status.");
            return;
        }
        if (selectedStudent == null) {
            showInfo("Select a student first.");
            return;
        }

        try {
            switch (action) {
                case "graduate" -> studentService.graduateStudent(selectedStudent, currentUser);
                case "suspend" -> studentService.suspendStudent(selectedStudent, currentUser);
                case "activate" -> studentService.activateStudent(selectedStudent, currentUser);
                default -> throw new IllegalArgumentException("Unknown status action.");
            }
            afterSuccessfulChange("Student status updated.");
        } catch (SQLException exception) {
            showError(exception.getMessage());
        }
    }

    private void choosePhoto() {
        if (!canManageStudents) {
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            photoPathField.setText(selectedFile.getAbsolutePath());
            updatePhotoPreview(selectedFile.getAbsolutePath());
        }
    }

    private void updatePhotoPreview(String path) {
        if (path == null || path.trim().isEmpty() || !new File(path).exists()) {
            photoPreview.setIcon(null);
            photoPreview.setText("No Photo");
            return;
        }

        try {
            ImageIcon icon = new ImageIcon(path);
            // Force image loading and clear cache
            java.awt.MediaTracker tracker = new java.awt.MediaTracker(photoPreview);
            tracker.addImage(icon.getImage(), 0);
            tracker.waitForAll();
            
            Image scaledImage = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            photoPreview.setText("");
            photoPreview.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            photoPreview.setIcon(null);
            photoPreview.setText("Error loading photo");
        }
    }

    private void importCsv() {
        if (!canManageStudents) {
            showInfo("You do not have permission to import students.");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            List<Major> majors = majorDAO.getAllMajors();
            List<Student> students = CSVImporter.importStudents(chooser.getSelectedFile().toPath(), majors);
            int imported = 0;
            List<String> errors = new ArrayList<>();
            for (Student student : students) {
                try {
                    if (student.getStudentCode() == null || student.getStudentCode().isBlank()) {
                        student.setStudentCode(studentDAO.generateStudentCode());
                    }
                    studentService.registerStudent(student, currentUser);
                    imported++;
                } catch (Exception exception) {
                    errors.add(student.getEmail() + ": " + exception.getMessage());
                }
            }
            loadStudents();
            if (afterChange != null) {
                afterChange.run();
            }
            String message = "Imported " + imported + " student(s).";
            if (!errors.isEmpty()) {
                message += System.lineSeparator() + String.join(System.lineSeparator(), errors);
            }
            showInfo(message);
        } catch (Exception exception) {
            showError(exception.getMessage());
        }
    }

    private void exportCsv() {
        if (!canManageStudents) {
            showInfo("You do not have permission to export students.");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
        chooser.setSelectedFile(new File("students.csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            Path path = chooser.getSelectedFile().toPath();
            CSVExporter.exportStudents(studentDAO.getAllStudents(), path);
            showInfo("Students exported to " + path);
        } catch (Exception exception) {
            showError(exception.getMessage());
        }
    }

    private void clearForm() {
        selectedStudent = null;
        if (canManageStudents) {
            generateStudentCode();
        } else {
            codeField.setText("");
        }
        typeBox.setSelectedIndex(0);
        firstNameField.setText("");
        lastNameField.setText("");
        genderBox.setSelectedIndex(0);
        dobField.setText("");
        phoneField.setText("");
        emailField.setText("");
        addressBox.setSelectedIndex(0);
        if (majorBox.getItemCount() > 0) {
            majorBox.setSelectedIndex(0);
        }
        yearLevelField.setText("");
        statusBox.setSelectedItem(StudentStatus.ACTIVE);
        gpaField.setText("");
        photoPathField.setText("");
        updatePhotoPreview(null);
        studentTable.clearSelection();
    }

    private void afterSuccessfulChange(String message) {
        loadStudents();
        clearForm();
        if (afterChange != null) {
            afterChange.run();
        }
        showInfo(message);
    }

    private void selectAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            addressBox.setSelectedIndex(0);
            return;
        }

        for (int index = 0; index < addressBox.getItemCount(); index++) {
            if (addressBox.getItemAt(index).equalsIgnoreCase(address.trim())) {
                addressBox.setSelectedIndex(index);
                return;
            }
        }

        addressBox.setSelectedIndex(0);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Student Management", JOptionPane.INFORMATION_MESSAGE);
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

        @Override
        default void insertUpdate(javax.swing.event.DocumentEvent event) {
            update(event);
        }

        @Override
        default void removeUpdate(javax.swing.event.DocumentEvent event) {
            update(event);
        }

        @Override
        default void changedUpdate(javax.swing.event.DocumentEvent event) {
            update(event);
        }
    }
}
