package com.retailerp.util;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public class UIConstants {
    // Colors
    public static final Color BG_DARK       = new Color(0x1E1E2F);
    public static final Color PANEL_BG      = new Color(0x2A2A40);
    public static final Color SIDEBAR_BG    = new Color(0x16162A);
    public static final Color CARD_BG       = new Color(0x33334D);
    public static final Color ACCENT_BLUE   = new Color(0x4A90D9);
    public static final Color ACCENT_GREEN  = new Color(0x27AE60);
    public static final Color ACCENT_RED    = new Color(0xE74C3C);
    public static final Color ACCENT_ORANGE = new Color(0xF39C12);
    public static final Color TEXT_PRIMARY   = Color.WHITE;
    public static final Color TEXT_SECONDARY = new Color(0xB0B0C0);
    public static final Color FIELD_BG      = new Color(0x3A3A55);
    public static final Color TABLE_HEADER  = new Color(0x3D3D5C);
    public static final Color TABLE_ROW_ALT = new Color(0x2F2F48);
    public static final Color BORDER_COLOR  = new Color(0x44446A);

    // Fonts
    public static final Font FONT_TITLE     = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE  = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_REGULAR   = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL     = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BUTTON    = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SIDEBAR   = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_HEADER    = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_CARD_NUM  = new Font("Segoe UI", Font.BOLD, 28);

    // Styled button
    public static JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setBackground(bgColor);
        btn.setForeground(TEXT_PRIMARY);
        btn.setFont(FONT_BUTTON);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 36));
        btn.setOpaque(true);
        return btn;
    }

    // Styled text field
    public static JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setBackground(FIELD_BG);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(TEXT_PRIMARY);
        field.setFont(FONT_REGULAR);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            new EmptyBorder(5, 8, 5, 8)
        ));
        return field;
    }

    // Styled password field
    public static JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setBackground(FIELD_BG);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(TEXT_PRIMARY);
        field.setFont(FONT_REGULAR);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            new EmptyBorder(5, 8, 5, 8)
        ));
        return field;
    }

    // Styled label
    public static JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(TEXT_PRIMARY);
        lbl.setFont(FONT_REGULAR);
        return lbl;
    }

    // Styled combo box
    public static <T> JComboBox<T> createStyledComboBox() {
        JComboBox<T> combo = new JComboBox<>();
        combo.setBackground(FIELD_BG);
        combo.setForeground(TEXT_PRIMARY);
        combo.setFont(FONT_REGULAR);
        return combo;
    }

    // Style a JTable for the dark theme
    public static void styleTable(JTable table) {
        table.setBackground(PANEL_BG);
        table.setForeground(TEXT_PRIMARY);
        table.setGridColor(BORDER_COLOR);
        table.setFont(FONT_REGULAR);
        table.setRowHeight(30);
        table.setSelectionBackground(ACCENT_BLUE);
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));

        JTableHeader header = table.getTableHeader();
        header.setBackground(TABLE_HEADER);
        header.setForeground(TEXT_PRIMARY);
        header.setFont(FONT_SUBTITLE);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 36));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        renderer.setBackground(PANEL_BG);
        renderer.setForeground(TEXT_PRIMARY);
        table.setDefaultRenderer(Object.class, renderer);
    }

    // Styled scroll pane wrapper for tables
    public static JScrollPane createStyledScrollPane(JTable table) {
        styleTable(table);
        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(PANEL_BG);
        sp.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        return sp;
    }

    // Styled panel
    public static JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(PANEL_BG);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        return panel;
    }
}
