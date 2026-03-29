package com.retailerp.ui;

import com.retailerp.ui.panels.*;
import com.retailerp.util.SessionManager;
import com.retailerp.util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainFrame extends JFrame {
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JLabel lblHeaderTitle;

    private static final String[] MENU_ITEMS = {
            "Dashboard", "Products", "Categories", "Branches", "Inventory",
            "Billing / POS", "Customers", "Suppliers", "Purchase Orders",
            "Employees", "Reports", "Audit Logs", "Users"
    };

    private static final String[] MENU_ICONS = {
            "📊", "📦", "🏷", "🏢", "📋",
            "🧾", "👥", "🚚", "📝",
            "👤", "📈", "🔍", "🔐"
    };

    public MainFrame() {
        setTitle("Retail ERP System — " + SessionManager.getCurrentUser().getFullName());
        setSize(1280, 780);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1100, 650));
        getContentPane().setBackground(UIConstants.BG_DARK);
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // ─── TOP HEADER ───
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIConstants.PANEL_BG);
        header.setPreferredSize(new Dimension(0, 55));
        header.setBorder(new EmptyBorder(10, 20, 10, 20));

        lblHeaderTitle = new JLabel("📊  Dashboard");
        lblHeaderTitle.setFont(UIConstants.FONT_TITLE);
        lblHeaderTitle.setForeground(UIConstants.TEXT_PRIMARY);
        header.add(lblHeaderTitle, BorderLayout.WEST);

        JPanel userInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userInfo.setOpaque(false);
        JLabel lblUser = new JLabel(
                SessionManager.getCurrentUser().getFullName() + " (" + SessionManager.getRoleName() + ")");
        lblUser.setFont(UIConstants.FONT_REGULAR);
        lblUser.setForeground(UIConstants.TEXT_SECONDARY);
        userInfo.add(lblUser);

        JButton btnLogout = UIConstants.createStyledButton("Logout", UIConstants.ACCENT_RED);
        btnLogout.setPreferredSize(new Dimension(100, 30));
        btnLogout.addActionListener(e -> {
            SessionManager.logout();
            dispose();
            new LoginFrame().setVisible(true);
        });
        userInfo.add(btnLogout);
        header.add(userInfo, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ─── SIDEBAR ───
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UIConstants.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(210, 0));
        sidebar.setBorder(new EmptyBorder(10, 0, 10, 0));

        // App title in sidebar
        JLabel appTitle = new JLabel("  RetailERP");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        appTitle.setForeground(UIConstants.ACCENT_BLUE);
        appTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        appTitle.setBorder(new EmptyBorder(8, 15, 15, 0));
        sidebar.add(appTitle);

        for (int i = 0; i < MENU_ITEMS.length; i++) {
            // Role-based visibility
            if (!SessionManager.isAdmin()) {
                if (MENU_ITEMS[i].equals("Users") || MENU_ITEMS[i].equals("Audit Logs"))
                    continue;
            }
            sidebar.add(createMenuItem(MENU_ICONS[i], MENU_ITEMS[i]));
        }

        sidebar.add(Box.createVerticalGlue());
        JScrollPane sidebarScroll = new JScrollPane(sidebar);
        sidebarScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sidebarScroll.setBorder(null);
        sidebarScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(sidebarScroll, BorderLayout.WEST);

        // ─── CONTENT AREA ───
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(UIConstants.BG_DARK);

        addPanel(new DashboardPanel(), "Dashboard");
        addPanel(new ProductPanel(), "Products");
        addPanel(new CategoryPanel(), "Categories");
        addPanel(new BranchPanel(), "Branches");
        addPanel(new InventoryPanel(), "Inventory");
        addPanel(new BillingPanel(), "Billing / POS");
        addPanel(new CustomerPanel(), "Customers");
        addPanel(new SupplierPanel(), "Suppliers");
        addPanel(new PurchaseOrderPanel(), "Purchase Orders");
        addPanel(new EmployeePanel(), "Employees");
        addPanel(new ReportsPanel(), "Reports");
        addPanel(new AuditLogPanel(), "Audit Logs");
        addPanel(new UserPanel(), "Users");

        add(contentPanel, BorderLayout.CENTER);
    }

    private void addPanel(JPanel panel, String name) {
        panel.setName(name);
        contentPanel.add(panel, name);
    }

    private JPanel createMenuItem(String icon, String text) {
        JPanel item = new JPanel(new BorderLayout());
        item.setMaximumSize(new Dimension(210, 42));
        item.setPreferredSize(new Dimension(210, 42));
        item.setBackground(UIConstants.SIDEBAR_BG);
        item.setBorder(new EmptyBorder(0, 15, 0, 10));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel lbl = new JLabel(icon + "   " + text);
        lbl.setFont(UIConstants.FONT_SIDEBAR);
        lbl.setForeground(UIConstants.TEXT_PRIMARY);
        item.add(lbl, BorderLayout.CENTER);

        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                item.setBackground(UIConstants.PANEL_BG);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                item.setBackground(UIConstants.SIDEBAR_BG);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // Find the target panel and refresh it
                for (Component comp : contentPanel.getComponents()) {
                    if (comp.getName() != null && comp.getName().equals(text)) {
                        if (comp instanceof com.retailerp.util.Refreshable) {
                            ((com.retailerp.util.Refreshable) comp).refresh();
                        }
                        break; // Stop searching once found
                    }
                }
                cardLayout.show(contentPanel, text);
                lblHeaderTitle.setText(icon + "  " + text);
            }
        });
        return item;
    }
}
