package com.retailerp.ui.panels;

import com.retailerp.model.Branch;
import com.retailerp.model.Role;
import com.retailerp.model.User;
import com.retailerp.service.ERPService;
import com.retailerp.util.EventBus;
import com.retailerp.util.Refreshable;
import com.retailerp.util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class UserPanel extends JPanel implements Refreshable {
    private final ERPService service = new ERPService();
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtUsername, txtPassword, txtFullName;
    private JComboBox<Role> cmbRole;
    private JComboBox<Branch> cmbBranch;
    private JCheckBox chkActive;
    private int selectedId = -1;

    public UserPanel() {
        setBackground(UIConstants.BG_DARK);
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        buildUI();

        EventBus.subscribe("USER_CHANGED", e -> refresh());
        EventBus.subscribe("BRANCH_CHANGED", e -> refresh());

        refresh();
    }

    private void buildUI() {
        JPanel form = UIConstants.createFormPanel();
        form.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 8, 5, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        txtUsername = UIConstants.createStyledTextField();
        txtPassword = UIConstants.createStyledTextField();
        txtFullName = UIConstants.createStyledTextField();
        cmbRole = UIConstants.createStyledComboBox();
        cmbBranch = UIConstants.createStyledComboBox();
        chkActive = new JCheckBox("Active", true);
        chkActive.setForeground(UIConstants.TEXT_PRIMARY);
        chkActive.setBackground(UIConstants.PANEL_BG);

        String[] labels = { "Username", "Password", "Full Name", "Role", "Branch", "Active" };
        JComponent[] fields = { txtUsername, txtPassword, txtFullName, cmbRole, cmbBranch, chkActive };
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
        btnAdd.addActionListener(e -> addUser());
        btnUpdate.addActionListener(e -> updateUser());
        btnDelete.addActionListener(e -> deleteUser());
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

        tableModel = new DefaultTableModel(new String[] { "ID", "Username", "Full Name", "Role", "Branch", "Active" },
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
        Role selectedRole = (Role) cmbRole.getSelectedItem();
        Branch selectedBranch = (Branch) cmbBranch.getSelectedItem();

        cmbRole.removeAllItems();
        cmbBranch.removeAllItems();

        for (Role r : service.getAllRoles())
            cmbRole.addItem(r);
        for (Branch b : service.getAllBranches())
            cmbBranch.addItem(b);

        if (selectedRole != null) {
            for (int i = 0; i < cmbRole.getItemCount(); i++) {
                if (cmbRole.getItemAt(i).getRoleId() == selectedRole.getRoleId()) {
                    cmbRole.setSelectedIndex(i);
                    break;
                }
            }
        }
        if (selectedBranch != null) {
            for (int i = 0; i < cmbBranch.getItemCount(); i++) {
                if (cmbBranch.getItemAt(i).getBranchId() == selectedBranch.getBranchId()) {
                    cmbBranch.setSelectedIndex(i);
                    break;
                }
            }
        }

        tableModel.setRowCount(0);
        for (User u : service.getAllUsers())
            tableModel.addRow(new Object[] { u.getUserId(), u.getUsername(), u.getFullName(),
                    u.getRoleName(), u.getBranchName(), u.isActive() });
    }

    private void onSelect() {
        int row = table.getSelectedRow();
        if (row < 0)
            return;
        selectedId = (int) tableModel.getValueAt(row, 0);
        txtUsername.setText(str(row, 1));
        txtFullName.setText(str(row, 2));
        chkActive.setSelected(Boolean.TRUE.equals(tableModel.getValueAt(row, 5)));
    }

    private String str(int r, int c) {
        Object v = tableModel.getValueAt(r, c);
        return v != null ? v.toString() : "";
    }

    private void addUser() {
        if (txtUsername.getText().trim().isEmpty() || txtPassword.getText().trim().isEmpty()) {
            msg("Username and password required.");
            return;
        }
        User u = formToUser();
        if (service.addUser(u)) {
            clear();
        } else
            msg("Failed.");
    }

    private void updateUser() {
        if (selectedId < 0) {
            msg("Select a user.");
            return;
        }
        User u = formToUser();
        u.setUserId(selectedId);
        if (service.updateUser(u)) {
            clear();
        } else
            msg("Failed.");
    }

    private void deleteUser() {
        if (selectedId < 0)
            return;
        if (JOptionPane.showConfirmDialog(this, "Delete user?", "Confirm", JOptionPane.YES_NO_OPTION) == 0)
            if (service.deleteUser(selectedId)) {
                clear();
            }
    }

    private User formToUser() {
        User u = new User();
        u.setUsername(txtUsername.getText().trim());
        u.setPassword(txtPassword.getText().trim());
        u.setFullName(txtFullName.getText().trim());
        Role r = (Role) cmbRole.getSelectedItem();
        if (r != null)
            u.setRoleId(r.getRoleId());
        Branch b = (Branch) cmbBranch.getSelectedItem();
        if (b != null)
            u.setBranchId(b.getBranchId());
        u.setActive(chkActive.isSelected());
        return u;
    }

    private void clear() {
        selectedId = -1;
        txtUsername.setText("");
        txtPassword.setText("");
        txtFullName.setText("");
        chkActive.setSelected(true);
        table.clearSelection();
    }

    private void msg(String m) {
        JOptionPane.showMessageDialog(this, m);
    }
}
