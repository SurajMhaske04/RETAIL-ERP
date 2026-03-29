package com.retailerp.ui.panels;

import com.retailerp.model.Category;
import com.retailerp.service.ERPService;
import com.retailerp.util.EventBus;
import com.retailerp.util.Refreshable;
import com.retailerp.util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CategoryPanel extends JPanel implements Refreshable {
    private final ERPService service = new ERPService();
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtName, txtDesc;
    private int selectedId = -1;

    public CategoryPanel() {
        setBackground(UIConstants.BG_DARK);
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        buildUI();

        EventBus.subscribe("CATEGORY_CHANGED", e -> refresh());

        refresh();
    }

    private void buildUI() {
        JPanel formPanel = UIConstants.createFormPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 8, 5, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        txtName = UIConstants.createStyledTextField();
        txtDesc = UIConstants.createStyledTextField();

        g.gridx = 0;
        g.gridy = 0;
        formPanel.add(UIConstants.createLabel("Category Name"), g);
        g.gridx = 1;
        g.weightx = 1;
        formPanel.add(txtName, g);
        g.gridx = 2;
        g.weightx = 0;
        formPanel.add(UIConstants.createLabel("Description"), g);
        g.gridx = 3;
        g.weightx = 1;
        formPanel.add(txtDesc, g);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        btnPanel.setOpaque(false);
        JButton btnAdd = UIConstants.createStyledButton("Add", UIConstants.ACCENT_GREEN);
        JButton btnUpdate = UIConstants.createStyledButton("Update", UIConstants.ACCENT_BLUE);
        JButton btnDelete = UIConstants.createStyledButton("Delete", UIConstants.ACCENT_RED);
        JButton btnClear = UIConstants.createStyledButton("Clear", UIConstants.CARD_BG);
        btnAdd.addActionListener(e -> add());
        btnUpdate.addActionListener(e -> update());
        btnDelete.addActionListener(e -> delete());
        btnClear.addActionListener(e -> clear());
        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);
        g.gridx = 0;
        g.gridy = 1;
        g.gridwidth = 4;
        formPanel.add(btnPanel, g);
        add(formPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[] { "ID", "Name", "Description" }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                selectedId = (int) tableModel.getValueAt(row, 0);
                txtName.setText(tableModel.getValueAt(row, 1).toString());
                txtDesc.setText(tableModel.getValueAt(row, 2) != null ? tableModel.getValueAt(row, 2).toString() : "");
            }
        });
        add(UIConstants.createStyledScrollPane(table), BorderLayout.CENTER);
    }

    @Override
    public void refresh() {
        tableModel.setRowCount(0);
        for (Category c : service.getAllCategories())
            tableModel.addRow(new Object[] { c.getCategoryId(), c.getCategoryName(), c.getDescription() });
    }

    private void add() {
        if (txtName.getText().trim().isEmpty()) {
            msg("Name required.");
            return;
        }
        Category c = new Category();
        c.setCategoryName(txtName.getText().trim());
        c.setDescription(txtDesc.getText().trim());
        if (service.addCategory(c)) {
            clear();
        } else
            msg("Failed.");
    }

    private void update() {
        if (selectedId < 0) {
            msg("Select a row.");
            return;
        }
        Category c = new Category();
        c.setCategoryId(selectedId);
        c.setCategoryName(txtName.getText().trim());
        c.setDescription(txtDesc.getText().trim());
        if (service.updateCategory(c)) {
            clear();
        } else
            msg("Failed.");
    }

    private void delete() {
        if (selectedId < 0)
            return;
        if (JOptionPane.showConfirmDialog(this, "Delete?", "Confirm", JOptionPane.YES_NO_OPTION) == 0)
            if (service.deleteCategory(selectedId)) {
                clear();
            }
    }

    private void clear() {
        selectedId = -1;
        txtName.setText("");
        txtDesc.setText("");
        table.clearSelection();
    }

    private void msg(String m) {
        JOptionPane.showMessageDialog(this, m);
    }
}
