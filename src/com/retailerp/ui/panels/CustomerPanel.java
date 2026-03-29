package com.retailerp.ui.panels;

import com.retailerp.model.Customer;
import com.retailerp.service.ERPService;
import com.retailerp.util.EventBus;
import com.retailerp.util.Refreshable;
import com.retailerp.util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CustomerPanel extends JPanel implements Refreshable {
    private final ERPService service = new ERPService();
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtName, txtPhone, txtEmail, txtAddress;
    private int selectedId = -1;

    public CustomerPanel() {
        setBackground(UIConstants.BG_DARK);
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        buildUI();

        EventBus.subscribe("CUSTOMER_CHANGED", e -> refresh());

        refresh();
    }

    private void buildUI() {
        JPanel form = UIConstants.createFormPanel();
        form.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 8, 5, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        txtName = UIConstants.createStyledTextField();
        txtPhone = UIConstants.createStyledTextField();
        txtEmail = UIConstants.createStyledTextField();
        txtAddress = UIConstants.createStyledTextField();

        String[] labels = { "Customer Name", "Phone", "Email", "Address" };
        JComponent[] fields = { txtName, txtPhone, txtEmail, txtAddress };
        for (int i = 0; i < labels.length; i++) {
            g.gridx = (i % 2) * 2;
            g.gridy = i / 2;
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
        btnAdd.addActionListener(e -> addCustomer());
        btnUpdate.addActionListener(e -> updateCustomer());
        btnDelete.addActionListener(e -> deleteCustomer());
        btnClear.addActionListener(e -> clear());
        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);
        g.gridx = 0;
        g.gridy = 3;
        g.gridwidth = 4;
        form.add(btnPanel, g);
        add(form, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[] { "ID", "Name", "Phone", "Email", "Address", "Loyalty Pts" },
                0) {
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
        for (Customer c : service.getAllCustomers())
            tableModel.addRow(new Object[] { c.getCustomerId(), c.getCustomerName(), c.getPhone(), c.getEmail(),
                    c.getAddress(), c.getLoyaltyPoints() });
    }

    private void onSelect() {
        int row = table.getSelectedRow();
        if (row < 0)
            return;
        selectedId = (int) tableModel.getValueAt(row, 0);
        txtName.setText(str(row, 1));
        txtPhone.setText(str(row, 2));
        txtEmail.setText(str(row, 3));
        txtAddress.setText(str(row, 4));
    }

    private String str(int r, int c) {
        Object v = tableModel.getValueAt(r, c);
        return v != null ? v.toString() : "";
    }

    private void addCustomer() {
        if (txtName.getText().trim().isEmpty()) {
            msg("Name required.");
            return;
        }
        Customer c = formToCustomer();
        if (service.addCustomer(c)) {
            clear();
        } else
            msg("Failed.");
    }

    private void updateCustomer() {
        if (selectedId < 0) {
            msg("Select a customer.");
            return;
        }
        Customer c = formToCustomer();
        c.setCustomerId(selectedId);
        if (service.updateCustomer(c)) {
            clear();
        } else
            msg("Failed.");
    }

    private void deleteCustomer() {
        if (selectedId < 0)
            return;
        if (JOptionPane.showConfirmDialog(this, "Delete?", "Confirm", JOptionPane.YES_NO_OPTION) == 0)
            if (service.deleteCustomer(selectedId)) {
                clear();
            }
    }

    private Customer formToCustomer() {
        Customer c = new Customer();
        c.setCustomerName(txtName.getText().trim());
        c.setPhone(txtPhone.getText().trim());
        c.setEmail(txtEmail.getText().trim());
        c.setAddress(txtAddress.getText().trim());
        return c;
    }

    private void clear() {
        selectedId = -1;
        txtName.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        txtAddress.setText("");
        table.clearSelection();
    }

    private void msg(String m) {
        JOptionPane.showMessageDialog(this, m);
    }
}
