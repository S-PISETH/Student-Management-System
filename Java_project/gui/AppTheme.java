package gui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;

public final class AppTheme {
    public static final Color BACKGROUND = new Color(232, 245, 255);
    public static final Color SIDEBAR = new Color(15, 118, 255);
    public static final Color SIDEBAR_ACTIVE = new Color(10, 90, 220);
    public static final Color PRIMARY = new Color(56, 163, 224);
    public static final Color PRIMARY_DARK = new Color(35, 120, 188);
    public static final Color WARNING = new Color(245, 158, 11);
    public static final Color CARD = Color.WHITE;
    public static final Color TEXT = new Color(15, 23, 42);
    public static final Color MUTED = new Color(83, 100, 120);
    public static final Color BORDER = new Color(190, 215, 235);
    public static final Color SUCCESS = new Color(22, 163, 74);
    public static final Color DANGER = new Color(220, 38, 38);
    public static final Color FIELD = new Color(242, 248, 255);

    private AppTheme() {
    }

    public static void apply() {
        Font baseFont = new Font("Segoe UI", Font.PLAIN, 13);
        UIManager.put("Label.font", baseFont);
        UIManager.put("Button.font", baseFont);
        UIManager.put("TextField.font", baseFont);
        UIManager.put("ComboBox.font", baseFont);
        UIManager.put("Table.font", baseFont);
        UIManager.put("TableHeader.font", baseFont.deriveFont(Font.BOLD));
        UIManager.put("OptionPane.messageFont", baseFont);
        UIManager.put("OptionPane.buttonFont", baseFont);
        UIManager.put("Panel.background", BACKGROUND);
    }

    public static JPanel cardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD);
        panel.setBorder(compoundBorder(16));
        return panel;
    }

    public static JPanel accentCard(Color accent) {
        JPanel panel = cardPanel();
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        BorderFactory.createEmptyBorder(16, 16, 16, 16)
                )
        ));
        return panel;
    }

    public static JLabel title(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        return label;
    }

    public static JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        return label;
    }

    public static JButton primaryButton(String text) {
        return button(text, PRIMARY, Color.WHITE);
    }

    public static JButton neutralButton(String text) {
        return button(text, new Color(236, 241, 245), TEXT);
    }

    public static JButton dangerButton(String text) {
        return button(text, DANGER, Color.WHITE);
    }

    public static JButton button(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(darker(background)),
                BorderFactory.createEmptyBorder(9, 14, 9, 14)
        ));
        button.setOpaque(true);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent event) {
                if (Boolean.TRUE.equals(button.getClientProperty("active"))) {
                    button.setBackground(darker(SIDEBAR_ACTIVE));
                    return;
                }
                button.setBackground(darker(background));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent event) {
                if (Boolean.TRUE.equals(button.getClientProperty("active"))) {
                    button.setBackground(SIDEBAR_ACTIVE);
                    return;
                }
                button.setBackground(background);
            }
        });
        return button;
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(34);
        table.setShowGrid(false);
        table.setIntercellSpacing(new java.awt.Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(204, 251, 241));
        table.setSelectionForeground(TEXT);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBackground(new Color(232, 240, 244));
        table.getTableHeader().setForeground(TEXT);
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    component.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                    component.setForeground(TEXT);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return component;
            }
        });
    }

    public static void styleTextField(JTextField field) {
        field.setBackground(FIELD);
        field.setForeground(TEXT);
        field.setCaretColor(PRIMARY);
        field.setBorder(fieldBorder());
        field.setPreferredSize(new Dimension(120, 34));
    }

    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setBackground(FIELD);
        comboBox.setForeground(TEXT);
        comboBox.setBorder(fieldBorder());
        comboBox.setPreferredSize(new Dimension(120, 34));
    }

    public static void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);
    }

    public static void styleFormControls(JPanel panel) {
        for (Component component : panel.getComponents()) {
            if (component instanceof JTextField textField) {
                styleTextField(textField);
            } else if (component instanceof JComboBox<?> comboBox) {
                styleComboBox(comboBox);
            } else if (component instanceof JScrollPane scrollPane) {
                styleScrollPane(scrollPane);
            } else if (component instanceof JPanel childPanel) {
                styleFormControls(childPanel);
            }
        }
    }

    public static void padded(JComponent component, int padding) {
        component.setBorder(compoundBorder(padding));
    }

    private static Border compoundBorder(int padding) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(padding, padding, padding, padding)
        );
    }

    private static Border fieldBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(7, 9, 7, 9)
        );
    }

    private static Color darker(Color color) {
        return new Color(
                Math.max(0, color.getRed() - 16),
                Math.max(0, color.getGreen() - 16),
                Math.max(0, color.getBlue() - 16)
        );
    }
}
