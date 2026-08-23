package gui;

import dao.TeacherDAO;
import model.Gender;
import model.Teacher;
import model.TeacherStatus;
import model.UserAccount;
import service.TeacherService;
import util.CambodiaLocations;

import javax.swing.BorderFactory;
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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class TeacherPanel extends JPanel {
    private final UserAccount currentUser;
    private final Runnable afterChange;
    private final TeacherDAO teacherDAO = new TeacherDAO();
    private final TeacherService teacherService = new TeacherService();
    private final TeacherTableModel tableModel = new TeacherTableModel();
    private final JTable teacherTable = new JTable(tableModel);
    private final TableRowSorter<TeacherTableModel> sorter = new TableRowSorter<>(tableModel);

    private final JTextField searchField = new JTextField();
    private final JComboBox<Object> statusFilter = new JComboBox<>();
    private final JComboBox<Object> departmentFilter = new JComboBox<>();
    private final JTextField codeField = new JTextField();
    private final JTextField firstNameField = new JTextField();
    private final JTextField lastNameField = new JTextField();
    private final JComboBox<Gender> genderBox = new JComboBox<>(Gender.values());
    private final JTextField dobField = new JTextField();
    private final JTextField phoneField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JComboBox<String> addressBox = new JComboBox<>(CambodiaLocations.PROVINCES_AND_CAPITAL);
    private final JTextField departmentField = new JTextField();
    private final JTextField qualificationField = new JTextField();
    private final JTextField hireDateField = new JTextField();
    private final JComboBox<TeacherStatus> statusBox = new JComboBox<>(TeacherStatus.values());
    private final JTextField salaryField = new JTextField();
    private final JTextField photoPathField = new JTextField();
    private final JLabel photoPreview = new JLabel("No Photo", JLabel.CENTER);

    private Teacher selectedTeacher;
    private final boolean canManageTeachers;

    public TeacherPanel(UserAccount currentUser, Runnable afterChange) {
        this.currentUser = currentUser;
        this.afterChange = afterChange;
        this.canManageTeachers = currentUser.canManageRecords();
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
        titleWrap.add(AppTheme.title("Teacher Management"), BorderLayout.NORTH);
        JLabel subtitle = new JLabel(canManageTeachers
                ? "Create, update, search, filter, and manage teacher records"
                : "View your teacher profile");
        subtitle.setForeground(AppTheme.MUTED);
        titleWrap.add(subtitle, BorderLayout.CENTER);
        header.add(titleWrap, BorderLayout.WEST);

        JPanel tools = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        tools.setOpaque(false);
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
        formCard.add(AppTheme.sectionTitle("Teacher Form"), BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        int row = 0;
        codeField.setEditable(false);
        row = addField(fields, gbc, row, "Teacher ID", codeField);
        row = addField(fields, gbc, row, "First Name", firstNameField);
        row = addField(fields, gbc, row, "Last Name", lastNameField);
        row = addField(fields, gbc, row, "Gender", genderBox);
        row = addField(fields, gbc, row, "DOB yyyy-mm-dd", dobField);
        row = addField(fields, gbc, row, "Phone", phoneField);
        row = addField(fields, gbc, row, "Email", emailField);
        row = addField(fields, gbc, row, "Province / Capital", addressBox);
        row = addField(fields, gbc, row, "Department", departmentField);
        row = addField(fields, gbc, row, "Qualification", qualificationField);
        row = addField(fields, gbc, row, "Hire Date yyyy-mm-dd", hireDateField);
        row = addField(fields, gbc, row, "Status", statusBox);
        row = addField(fields, gbc, row, "Salary", salaryField);

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
        refreshButton.addActionListener(event -> loadTeachers());
        if (canManageTeachers) {
            JButton addButton = AppTheme.primaryButton("Add");
            addButton.addActionListener(event -> addTeacher());
            JButton updateButton = AppTheme.neutralButton("Update");
            updateButton.addActionListener(event -> updateTeacher());
            JButton deleteButton = AppTheme.dangerButton("Delete");
            deleteButton.addActionListener(event -> deleteTeacher());
            JButton clearButton = AppTheme.neutralButton("Clear");
            clearButton.addActionListener(event -> clearForm());
            JButton suspendButton = AppTheme.neutralButton("Suspend");
            suspendButton.addActionListener(event -> changeStatus("suspend"));
            JButton activateButton = AppTheme.neutralButton("Activate");
            activateButton.addActionListener(event -> changeStatus("activate"));
            JButton codeButton = AppTheme.neutralButton("New ID");
            codeButton.addActionListener(event -> generateTeacherCode());
            actions.add(addButton);
            actions.add(updateButton);
            actions.add(deleteButton);
            actions.add(clearButton);
            actions.add(suspendButton);
            actions.add(activateButton);
            actions.add(codeButton);
        }
        actions.add(refreshButton);
        formCard.add(actions, BorderLayout.SOUTH);

        AppTheme.styleFormControls(fields);
        if (!canManageTeachers) {
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
        top.add(AppTheme.sectionTitle("Teacher Records"), BorderLayout.NORTH);

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
        departmentFilter.setPreferredSize(new Dimension(190, 30));
        filters.add(departmentFilter, gbc);

        JButton refreshButton = AppTheme.neutralButton("Refresh");
        refreshButton.addActionListener(event -> loadTeachers());
        gbc.gridx = 3;
        filters.add(refreshButton, gbc);

        searchField.putClientProperty("JTextField.placeholderText", "Search teachers");
        searchField.getDocument().addDocumentListener((SimpleDocumentListener) event -> applyLocalFilter());
        statusFilter.addActionListener(event -> loadTeachers());
        departmentFilter.addActionListener(event -> loadTeachers());
        statusFilter.setEnabled(canManageTeachers);
        departmentFilter.setEnabled(canManageTeachers);
        AppTheme.styleFormControls(filters);
        top.add(filters, BorderLayout.CENTER);
        tableCard.add(top, BorderLayout.NORTH);

        teacherTable.setAutoCreateRowSorter(false);
        teacherTable.setRowSorter(sorter);
        teacherTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        AppTheme.styleTable(teacherTable);
        teacherTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                fillFormFromSelection();
            }
        });
        JScrollPane scrollPane = new JScrollPane(teacherTable);
        AppTheme.styleScrollPane(scrollPane);
        tableCard.add(scrollPane, BorderLayout.CENTER);
        return tableCard;
    }

    private void loadInitialData() {
        setupFilters();
        if (canManageTeachers) {
            generateTeacherCode();
        }
        loadTeachers();
    }

    private void setupFilters() {
        statusFilter.removeAllItems();
        statusFilter.addItem("All Statuses");
        for (TeacherStatus status : TeacherStatus.values()) {
            statusFilter.addItem(status);
        }

        departmentFilter.removeAllItems();
        departmentFilter.addItem("All Departments");
        try {
            for (String department : teacherDAO.countByDepartment().keySet()) {
                departmentFilter.addItem(department);
            }
        } catch (SQLException exception) {
            showError("Could not load filters: " + exception.getMessage());
        }
    }

    private void loadTeachers() {
        try {
            TeacherStatus status = statusFilter.getSelectedItem() instanceof TeacherStatus selectedStatus
                    ? selectedStatus : null;
            String department = departmentFilter.getSelectedItem() instanceof String selectedDept
                    && !"All Departments".equals(selectedDept) ? (String) selectedDept : null;
            if (currentUser.isTeacher()) {
                Teacher teacher = teacherDAO.getTeacherByUserId(currentUser.getUserId());
                tableModel.setTeachers(teacher == null ? List.of() : List.of(teacher));
            } else {
                tableModel.setTeachers(teacherDAO.searchTeachers(searchField.getText(), status, department));
            }
            applyLocalFilter();
            resizeColumns();
        } catch (SQLException exception) {
            showError("Could not load teachers: " + exception.getMessage());
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
        int[] widths = {80, 160, 80, 100, 120, 190, 140, 140, 100, 100, 80};
        for (int index = 0; index < widths.length && index < teacherTable.getColumnModel().getColumnCount(); index++) {
            teacherTable.getColumnModel().getColumn(index).setPreferredWidth(widths[index]);
        }
    }

    private void generateTeacherCode() {
        try {
            codeField.setText(teacherDAO.generateTeacherCode());
        } catch (SQLException exception) {
            codeField.setText("TC001");
        }
    }

    private void fillFormFromSelection() {
        int selectedRow = teacherTable.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }

        int modelRow = teacherTable.convertRowIndexToModel(selectedRow);
        selectedTeacher = tableModel.getTeacherAt(modelRow);
        if (selectedTeacher == null) {
            return;
        }

        codeField.setText(selectedTeacher.getTeacherCode());
        firstNameField.setText(selectedTeacher.getFirstName());
        lastNameField.setText(selectedTeacher.getLastName());
        genderBox.setSelectedItem(selectedTeacher.getGender());
        dobField.setText(selectedTeacher.getDateOfBirth().toString());
        phoneField.setText(selectedTeacher.getPhone());
        emailField.setText(selectedTeacher.getEmail());
        selectAddress(selectedTeacher.getAddress());
        departmentField.setText(selectedTeacher.getDepartment());
        qualificationField.setText(selectedTeacher.getQualification());
        hireDateField.setText(selectedTeacher.getHireDate().toString());
        statusBox.setSelectedItem(selectedTeacher.getStatus());
        salaryField.setText(String.valueOf(selectedTeacher.getSalary()));
        photoPathField.setText(selectedTeacher.getPhotoPath());
        updatePhotoPreview(selectedTeacher.getPhotoPath());
    }

    private Teacher readTeacherFromForm() {
        Teacher teacher = new Teacher();
        if (selectedTeacher != null) {
            teacher.setTeacherId(selectedTeacher.getTeacherId());
            teacher.setUserAccount(selectedTeacher.getUserAccount());
        }
        teacher.setTeacherCode(codeField.getText().trim());
        teacher.setFirstName(firstNameField.getText().trim());
        teacher.setLastName(lastNameField.getText().trim());
        teacher.setGender((Gender) genderBox.getSelectedItem());
        teacher.setDateOfBirth(LocalDate.parse(dobField.getText().trim()));
        teacher.setPhone(phoneField.getText().trim());
        teacher.setEmail(emailField.getText().trim());
        teacher.setAddress(String.valueOf(addressBox.getSelectedItem()).trim());
        teacher.setDepartment(departmentField.getText().trim());
        teacher.setQualification(qualificationField.getText().trim());
        teacher.setHireDate(LocalDate.parse(hireDateField.getText().trim()));
        teacher.setStatus((TeacherStatus) statusBox.getSelectedItem());
        teacher.setSalary(Double.parseDouble(salaryField.getText().trim()));
        teacher.setPhotoPath(photoPathField.getText().trim());
        return teacher;
    }

    private void addTeacher() {
        if (!canManageTeachers) {
            showInfo("You do not have permission to add teachers.");
            return;
        }
        try {
            selectedTeacher = null;
            Teacher teacher = readTeacherFromForm();
            teacherService.registerTeacher(teacher, currentUser);
            afterSuccessfulChange("Teacher added successfully.");
        } catch (Exception exception) {
            showError(exception.getMessage());
        }
    }

    private void updateTeacher() {
        if (!canManageTeachers) {
            showInfo("You do not have permission to update teachers.");
            return;
        }
        if (selectedTeacher == null) {
            showInfo("Select a teacher to update.");
            return;
        }

        try {
            Teacher teacher = readTeacherFromForm();
            teacherService.updateTeacher(teacher, currentUser);
            afterSuccessfulChange("Teacher updated successfully.");
        } catch (Exception exception) {
            showError(exception.getMessage());
        }
    }

    private void deleteTeacher() {
        if (!canManageTeachers) {
            showInfo("You do not have permission to delete teachers.");
            return;
        }
        if (selectedTeacher == null) {
            showInfo("Select a teacher to delete.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
                "Delete " + selectedTeacher.getFullName() + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            teacherService.deleteTeacher(selectedTeacher.getTeacherId(), selectedTeacher.getTeacherCode(), currentUser);
            afterSuccessfulChange("Teacher deleted successfully.");
        } catch (SQLException exception) {
            showError(exception.getMessage());
        }
    }

    private void changeStatus(String action) {
        if (!canManageTeachers) {
            showInfo("You do not have permission to change teacher status.");
            return;
        }
        if (selectedTeacher == null) {
            showInfo("Select a teacher first.");
            return;
        }

        try {
            switch (action) {
                case "suspend" -> teacherService.suspendTeacher(selectedTeacher, currentUser);
                case "activate" -> teacherService.activateTeacher(selectedTeacher, currentUser);
                default -> throw new IllegalArgumentException("Unknown status action.");
            }
            afterSuccessfulChange("Teacher status updated.");
        } catch (SQLException exception) {
            showError(exception.getMessage());
        }
    }

    private void choosePhoto() {
        if (!canManageTeachers) {
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

        ImageIcon icon = new ImageIcon(path);
        Image scaledImage = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
        photoPreview.setText("");
        photoPreview.setIcon(new ImageIcon(scaledImage));
    }

    private void clearForm() {
        selectedTeacher = null;
        if (canManageTeachers) {
            generateTeacherCode();
        } else {
            codeField.setText("");
        }
        firstNameField.setText("");
        lastNameField.setText("");
        genderBox.setSelectedIndex(0);
        dobField.setText("");
        phoneField.setText("");
        emailField.setText("");
        addressBox.setSelectedIndex(0);
        departmentField.setText("");
        qualificationField.setText("");
        hireDateField.setText("");
        statusBox.setSelectedItem(TeacherStatus.ACTIVE);
        salaryField.setText("");
        photoPathField.setText("");
        updatePhotoPreview(null);
        teacherTable.clearSelection();
    }

    private void afterSuccessfulChange(String message) {
        loadTeachers();
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
        JOptionPane.showMessageDialog(this, message, "Teacher Management", JOptionPane.INFORMATION_MESSAGE);
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
