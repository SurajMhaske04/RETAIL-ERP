package com.retailerp.ui.panels;

import com.retailerp.model.Supplier;
import com.retailerp.service.ERPService;
import com.retailerp.util.EventBus;
import com.retailerp.util.Refreshable;
import com.retailerp.util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SupplierPanel extends JPanel implements Refreshable {
    private final ERPService service = new ERPService();
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtName, txtContact, txtPhone, txtEmail, txtAddress, txtGst;
    private int selectedId = -1;

    public SupplierPanel() {
        setBackground(UIConstants.BG_DARK);
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        buildUI();

        EventBus.subscribe("SUPPLIER_CHANGED", e -> refresh());

        refresh();
    }

    private void buildUI() {
        JPanel form = UIConstants.createFormPanel();
        form.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 8, 5, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        txtName = UIConstants.createStyledTextField();
        txtContact = UIConstants.createStyledTextField();
        txtPhone = UIConstants.createStyledTextField();
        txtEmail = UIConstants.createStyledTextField();
        txtAddress = UIConstants.createStyledTextField();
        txtGst = UIConstants.createStyledTextField();

        String[] labels = { "Supplier Name", "Contact Person", "Phone", "Email", "Address", "GST Number" };
        JComponent[] fields = { txtName, txtContact, txtPhone, txtEmail, txtAddress, txtGst };
        for (int i = 0; i < labels.length; i++) {
            g.gridx = (i % 3) * 2;
            g.gridy = i / 3;
            g.weightx = 0;
            form.add(UIConstants.createLabel(labels[i]), g);
            g.gridx++;
            g.weightx = 1;
            form.add(fields[i], g);
        }

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        btnPanel.setOpaque(false);
        JButton btnAdd = UIConstants.createStyledButton("Add", UIConstants.ACCENT_GREEN);
        JButton btnUpdate = UIConstants.createStyledButton("Update", UIConstants.ACCENT_BLUE);
        JButton btnDelete = UIConstants.createStyledButton("Delete", UIConstants.ACCENT_RED);
        JButton btnClear = UIConstants.createStyledButton("Clear", UIConstants.CARD_BG);
        btnAdd.addActionListener(e -> addSupplier());
        btnUpdate.addActionListener(e -> updateSupplier());
        btnDelete.addActionListener(e -> deleteSupplier());
        btnClear.addActionListener(e -> clear());
        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);
        g.gridx = 0;
        g.gridy = 3;
        g.gridwidth = 6;
        form.add(btnPanel, g);
        add(form, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[] { "ID", "Name", "Contact", "Phone", "Email", "GST No." }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.getSelectionModel().addListSelectionListener(e -> onSelect());
        add(UIConstants.createStyledScrollPane(table), BorderLayout.CENTER);
    }

    @Override
    public void refresh() {
        tableModel.setRowCount(0);
        for (Supplier s : service.getAllSuppliers())
            tableModel.addRow(new Object[] { s.getSupplierId(), s.getSupplierName(), s.getContactPerson(), s.getPhone(),
                    s.getEmail(), s.getGstNumber() });
    }

    private void onSelect() {
        int row = table.getSelectedRow();
        if (row < 0)
            return;
        selectedId = (int) tableModel.getValueAt(row, 0);
        txtName.setText(str(row, 1));
        txtContact.setText(str(row, 2));
        txtPhone.setText(str(row, 3));
        txtEmail.setText(str(row, 4));
        txtGst.setText(str(row, 5));
    }

    private String str(int r, int c) {
        Object v = tableModel.getValueAt(r, c);
        return v != null ? v.toString() : "";
    }

    private void addSupplier() {
        if (txtName.getText().trim().isEmpty()) {
            msg("Name required.");
            return;
        }
        Supplier s = formToSupplier();
        if (service.addSupplier(s)) {
            clear();
        } else
            msg("Failed.");
    }

    private void updateSupplier() {
        if (selectedId < 0) {
            msg("Select a supplier.");
            return;
        }
        Supplier s = formToSupplier();
        s.setSupplierId(selectedId);
        if (service.updateSupplier(s)) {
            clear();
        } else
            msg("Failed.");
    }

    private void deleteSupplier() {
        if (selectedId < 0)
            return;
        if (JOptionPane.showConfirmDialog(this, "Delete?", "Confirm", JOptionPane.YES_NO_OPTION) == 0)
            if (service.deleteSupplier(selectedId)) {
                clear();
            }
    }

    private Supplier formToSupplier() {
        Supplier s = new Supplier();
        s.setSupplierName(txtName.getText().trim());
        s.setContactPerson(txtContact.getText().trim());
        s.setPhone(txtPhone.getText().trim());
        s.setEmail(txtEmail.getText().trim());
        s.setAddress(txtAddress.getText().trim());
        s.setGstNumber(txtGst.getText().trim());
        s.setActive(true);
        return s;
    }

    private void clear() {
        selectedId = -1;
        txtName.setText("");
        txtContact.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        txtAddress.setText("");
        txtGst.setText("");
        table.clearSelection();
    }

    private void msg(String m) {
        JOptionPane.showMessageDialog(this, m);
    }
}
