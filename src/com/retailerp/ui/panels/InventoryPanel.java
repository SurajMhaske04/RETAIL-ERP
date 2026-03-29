package com.retailerp.ui.panels;

import com.retailerp.model.Branch;
import com.retailerp.model.Inventory;
import com.retailerp.model.Product;
import com.retailerp.service.ERPService;
import com.retailerp.util.EventBus;
import com.retailerp.util.Refreshable;
import com.retailerp.util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class InventoryPanel extends JPanel implements Refreshable {
    private final ERPService service = new ERPService();
    private DefaultTableModel tableModel;
    private JTable table;
    private JComboBox<Branch> cmbBranch;
    private JComboBox<Product> cmbProduct;
    private JTextField txtQty, txtMinStock;

    // Transfer section
    private JComboBox<Product> cmbTransProduct;
    private JComboBox<Branch> cmbFrom, cmbTo;
    private JTextField txtTransQty;

    public InventoryPanel() {
        setBackground(UIConstants.BG_DARK);
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        buildUI();

        EventBus.subscribe(EventBus.INVENTORY_CHANGED, e -> refresh());
        EventBus.subscribe("BRANCH_CHANGED", e -> refresh());
        EventBus.subscribe("PRODUCT_CHANGED", e -> refresh());

        refresh();
    }

    private void buildUI() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(UIConstants.PANEL_BG);
        tabs.setForeground(UIConstants.TEXT_PRIMARY);
        tabs.setFont(UIConstants.FONT_REGULAR);

        // ─── Tab 1: Stock Management ───
        JPanel stockPanel = new JPanel(new BorderLayout(10, 10));
        stockPanel.setBackground(UIConstants.BG_DARK);
        stockPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel form = UIConstants.createFormPanel();
        form.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 8));

        cmbBranch = UIConstants.createStyledComboBox();
        cmbProduct = UIConstants.createStyledComboBox();
        txtQty = UIConstants.createStyledTextField();
        txtQty.setPreferredSize(new Dimension(80, 30));
        txtMinStock = UIConstants.createStyledTextField();
        txtMinStock.setPreferredSize(new Dimension(80, 30));
        txtMinStock.setText("10");

        form.add(UIConstants.createLabel("Branch:"));
        form.add(cmbBranch);
        form.add(UIConstants.createLabel("Product:"));
        form.add(cmbProduct);
        form.add(UIConstants.createLabel("Qty:"));
        form.add(txtQty);
        form.add(UIConstants.createLabel("Min Stock:"));
        form.add(txtMinStock);

        JButton btnSave = UIConstants.createStyledButton("Save Stock", UIConstants.ACCENT_GREEN);
        btnSave.addActionListener(e -> saveStock());
        form.add(btnSave);

        stockPanel.add(form, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new String[] { "ID", "Product", "Branch", "Quantity", "Min Stock", "Status" }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(tableModel);
        stockPanel.add(UIConstants.createStyledScrollPane(table), BorderLayout.CENTER);

        tabs.addTab("Stock Management", stockPanel);

        // ─── Tab 2: Stock Transfer ───
        JPanel transferPanel = new JPanel(new BorderLayout(10, 10));
        transferPanel.setBackground(UIConstants.BG_DARK);
        transferPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel tForm = UIConstants.createFormPanel();
        tForm.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 8));

        cmbTransProduct = UIConstants.createStyledComboBox();
        cmbFrom = UIConstants.createStyledComboBox();
        cmbTo = UIConstants.createStyledComboBox();
        txtTransQty = UIConstants.createStyledTextField();
        txtTransQty.setPreferredSize(new Dimension(80, 30));

        tForm.add(UIConstants.createLabel("Product:"));
        tForm.add(cmbTransProduct);
        tForm.add(UIConstants.createLabel("From Branch:"));
        tForm.add(cmbFrom);
        tForm.add(UIConstants.createLabel("To Branch:"));
        tForm.add(cmbTo);
        tForm.add(UIConstants.createLabel("Qty:"));
        tForm.add(txtTransQty);

        JButton btnTransfer = UIConstants.createStyledButton("Transfer", UIConstants.ACCENT_ORANGE);
        btnTransfer.addActionListener(e -> doTransfer());
        tForm.add(btnTransfer);

        transferPanel.add(tForm, BorderLayout.NORTH);

        JLabel info = new JLabel("Transfer stock between branches. Ensure source branch has sufficient quantity.");
        info.setForeground(UIConstants.TEXT_SECONDARY);
        info.setFont(UIConstants.FONT_SMALL);
        info.setBorder(new EmptyBorder(10, 10, 10, 10));
        transferPanel.add(info, BorderLayout.CENTER);

        tabs.addTab("Stock Transfer", transferPanel);

        add(tabs, BorderLayout.CENTER);
    }

    @Override
    public void refresh() {
        Branch selB = (Branch) cmbBranch.getSelectedItem();
        Product selP = (Product) cmbProduct.getSelectedItem();
        Product selTP = (Product) cmbTransProduct.getSelectedItem();
        Branch selF = (Branch) cmbFrom.getSelectedItem();
        Branch selT = (Branch) cmbTo.getSelectedItem();

        cmbBranch.removeAllItems();
        cmbFrom.removeAllItems();
        cmbTo.removeAllItems();
        for (Branch b : service.getAllBranches()) {
            cmbBranch.addItem(b);
            cmbFrom.addItem(b);
            cmbTo.addItem(b);
        }
        cmbProduct.removeAllItems();
        cmbTransProduct.removeAllItems();
        for (Product p : service.getAllProducts()) {
            cmbProduct.addItem(p);
            cmbTransProduct.addItem(p);
        }

        restoreBranchCombo(cmbBranch, selB);
        restoreBranchCombo(cmbFrom, selF);
        restoreBranchCombo(cmbTo, selT);
        restoreProductCombo(cmbProduct, selP);
        restoreProductCombo(cmbTransProduct, selTP);

        tableModel.setRowCount(0);
        for (Inventory inv : service.getAllInventory()) {
            String status = inv.getQuantity() <= inv.getMinStock() ? "⚠ LOW" : "✅ OK";
            tableModel.addRow(new Object[] { inv.getInventoryId(), inv.getProductName(), inv.getBranchName(),
                    inv.getQuantity(), inv.getMinStock(), status });
        }
    }

    private void restoreBranchCombo(JComboBox<Branch> combo, Branch b) {
        if (b != null) {
            for (int i = 0; i < combo.getItemCount(); i++) {
                if (combo.getItemAt(i).getBranchId() == b.getBranchId()) {
                    combo.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void restoreProductCombo(JComboBox<Product> combo, Product p) {
        if (p != null) {
            for (int i = 0; i < combo.getItemCount(); i++) {
                if (combo.getItemAt(i).getProductId() == p.getProductId()) {
                    combo.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void saveStock() {
        Product p = (Product) cmbProduct.getSelectedItem();
        Branch b = (Branch) cmbBranch.getSelectedItem();
        if (p == null || b == null) {
            msg("Select product and branch.");
            return;
        }
        try {
            int qty = Integer.parseInt(txtQty.getText().trim());
            int min = Integer.parseInt(txtMinStock.getText().trim());
            if (service.upsertStock(p.getProductId(), b.getBranchId(), qty, min)) {
                msg("Stock saved.");
            } else
                msg("Failed.");
        } catch (NumberFormatException ex) {
            msg("Enter valid numbers.");
        }
    }

    private void doTransfer() {
        Product p = (Product) cmbTransProduct.getSelectedItem();
        Branch from = (Branch) cmbFrom.getSelectedItem();
        Branch to = (Branch) cmbTo.getSelectedItem();
        if (p == null || from == null || to == null) {
            msg("Select all fields.");
            return;
        }
        if (from.getBranchId() == to.getBranchId()) {
            msg("Source and destination must differ.");
            return;
        }
        try {
            int qty = Integer.parseInt(txtTransQty.getText().trim());
            if (qty <= 0) {
                msg("Quantity must be positive.");
                return;
            }
            if (service.transferStock(p.getProductId(), from.getBranchId(), to.getBranchId(), qty)) {
                msg("Transfer complete.");
            } else
                msg("Transfer failed — insufficient stock?");
        } catch (NumberFormatException ex) {
            msg("Enter valid quantity.");
        }
    }

    private void msg(String m) {
        JOptionPane.showMessageDialog(this, m);
    }
}
