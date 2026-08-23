package gui;

import config.DatabaseConnection;
import model.UserAccount;
import service.AuthService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;

public class LoginForm extends JFrame {
    private final AuthService authService = new AuthService();
    private final JTextField usernameField = new JTextField("admin");
    private final JPasswordField passwordField = new JPasswordField("admin123");

    public LoginForm() {
        AppTheme.apply();
        setTitle("Student Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 560);
        setMinimumSize(new Dimension(900, 560));
        setLocationRelativeTo(null);
        buildLayout();
    }

    private void buildLayout() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(AppTheme.BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setContentPane(root);

        JPanel brandPanel = new JPanel(new GridBagLayout());
        brandPanel.setBackground(AppTheme.SIDEBAR);
        brandPanel.setPreferredSize(new Dimension(380, 560));
        brandPanel.setBorder(BorderFactory.createEmptyBorder(34, 34, 34, 34));

        JPanel brandContent = new JPanel(new GridBagLayout());
        brandContent.setOpaque(false);
        GridBagConstraints brandGbc = new GridBagConstraints();
        brandGbc.gridx = 0;
        brandGbc.anchor = GridBagConstraints.WEST;
        brandGbc.insets = new Insets(8, 0, 8, 0);

        JLabel brand = new JLabel("<html><div style='font-size:31px;color:white;font-weight:bold;'>University SMS</div>"
                + "<div style='font-size:13px;color:#d1d5db;'>Student records, statistics, CSV, and logs</div></html>");
        brandContent.add(brand, brandGbc);

        brandGbc.gridy = 1;
        brandGbc.fill = GridBagConstraints.HORIZONTAL;
        brandContent.add(sideNote("Java Swing", "Desktop GUI"), brandGbc);
        brandGbc.gridy = 2;
        brandContent.add(sideNote("JDBC + SQL Server", "PreparedStatement data access"), brandGbc);
        brandGbc.gridy = 3;
        brandContent.add(sideNote("OOP Project", "Inheritance, interface, composition"), brandGbc);
        brandPanel.add(brandContent);
        root.add(brandPanel, BorderLayout.WEST);

        JPanel formWrap = new JPanel(new GridBagLayout());
        formWrap.setBackground(AppTheme.BACKGROUND);
        root.add(formWrap, BorderLayout.CENTER);

        JPanel form = AppTheme.cardPanel();
        form.setLayout(new GridBagLayout());
        form.setPreferredSize(new Dimension(380, 330));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        form.add(AppTheme.title("Sign in"), gbc);

        addField(form, gbc, 1, "Username", usernameField);
        addField(form, gbc, 2, "Password", passwordField);

        JButton loginButton = AppTheme.primaryButton("Login");
        loginButton.addActionListener(event -> login());
        gbc.gridy = 3;
        gbc.weightx = 1;
        gbc.gridwidth = 2;
        form.add(loginButton, gbc);

        JLabel hint = new JLabel("Default account: admin / admin123");
        hint.setForeground(AppTheme.MUTED);
        hint.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        gbc.gridy = 4;
        form.add(hint, gbc);

        formWrap.add(form);
        getRootPane().setDefaultButton(loginButton);
    }

    private JPanel sideNote(String title, String detail) {
        JPanel panel = new JPanel(new BorderLayout(4, 2));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 3, 0, 0, AppTheme.WARNING),
                BorderFactory.createEmptyBorder(8, 12, 8, 8)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(titleLabel, BorderLayout.NORTH);

        JLabel detailLabel = new JLabel(detail);
        detailLabel.setForeground(new Color(209, 213, 219));
        panel.add(detailLabel, BorderLayout.CENTER);
        return panel;
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.weightx = 0;
        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setForeground(AppTheme.TEXT);
        panel.add(fieldLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        AppTheme.styleTextField(field);
        panel.add(field, gbc);
    }

    private void login() {
        if (!DatabaseConnection.testConnection()) {
            JOptionPane.showMessageDialog(this,
                    "Cannot connect to SQL Server. Check DatabaseConnection.java settings and run the SQL script.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            UserAccount user = authService.login(username, password);
            if (user == null) {
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Dashboard dashboard = new Dashboard(user);
            dashboard.setVisible(true);
            dispose();
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void showLogin() {
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}
