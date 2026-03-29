package com.retailerp.ui.panels;

import com.retailerp.model.Branch;
import com.retailerp.service.ERPService;
import com.retailerp.util.EventBus;
import com.retailerp.util.Refreshable;
import com.retailerp.util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class BranchPanel extends JPanel implements Refreshable {
    private final ERPService service = new ERPService();
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtName, txtAddress, txtCity, txtState, txtPhone;
    private JCheckBox chkActive;
    private int selectedId = -1;

    public BranchPanel() {
        setBackground(UIConstants.BG_DARK);
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        buildUI();

        EventBus.subscribe("BRANCH_CHANGED", e -> refresh());

        refresh();
    }

    private void buildUI() {
        JPanel form = UIConstants.createFormPanel();
        form.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 8, 5, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        txtName = UIConstants.createStyledTextField();
        txtAddress = UIConstants.createStyledTextField();
        txtCity = UIConstants.createStyledTextField();
        txtState = UIConstants.createStyledTextField();
        txtPhone = UIConstants.createStyledTextField();
        chkActive = new JCheckBox("Active", true);
        chkActive.setForeground(UIConstants.TEXT_PRIMARY);
        chkActive.setBackground(UIConstants.PANEL_BG);

        String[] labels = { "Branch Name", "Address", "City", "State", "Phone", "Active" };
        JComponent[] fields = { txtName, txtAddress, txtCity, txtState, txtPhone, chkActive };
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
        btnAdd.addActionListener(e -> addBranch());
        btnUpdate.addActionListener(e -> updateBranch());
        btnDelete.addActionListener(e -> deleteBranch());
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

        tableModel = new DefaultTableModel(new String[] { "ID", "Name", "Address", "City", "State", "Phone", "Active" },
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
        for (Branch b : service.getAllBranches())
            tableModel.addRow(new Object[] { b.getBranchId(), b.getBranchName(), b.getAddress(), b.getCity(),
                    b.getState(), b.getPhone(), b.isActive() });
    }

    private void onSelect() {
        int row = table.getSelectedRow();
        if (row < 0)
            return;
        selectedId = (int) tableModel.getValueAt(row, 0);
        txtName.setText(str(row, 1));
        txtAddress.setText(str(row, 2));
        txtCity.setText(str(row, 3));
        txtState.setText(str(row, 4));
        txtPhone.setText(str(row, 5));
        chkActive.setSelected(Boolean.TRUE.equals(tableModel.getValueAt(row, 6)));
    }

    private String str(int r, int c) {
        Object v = tableModel.getValueAt(r, c);
        return v != null ? v.toString() : "";
    }

    private void addBranch() {
        if (txtName.getText().trim().isEmpty()) {
            msg("Name required.");
            return;
        }
        Branch b = formToBranch();
        if (service.addBranch(b)) {
            clear();
        } else
            msg("Failed.");
    }

    private void updateBranch() {
        if (selectedId < 0) {
            msg("Select a branch.");
            return;
        }
        Branch b = formToBranch();
        b.setBranchId(selectedId);
        if (service.updateBranch(b)) {
            clear();
        } else
            msg("Failed.");
    }

    private void deleteBranch() {
        if (selectedId < 0)
            return;
        if (JOptionPane.showConfirmDialog(this, "Delete?", "Confirm", JOptionPane.YES_NO_OPTION) == 0)
            if (service.deleteBranch(selectedId)) {
                clear();
            }
    }

    private Branch formToBranch() {
        Branch b = new Branch();
        b.setBranchName(txtName.getText().trim());
        b.setAddress(txtAddress.getText().trim());
        b.setCity(txtCity.getText().trim());
        b.setState(txtState.getText().trim());
        b.setPhone(txtPhone.getText().trim());
        b.setActive(chkActive.isSelected());
        return b;
    }

    private void clear() {
        selectedId = -1;
        txtName.setText("");
        txtAddress.setText("");
        txtCity.setText("");
        txtState.setText("");
        txtPhone.setText("");
        chkActive.setSelected(true);
        table.clearSelection();
    }

    private void msg(String m) {
        JOptionPane.showMessageDialog(this, m);
    }
}
