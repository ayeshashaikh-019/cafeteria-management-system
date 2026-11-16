package com.cafeteria.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;

public final class Ui {
    public static final Color NAVY = new Color(25, 39, 59);
    public static final Color NAVY_LIGHT = new Color(36, 54, 78);
    public static final Color TEAL = new Color(24, 145, 124);
    public static final Color TEAL_DARK = new Color(18, 116, 101);
    public static final Color ORANGE = new Color(238, 153, 79);
    public static final Color BG = new Color(246, 248, 251);
    public static final Color SURFACE = Color.WHITE;
    public static final Color TEXT = new Color(36, 48, 64);
    public static final Color MUTED = new Color(122, 134, 151);
    public static final Color LINE = new Color(229, 234, 240);
    public static final Color GREEN = new Color(35, 145, 105);
    public static final Color RED = new Color(202, 76, 76);

    private Ui() { }

    public static JPanel page() {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setBackground(BG);
        panel.setBorder(new EmptyBorder(24, 28, 28, 28));
        return panel;
    }

    public static JPanel card() {
        JPanel panel = new JPanel();
        panel.setBackground(SURFACE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINE, 1, true), new EmptyBorder(18, 18, 18, 18)));
        return panel;
    }

    public static JLabel title(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 25));
        label.setForeground(TEXT);
        return label;
    }

    public static JLabel subtitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label.setForeground(MUTED);
        return label;
    }

    public static JButton primaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(TEAL);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 16, 10, 16));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.putClientProperty("JButton.buttonType", "roundRect");
        return button;
    }

    public static JButton secondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setForeground(TEXT);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(LINE), new EmptyBorder(8, 12, 8, 12)));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static JButton dangerButton(String text) {
        JButton button = secondaryButton(text);
        button.setForeground(RED);
        return button;
    }

    public static void styleTable(JTable table) {
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setForeground(TEXT);
        table.setBackground(Color.WHITE);
        table.setRowHeight(38);
        table.setSelectionBackground(new Color(226, 244, 240));
        table.setSelectionForeground(TEXT);
        table.setGridColor(LINE);
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new Dimension(0, 1));
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 12));
        header.setForeground(MUTED);
        header.setBackground(new Color(249, 250, 252));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 38));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, LINE));
    }

    public static JScrollPane scroll(JTable table) {
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(LINE));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    public static JTextField textField(String placeholder, int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setToolTipText(placeholder);
        field.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(LINE), new EmptyBorder(8, 10, 8, 10)));
        return field;
    }

    public static void showError(Component parent, Exception ex) {
        String message = ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
        JOptionPane.showMessageDialog(parent, message, "Something went wrong", JOptionPane.ERROR_MESSAGE);
    }
}
