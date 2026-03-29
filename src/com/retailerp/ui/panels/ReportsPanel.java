package com.retailerp.ui.panels;

import com.retailerp.service.ERPService;
import com.retailerp.util.EventBus;
import com.retailerp.util.Refreshable;
import com.retailerp.util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ReportsPanel extends JPanel implements Refreshable {
    private final ERPService service = new ERPService();

    private DefaultTableModel dailyModel;
    private DefaultTableModel topModel;
    private JTextField txtDays;
    private JTextField txtLimit;

    private JLabel lblToday;
    private JLabel lblMonthly;
    private JLabel lblTotal;

    public ReportsPanel() {
        setBackground(UIConstants.BG_DARK);
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        buildUI();

        EventBus.subscribe(EventBus.SALE_CHANGED, e -> refresh());

        refresh();
    }

    private void buildUI() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(UIConstants.PANEL_BG);
        tabs.setForeground(UIConstants.TEXT_PRIMARY);
        tabs.setFont(UIConstants.FONT_REGULAR);

        // ─── Tab 1: Daily Sales ───
        JPanel dailyPanel = new JPanel(new BorderLayout(10, 10));
        dailyPanel.setBackground(UIConstants.BG_DARK);
        dailyPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel dailyTopPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        dailyTopPanel.setOpaque(false);
        txtDays = UIConstants.createStyledTextField();
        txtDays.setPreferredSize(new Dimension(60, 30));
        txtDays.setText("30");
        JButton btnLoad = UIConstants.createStyledButton("Load", UIConstants.ACCENT_BLUE);
        dailyTopPanel.add(UIConstants.createLabel("Last N Days:"));
        dailyTopPanel.add(txtDays);
        dailyTopPanel.add(btnLoad);
        dailyPanel.add(dailyTopPanel, BorderLayout.NORTH);

        dailyModel = new DefaultTableModel(new String[] { "Date", "# Sales", "Revenue (₹)" }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable dailyTable = new JTable(dailyModel);
        dailyPanel.add(UIConstants.createStyledScrollPane(dailyTable), BorderLayout.CENTER);

        btnLoad.addActionListener(e -> fetchDailySales());

        tabs.addTab("Daily Sales", dailyPanel);

        // ─── Tab 2: Top Products ───
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(UIConstants.BG_DARK);
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel topCtrl = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topCtrl.setOpaque(false);
        txtLimit = UIConstants.createStyledTextField();
        txtLimit.setPreferredSize(new Dimension(60, 30));
        txtLimit.setText("10");
        JButton btnLoadTop = UIConstants.createStyledButton("Load", UIConstants.ACCENT_GREEN);
        topCtrl.add(UIConstants.createLabel("Top N:"));
        topCtrl.add(txtLimit);
        topCtrl.add(btnLoadTop);
        topPanel.add(topCtrl, BorderLayout.NORTH);

        topModel = new DefaultTableModel(new String[] { "Product", "Qty Sold", "Revenue (₹)" }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable topTable = new JTable(topModel);
        topPanel.add(UIConstants.createStyledScrollPane(topTable), BorderLayout.CENTER);

        btnLoadTop.addActionListener(e -> fetchTopProducts());

        tabs.addTab("Top Selling Products", topPanel);

        // ─── Tab 3: Revenue Summary ───
        JPanel revPanel = new JPanel();
        revPanel.setLayout(new BoxLayout(revPanel, BoxLayout.Y_AXIS));
        revPanel.setBackground(UIConstants.BG_DARK);
        revPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JButton btnRefresh = UIConstants.createStyledButton("Refresh", UIConstants.ACCENT_BLUE);
        btnRefresh.setAlignmentX(LEFT_ALIGNMENT);

        lblToday = createRevenueLabel("Today's Sales: Loading...");
        lblMonthly = createRevenueLabel("Monthly Sales: Loading...");
        lblTotal = createRevenueLabel("Total Sales: Loading...");

        btnRefresh.addActionListener(e -> refresh());

        revPanel.add(btnRefresh);
        revPanel.add(Box.createVerticalStrut(25));
        revPanel.add(lblToday);
        revPanel.add(Box.createVerticalStrut(15));
        revPanel.add(lblMonthly);
        revPanel.add(Box.createVerticalStrut(15));
        revPanel.add(lblTotal);

        tabs.addTab("Revenue Statistics", revPanel);

        add(tabs, BorderLayout.CENTER);
    }

    @Override
    public void refresh() {
        lblToday.setText(String.format("Today's Sales: ₹ %.2f", service.getTodaySales()));
        lblMonthly.setText(String.format("Monthly Sales: ₹ %.2f", service.getMonthlySales()));
        lblTotal.setText(String.format("Total Sales: ₹ %.2f", service.getTotalSales()));

        fetchDailySales();
        fetchTopProducts();
    }

    private void fetchDailySales() {
        int days;
        try {
            days = Integer.parseInt(txtDays.getText().trim());
        } catch (NumberFormatException ex) {
            days = 30;
        }
        dailyModel.setRowCount(0);
        List<Object[]> data = service.getDailySales(days);
        for (Object[] row : data) {
            dailyModel.addRow(new Object[] { row[0], row[1], String.format("%.2f", (double) row[2]) });
        }
    }

    private void fetchTopProducts() {
        int limit;
        try {
            limit = Integer.parseInt(txtLimit.getText().trim());
        } catch (NumberFormatException ex) {
            limit = 10;
        }
        topModel.setRowCount(0);
        List<Object[]> data = service.getTopSellingProducts(limit);
        for (Object[] row : data) {
            topModel.addRow(new Object[] { row[0], row[1], String.format("%.2f", (double) row[2]) });
        }
    }

    private JLabel createRevenueLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lbl.setForeground(UIConstants.ACCENT_GREEN);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
    }
}
