package gui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

public class AboutPanel extends JPanel {
    public AboutPanel() {
        setLayout(new BorderLayout(16, 16));
        setBackground(AppTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        buildLayout();
        
    }

    private void buildLayout() {
        JPanel header = new JPanel(new BorderLayout(0, 4));
        header.setOpaque(false);
        header.add(AppTheme.title("About"), BorderLayout.NORTH);
        JLabel subtitle = new JLabel("Final project overview and technical stack");
        subtitle.setForeground(AppTheme.MUTED);
        header.add(subtitle, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        JPanel card = AppTheme.accentCard(AppTheme.PRIMARY);
        card.setLayout(new BorderLayout(12, 12));

        JLabel heading = new JLabel("University Student Management System");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        heading.setForeground(AppTheme.TEXT);
        card.add(heading, BorderLayout.NORTH);

        JTextArea body = new JTextArea("""
                This final project demonstrates a complete Java Swing desktop application using JDBC and SQL Server.

                OOP concepts included:
                Encapsulation, abstraction, inheritance, polymorphism, interface usage, association, and composition.

                Architecture:
                MVC-style packages with model, DAO, service, utility, GUI, configuration, and main launcher classes.

                Features:
                Login, dashboard, student CRUD, search, filters, automatic student IDs, photo path storage,
                statistics, CSV import/export, activity logging, validation, and professional Swing layout.
                """);
        body.setEditable(false);
        body.setOpaque(false);
        body.setForeground(AppTheme.MUTED);
        body.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        body.setLineWrap(true);
        body.setWrapStyleWord(true);
        card.add(body, BorderLayout.CENTER);

        JPanel badges = new JPanel(new GridLayout(1, 4, 10, 10));
        badges.setOpaque(false);
        badges.add(badge("Java Swing", AppTheme.PRIMARY));
        badges.add(badge("SQL Server", AppTheme.WARNING));
        badges.add(badge("JDBC", AppTheme.SUCCESS));
        badges.add(badge("MVC + OOP", new Color(14, 165, 233)));
        card.add(badges, BorderLayout.SOUTH);

        add(card, BorderLayout.CENTER);
    }

    private JLabel badge(String text, Color color) {
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setForeground(color);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color),
                BorderFactory.createEmptyBorder(9, 8, 9, 8)
        ));
        return label;
    }
}
