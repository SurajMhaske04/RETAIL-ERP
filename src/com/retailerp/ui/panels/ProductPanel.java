package com.retailerp.ui.panels;

import com.retailerp.model.Category;
import com.retailerp.model.Product;
import com.retailerp.service.ERPService;
import com.retailerp.util.EventBus;
import com.retailerp.util.Refreshable;
import com.retailerp.util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProductPanel extends JPanel implements Refreshable {
    private final ERPService service = new ERPService();
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtName, txtBrand, txtUnit, txtCost, txtSell, txtGst, txtDesc;
    private JComboBox<Category> cmbCategory;
    private int selectedId = -1;

    public ProductPanel() {
        setBackground(UIConstants.BG_DARK);
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        buildUI();

        EventBus.subscribe("CATEGORY_CHANGED", e -> refresh());
        EventBus.subscribe("PRODUCT_CHANGED", e -> refresh());

        refresh();
    }

    private void buildUI() {
        // ─── FORM ───
        JPanel formPanel = UIConstants.createFormPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 8, 5, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        String[] labels = { "Name", "Category", "Brand", "Unit", "Cost Price", "Sell Price", "GST %", "Description" };
        txtName = UIConstants.createStyledTextField();
        cmbCategory = UIConstants.createStyledComboBox();
        txtBrand = UIConstants.createStyledTextField();
        txtUnit = UIConstants.createStyledTextField();
        txtUnit.setText("pcs");
        txtCost = UIConstants.createStyledTextField();
        txtSell = UIConstants.createStyledTextField();
        txtGst = UIConstants.createStyledTextField();
        txtGst.setText("18.0");
        txtDesc = UIConstants.createStyledTextField();

        JComponent[] fields = { txtName, cmbCategory, txtBrand, txtUnit, txtCost, txtSell, txtGst, txtDesc };

        for (int i = 0; i < labels.length; i++) {
            g.gridx = (i % 4) * 2;
            g.gridy = i / 4;
            g.weightx = 0;
            formPanel.add(UIConstants.createLabel(labels[i]), g);
            g.gridx++;
            g.weightx = 1;
            formPanel.add(fields[i], g);
        }

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        btnPanel.setOpaque(false);
        JButton btnAdd = UIConstants.createStyledButton("Add", UIConstants.ACCENT_GREEN);
        JButton btnUpdate = UIConstants.createStyledButton("Update", UIConstants.ACCENT_BLUE);
        JButton btnDelete = UIConstants.createStyledButton("Delete", UIConstants.ACCENT_RED);
        JButton btnClear = UIConstants.createStyledButton("Clear", UIConstants.CARD_BG);

        btnAdd.addActionListener(e -> addProduct());
        btnUpdate.addActionListener(e -> updateProduct());
        btnDelete.addActionListener(e -> deleteProduct());
        btnClear.addActionListener(e -> clearForm());

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        g.gridx = 0;
        g.gridy = 3;
        g.gridwidth = 8;
        formPanel.add(btnPanel, g);

        add(formPanel, BorderLayout.NORTH);

        // ─── TABLE ───
        tableModel = new DefaultTableModel(
                new String[] { "ID", "Name", "Category", "Brand", "Unit", "Cost", "Sell", "GST%" }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.getSelectionModel().addListSelectionListener(e -> onRowSelect());
        add(UIConstants.createStyledScrollPane(table), BorderLayout.CENTER);
    }

    @Override
    public void refresh() {
        Category selectedCat = (Category) cmbCategory.getSelectedItem();

        cmbCategory.removeAllItems();
        for (Category c : service.getAllCategories()) {
            cmbCategory.addItem(c);
        }

        if (selectedCat != null) {
            for (int i = 0; i < cmbCategory.getItemCount(); i++) {
                if (cmbCategory.getItemAt(i).getCategoryId() == selectedCat.getCategoryId()) {
                    cmbCategory.setSelectedIndex(i);
                    break;
                }
            }
        }

        tableModel.setRowCount(0);
        for (Product p : service.getAllProducts()) {
            tableModel.addRow(new Object[] { p.getProductId(), p.getProductName(), p.getCategoryName(),
                    p.getBrand(), p.getUnit(), p.getCostPrice(), p.getSellPrice(), p.getGstPercent() });
        }
    }

    private void onRowSelect() {
        int row = table.getSelectedRow();
        if (row < 0)
            return;
        selectedId = (int) tableModel.getValueAt(row, 0);
        txtName.setText(str(row, 1));

        String catName = str(row, 2);
        for (int i = 0; i < cmbCategory.getItemCount(); i++) {
            if (cmbCategory.getItemAt(i).getCategoryName().equals(catName)) {
                cmbCategory.setSelectedIndex(i);
                break;
            }
        }

        txtBrand.setText(str(row, 3));
        txtUnit.setText(str(row, 4));
        txtCost.setText(str(row, 5));
        txtSell.setText(str(row, 6));
        txtGst.setText(str(row, 7));
    }

    private String str(int r, int c) {
        Object v = tableModel.getValueAt(r, c);
        return v != null ? v.toString() : "";
    }

    private void addProduct() {
        if (txtName.getText().trim().isEmpty()) {
            msg("Product name is required.");
            return;
        }
        Product p = formToProduct();
        if (service.addProduct(p)) {
            clearForm();
            msg("Product added.");
        } else
            msg("Failed to add product.");
    }

    private void updateProduct() {
        if (selectedId < 0) {
            msg("Select a product first.");
            return;
        }
        Product p = formToProduct();
        p.setProductId(selectedId);
        if (service.updateProduct(p)) {
            clearForm();
            msg("Product updated.");
        } else
            msg("Failed to update product.");
    }

    private void deleteProduct() {
        if (selectedId < 0) {
            msg("Select a product first.");
            return;
        }
        int opt = JOptionPane.showConfirmDialog(this, "Delete this product?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            if (service.deleteProduct(selectedId)) {
                clearForm();
                msg("Deleted.");
            } else
                msg("Failed to delete.");
        }
    }

    private Product formToProduct() {
        Product p = new Product();
        p.setProductName(txtName.getText().trim());
        Category sel = (Category) cmbCategory.getSelectedItem();
        if (sel != null)
            p.setCategoryId(sel.getCategoryId());
        p.setBrand(txtBrand.getText().trim());
        p.setUnit(txtUnit.getText().trim());
        try {
            p.setCostPrice(Double.parseDouble(txtCost.getText().trim()));
        } catch (NumberFormatException ignored) {
        }
        try {
            p.setSellPrice(Double.parseDouble(txtSell.getText().trim()));
        } catch (NumberFormatException ignored) {
        }
        try {
            p.setGstPercent(Double.parseDouble(txtGst.getText().trim()));
        } catch (NumberFormatException ignored) {
            p.setGstPercent(18);
        }
        p.setDescription(txtDesc.getText().trim());
        p.setActive(true);
        return p;
    }

    private void clearForm() {
        selectedId = -1;
        txtName.setText("");
        txtBrand.setText("");
        txtUnit.setText("pcs");
        txtCost.setText("");
        txtSell.setText("");
        txtGst.setText("18.0");
        txtDesc.setText("");
        table.clearSelection();
    }

    private void msg(String m) {
        JOptionPane.showMessageDialog(this, m);
    }
}
