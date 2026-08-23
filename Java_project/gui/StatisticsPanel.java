package gui;

import dao.StudentDAO;
import model.Gender;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.Map;

public class StatisticsPanel extends JPanel {
    private final StudentDAO studentDAO = new StudentDAO();

    public StatisticsPanel() {
        setLayout(new BorderLayout(16, 16));
        setBackground(AppTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        buildLayout();
    }

    private void buildLayout() {
        JPanel header = new JPanel(new BorderLayout(0, 4));
        header.setOpaque(false);
        header.add(AppTheme.title("Statistics"), BorderLayout.NORTH);
        JLabel subtitle = new JLabel("Quick summaries by gender and major");
        subtitle.setForeground(AppTheme.MUTED);
        header.add(subtitle, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(1, 2, 16, 16));
        content.setOpaque(false);
        content.add(createGenderPanel());
        content.add(createMajorPanel());
        add(content, BorderLayout.CENTER);
    }

    private JPanel createGenderPanel() {
        JPanel panel = AppTheme.accentCard(AppTheme.PRIMARY);
        panel.setLayout(new BorderLayout(8, 8));
        panel.add(AppTheme.sectionTitle("Students by Gender"), BorderLayout.NORTH);

        JPanel rows = new JPanel(new GridLayout(0, 1, 8, 8));
        rows.setOpaque(false);
        try {
            Map<Gender, Integer> counts = studentDAO.countByGender();
            for (Gender gender : Gender.values()) {
                rows.add(statRow(gender.getDisplayName(), counts.getOrDefault(gender, 0), AppTheme.SIDEBAR_ACTIVE));
            }
        } catch (SQLException exception) {
            rows.add(new JLabel(exception.getMessage()));
        }

        panel.add(rows, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMajorPanel() {
        JPanel panel = AppTheme.accentCard(AppTheme.WARNING);
        panel.setLayout(new BorderLayout(8, 8));
        panel.add(AppTheme.sectionTitle("Students by Major"), BorderLayout.NORTH);

        JPanel rows = new JPanel(new GridLayout(0, 1, 8, 8));
        rows.setOpaque(false);
        try {
            Map<String, Integer> counts = studentDAO.countByMajor();
            if (counts.isEmpty()) {
                rows.add(new JLabel("No student data yet."));
            }
            for (Map.Entry<String, Integer> entry : counts.entrySet()) {
                rows.add(statRow(entry.getKey(), entry.getValue(), AppTheme.SUCCESS));
            }
        } catch (SQLException exception) {
            rows.add(new JLabel(exception.getMessage()));
        }

        JScrollPane scrollPane = new JScrollPane(rows);
        AppTheme.styleScrollPane(scrollPane);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel statRow(String label, int value, Color accent) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDER),
                BorderFactory.createEmptyBorder(10, 4, 10, 4)
        ));

        JLabel labelView = new JLabel(label);
        labelView.setForeground(AppTheme.TEXT);
        row.add(labelView, BorderLayout.WEST);

        JLabel valueView = new JLabel(String.valueOf(value));
        valueView.setForeground(accent);
        valueView.setFont(new Font("Segoe UI", Font.BOLD, 18));
        row.add(valueView, BorderLayout.EAST);
        return row;
    }
}
