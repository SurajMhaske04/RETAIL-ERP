package com.retailerp.ui.panels;

import com.retailerp.service.ERPService;
import com.retailerp.util.EventBus;
import com.retailerp.util.Refreshable;
import com.retailerp.util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardPanel extends JPanel implements Refreshable {
    private final ERPService service = new ERPService();

    private JLabel lblTotalSales;
    private JLabel lblTodaySales;
    private JLabel lblProductCount;
    private JLabel lblCustomerCount;

    private JLabel lblMonthlyRevenueValue;
    private DefaultListModel<String> lowStockListModel;

    public DashboardPanel() {
        setBackground(UIConstants.BG_DARK);
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        buildUI();

        EventBus.subscribe(EventBus.SALE_CHANGED, e -> refresh());
        EventBus.subscribe(EventBus.INVENTORY_CHANGED, e -> refresh());
        EventBus.subscribe("CUSTOMER_CHANGED", e -> refresh());
        EventBus.subscribe("PRODUCT_CHANGED", e -> refresh());

        refresh(); // load initially
    }

    private void buildUI() {
        // ─── CARDS ROW ───
        JPanel cardsRow = new JPanel(new GridLayout(1, 4, 15, 0));
        cardsRow.setOpaque(false);

        lblTotalSales = new JLabel("₹ 0.00");
        lblTodaySales = new JLabel("₹ 0.00");
        lblProductCount = new JLabel("0");
        lblCustomerCount = new JLabel("0");

        cardsRow.add(createCard("Total Sales", lblTotalSales, UIConstants.ACCENT_BLUE, "💰"));
        cardsRow.add(createCard("Today's Sales", lblTodaySales, UIConstants.ACCENT_GREEN, "📅"));
        cardsRow.add(createCard("Products", lblProductCount, UIConstants.ACCENT_ORANGE, "📦"));
        cardsRow.add(createCard("Customers", lblCustomerCount, new Color(0x9B59B6), "👥"));

        add(cardsRow, BorderLayout.NORTH);

        // ─── LOWER SECTION ───
        JPanel lowerPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        lowerPanel.setOpaque(false);

        // Monthly sales card
        JPanel monthlySalesPanel = new JPanel();
        monthlySalesPanel.setLayout(new BoxLayout(monthlySalesPanel, BoxLayout.Y_AXIS));
        monthlySalesPanel.setBackground(UIConstants.PANEL_BG);
        monthlySalesPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Monthly Revenue");
        lblTitle.setFont(UIConstants.FONT_SUBTITLE);
        lblTitle.setForeground(UIConstants.TEXT_SECONDARY);
        lblTitle.setAlignmentX(LEFT_ALIGNMENT);
        monthlySalesPanel.add(lblTitle);
        monthlySalesPanel.add(Box.createVerticalStrut(10));

        lblMonthlyRevenueValue = new JLabel("₹ 0.00");
        lblMonthlyRevenueValue.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblMonthlyRevenueValue.setForeground(UIConstants.ACCENT_GREEN);
        lblMonthlyRevenueValue.setAlignmentX(LEFT_ALIGNMENT);
        monthlySalesPanel.add(lblMonthlyRevenueValue);
        monthlySalesPanel.add(Box.createVerticalStrut(8));

        JLabel lblSub = new JLabel("Current month aggregate sales revenue.");
        lblSub.setFont(UIConstants.FONT_SMALL);
        lblSub.setForeground(UIConstants.TEXT_SECONDARY);
        lblSub.setAlignmentX(LEFT_ALIGNMENT);
        monthlySalesPanel.add(lblSub);

        // Low stock alerts
        JPanel lowStockPanel = new JPanel(new BorderLayout(0, 10));
        lowStockPanel.setBackground(UIConstants.PANEL_BG);
        lowStockPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lowStockTitle = new JLabel("⚠  Low Stock Alerts");
        lowStockTitle.setFont(UIConstants.FONT_SUBTITLE);
        lowStockTitle.setForeground(UIConstants.ACCENT_ORANGE);
        lowStockPanel.add(lowStockTitle, BorderLayout.NORTH);

        lowStockListModel = new DefaultListModel<>();
        JList<String> lowStockList = new JList<>(lowStockListModel);
        lowStockList.setBackground(UIConstants.PANEL_BG);
        lowStockList.setForeground(UIConstants.TEXT_PRIMARY);
        lowStockList.setFont(UIConstants.FONT_REGULAR);
        JScrollPane sp = new JScrollPane(lowStockList);
        sp.setBorder(null);
        sp.getViewport().setBackground(UIConstants.PANEL_BG);
        lowStockPanel.add(sp, BorderLayout.CENTER);

        lowerPanel.add(monthlySalesPanel);
        lowerPanel.add(lowStockPanel);
        add(lowerPanel, BorderLayout.CENTER);
    }

    private JPanel createCard(String title, JLabel lblValue, Color accentColor, String icon) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBackground(UIConstants.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accentColor.darker(), 1),
                new EmptyBorder(18, 18, 18, 18)));

        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        card.add(lblIcon, BorderLayout.WEST);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UIConstants.FONT_SMALL);
        lblTitle.setForeground(UIConstants.TEXT_SECONDARY);
        textPanel.add(lblTitle);

        lblValue.setFont(UIConstants.FONT_CARD_NUM);
        lblValue.setForeground(accentColor);
        textPanel.add(lblValue);

        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    @Override
    public void refresh() {
        try {
            lblTotalSales.setText(String.format("₹ %.2f", service.getTotalSales()));
            lblTodaySales.setText(String.format("₹ %.2f", service.getTodaySales()));
            lblMonthlyRevenueValue.setText(String.format("₹ %.2f", service.getMonthlySales()));
            lblProductCount.setText(String.valueOf(service.getProductCount()));
            lblCustomerCount.setText(String.valueOf(service.getCustomerCount()));

            lowStockListModel.clear();
            var lowStockItems = service.getLowStock();
            if (lowStockItems.isEmpty()) {
                lowStockListModel.addElement("  ✅ All stock levels are healthy.");
            } else {
                for (var inv : lowStockItems) {
                    lowStockListModel.addElement(String.format("  ⚠ %s @ %s — Qty: %d (Min: %d)",
                            inv.getProductName(), inv.getBranchName(), inv.getQuantity(), inv.getMinStock()));
                }
            }
        } catch (Exception ignored) {
        }
    }
}
