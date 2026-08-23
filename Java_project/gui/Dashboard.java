package gui;

import dao.ActivityLogDAO;
import dao.StudentDAO;
import dao.StudentScoreDAO;
import dao.TeacherDAO;
import model.Gender;
import model.Student;
import model.UserAccount;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class Dashboard extends JFrame {
    private final UserAccount currentUser;
    private final StudentDAO studentDAO = new StudentDAO();
    private final TeacherDAO teacherDAO = new TeacherDAO();
    private final StudentScoreDAO scoreDAO = new StudentScoreDAO();
    private final ActivityLogDAO activityLogDAO = new ActivityLogDAO();
    private final JPanel contentPanel = new JPanel(new CardLayout());
    private final JPanel sidebar = new JPanel();
    private JButton activeButton;

    public Dashboard(UserAccount currentUser) {
        this.currentUser = currentUser;
        AppTheme.apply();
        setTitle("Student Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setMinimumSize(new Dimension(1100, 650));
        setLocationRelativeTo(null);
        buildLayout();
    }

    private void buildLayout() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(AppTheme.BACKGROUND);
        setContentPane(root);

        buildSidebar();
        root.add(sidebar, BorderLayout.WEST);

        contentPanel.setBackground(AppTheme.BACKGROUND);
        contentPanel.add(createHomePanel(), "Dashboard");
        if (currentUser.canManageRecords() || currentUser.isTeacher() || currentUser.isStudent()) {
            contentPanel.add(new StudentPanel(currentUser, this::refreshHome), "Students");
        }
        if (currentUser.canManageRecords() || currentUser.isTeacher()) {
            contentPanel.add(new TeacherPanel(currentUser, this::refreshHome), "Teachers");
        }
        if (currentUser.canManageRecords() || currentUser.isTeacher() || currentUser.isStudent()) {
            contentPanel.add(new ScorePanel(currentUser), "Scores");
        }
        if (currentUser.canManageRecords()) {
            contentPanel.add(new StatisticsPanel(), "Statistics");
        }
        contentPanel.add(new AboutPanel(), "About");
        root.add(contentPanel, BorderLayout.CENTER);

        showPage("Dashboard");
    }

    private void buildSidebar() {
        sidebar.setPreferredSize(new Dimension(230, 700));
        sidebar.setBackground(AppTheme.SIDEBAR);
        sidebar.setLayout(new BorderLayout());

        JLabel title = new JLabel("<html><div style='color:white;font-size:18px;font-weight:bold;'>Student SMS</div>"
                + "<div style='color:#cbd5e1;font-size:11px;'>Welcome, " + currentUser.getFullName()
                + "</div></html>");
        title.setBorder(BorderFactory.createEmptyBorder(28, 20, 24, 20));
        sidebar.add(title, BorderLayout.NORTH);

        JPanel nav = new JPanel(new GridLayout(0, 1, 0, 8));
        nav.setOpaque(false);
        nav.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        nav.add(navButton("Dashboard"));
        if (currentUser.canManageRecords() || currentUser.isTeacher() || currentUser.isStudent()) {
            nav.add(navButton(currentUser.isStudent() ? "My Info" : "Students", "Students"));
        }
        if (currentUser.canManageRecords() || currentUser.isTeacher()) {
            nav.add(navButton(currentUser.isTeacher() ? "My Profile" : "Teachers", "Teachers"));
        }
        if (currentUser.canManageRecords() || currentUser.isTeacher() || currentUser.isStudent()) {
            nav.add(navButton(currentUser.isStudent() ? "My Results" : "Scores", "Scores"));
        }
        if (currentUser.canManageRecords()) {
            nav.add(navButton("Statistics"));
        }
        nav.add(navButton("About"));
        sidebar.add(nav, BorderLayout.CENTER);

        JButton logoutButton = AppTheme.button("Logout", new Color(61, 78, 83), Color.WHITE);
        logoutButton.addActionListener(event -> {
            new LoginForm().setVisible(true);
            dispose();
        });
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(14, 14, 20, 14));
        bottom.add(logoutButton);
        sidebar.add(bottom, BorderLayout.SOUTH);
    }

    private JButton navButton(String pageName) {
        return navButton(pageName, pageName);
    }

    private JButton navButton(String label, String pageName) {
        JButton button = AppTheme.button(pageName, AppTheme.SIDEBAR, new Color(235, 245, 245));
        button.setText(label);
        button.putClientProperty("pageName", pageName);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.addActionListener(event -> showPage(pageName));
        return button;
    }

    private void showPage(String pageName) {
        for (java.awt.Component component : ((JPanel) sidebar.getComponent(1)).getComponents()) {
            if (component instanceof JButton button) {
                button.putClientProperty("active", false);
                button.setBackground(AppTheme.SIDEBAR);
            }
        }

        for (java.awt.Component component : ((JPanel) sidebar.getComponent(1)).getComponents()) {
            if (component instanceof JButton button && pageName.equals(button.getClientProperty("pageName"))) {
                activeButton = button;
                activeButton.putClientProperty("active", true);
                activeButton.setBackground(AppTheme.SIDEBAR_ACTIVE);
                break;
            }
        }

        if ("Dashboard".equals(pageName)) {
            refreshHome();
        }
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, pageName);
    }

    private JPanel createHomePanel() {
        JPanel page = new JPanel(new BorderLayout(16, 16));
        page.setBackground(AppTheme.BACKGROUND);
        page.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        page.setName("home");

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = AppTheme.title("Dashboard");
        JLabel subtitle = new JLabel(currentUser.canManageRecords()
                ? "Overview of student records, activity, and university statistics"
                : "Your available academic information and actions");
        subtitle.setForeground(AppTheme.MUTED);

        JPanel titleWrap = new JPanel(new BorderLayout(0, 4));
        titleWrap.setOpaque(false);
        titleWrap.add(title, BorderLayout.NORTH);
        titleWrap.add(subtitle, BorderLayout.CENTER);
        header.add(titleWrap, BorderLayout.WEST);

        JLabel userLabel = new JLabel("Signed in as " + currentUser.getUsername());
        userLabel.setForeground(AppTheme.MUTED);
        header.add(userLabel, BorderLayout.EAST);
        page.add(header, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(16, 16));
        body.setOpaque(false);
        body.setName("homeBody");
        page.add(body, BorderLayout.CENTER);
        return page;
    }

    public void refreshHome() {
        JPanel page = (JPanel) contentPanel.getComponent(0);
        JPanel body = findNamedPanel(page, "homeBody");
        if (body == null) {
            return;
        }

        body.removeAll();
        body.add(createCardsPanel(), BorderLayout.NORTH);
        body.add(createActivityPanel(), BorderLayout.CENTER);
        body.revalidate();
        body.repaint();
    }

    private JPanel createCardsPanel() {
        JPanel cards = new JPanel(new GridLayout(1, 4, 16, 16));
        cards.setOpaque(false);

        try {
            if (currentUser.isStudent()) {
                Student student = studentDAO.getStudentByUserId(currentUser.getUserId());
                int scoreCount = student == null ? 0 : scoreDAO.countScoresByStudent(student.getStudentId());
                cards.add(metricCard("My Records", student == null ? "0" : "1", AppTheme.PRIMARY));
                cards.add(metricCard("My Scores", String.valueOf(scoreCount), AppTheme.SUCCESS));
                cards.add(metricCard("Role", currentUser.getRoleName(), AppTheme.WARNING));
                cards.add(metricCard("Access", "Read Only", AppTheme.MUTED));
                return cards;
            }

            if (currentUser.isTeacher()) {
                int students = studentDAO.getStudentsForTeacherUser(currentUser.getUserId()).size();
                int scores = scoreDAO.getScoresForTeacherUser(currentUser.getUserId()).size();
                cards.add(metricCard("My Students", String.valueOf(students), AppTheme.PRIMARY));
                cards.add(metricCard("Score Records", String.valueOf(scores), AppTheme.SUCCESS));
                cards.add(metricCard("Profile", teacherDAO.getTeacherByUserId(currentUser.getUserId()) == null ? "Missing" : "Linked", AppTheme.WARNING));
                cards.add(metricCard("Access", "Teacher", AppTheme.MUTED));
                return cards;
            }

            int total = studentDAO.countStudents();
            Map<Gender, Integer> genderCounts = new LinkedHashMap<>(studentDAO.countByGender());
            int male = genderCounts.getOrDefault(Gender.MALE, 0);
            int female = genderCounts.getOrDefault(Gender.FEMALE, 0);
            int other = genderCounts.getOrDefault(Gender.OTHER, 0);
            cards.add(metricCard("Total Students", String.valueOf(total), AppTheme.PRIMARY));
            cards.add(metricCard("Male", String.valueOf(male), new Color(14, 165, 233)));
            cards.add(metricCard("Female", String.valueOf(female), new Color(236, 72, 153)));
            cards.add(metricCard("Other", String.valueOf(other), AppTheme.SUCCESS));
        } catch (SQLException exception) {
            cards.add(metricCard("Database", "Unavailable", AppTheme.DANGER));
        }

        return cards;
    }

    private JPanel metricCard(String label, String value, Color accent) {
        JPanel card = AppTheme.accentCard(accent);
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(2, 4, 2, 4);

        JLabel labelView = new JLabel(label);
        labelView.setForeground(AppTheme.MUTED);
        card.add(labelView, gbc);

        gbc.gridy = 1;
        JLabel valueView = new JLabel(value);
        valueView.setForeground(accent);
        valueView.setFont(new Font("Segoe UI", Font.BOLD, 34));
        card.add(valueView, gbc);

        gbc.gridy = 2;
        JLabel footer = new JLabel("Live database count");
        footer.setForeground(new Color(148, 163, 184));
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        card.add(footer, gbc);
        return card;
    }

    private JPanel createActivityPanel() {
        JPanel panel = AppTheme.cardPanel();
        panel.setLayout(new BorderLayout(8, 8));
        panel.add(AppTheme.sectionTitle("Recent Activity"), BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Time", "User", "Action", "Entity", "ID", "Description"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try {
            for (String[] row : activityLogDAO.getRecentLogs(25)) {
                model.addRow(row);
            }
        } catch (SQLException exception) {
            model.addRow(new Object[]{"", "", "ERROR", "", "", exception.getMessage()});
        }

        JTable table = new JTable(model);
        AppTheme.styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        AppTheme.styleScrollPane(scrollPane);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel findNamedPanel(JPanel parent, String name) {
        for (java.awt.Component component : parent.getComponents()) {
            if (component instanceof JPanel panel) {
                if (name.equals(panel.getName())) {
                    return panel;
                }
                JPanel nested = findNamedPanel(panel, name);
                if (nested != null) {
                    return nested;
                }
            }
        }
        return null;
    }
}
