package com.retailerp.ui.panels;

import com.retailerp.model.Branch;
import com.retailerp.model.Employee;
import com.retailerp.model.Role;
import com.retailerp.service.ERPService;
import com.retailerp.util.EventBus;
import com.retailerp.util.Refreshable;
import com.retailerp.util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;

public class EmployeePanel extends JPanel implements Refreshable {
    private final ERPService service = new ERPService();
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtFirst, txtLast, txtEmail, txtPhone, txtAddress, txtSalary, txtJoinDate;
    private JComboBox<Role> cmbRole;
    private JComboBox<Branch> cmbBranch;
    private int selectedId = -1;

    public EmployeePanel() {
        setBackground(UIConstants.BG_DARK);
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        buildUI();

        EventBus.subscribe("EMPLOYEE_CHANGED", e -> refresh());
        EventBus.subscribe("BRANCH_CHANGED", e -> refresh());

        refresh();
    }

    private void buildUI() {
        JPanel form = UIConstants.createFormPanel();
        form.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 8, 5, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        txtFirst = UIConstants.createStyledTextField();
        txtLast = UIConstants.createStyledTextField();
        txtEmail = UIConstants.createStyledTextField();
        txtPhone = UIConstants.createStyledTextField();
        txtAddress = UIConstants.createStyledTextField();
        txtSalary = UIConstants.createStyledTextField();
        txtJoinDate = UIConstants.createStyledTextField();
        txtJoinDate.setToolTipText("YYYY-MM-DD");
        cmbRole = UIConstants.createStyledComboBox();
        cmbBranch = UIConstants.createStyledComboBox();

        String[] labels = { "First Name", "Last Name", "Email", "Phone", "Role", "Branch", "Salary",
                "Join Date (YYYY-MM-DD)", "Address" };
        JComponent[] fields = { txtFirst, txtLast, txtEmail, txtPhone, cmbRole, cmbBranch, txtSalary, txtJoinDate,
                txtAddress };
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
        btnAdd.addActionListener(e -> addEmployee());
        btnUpdate.addActionListener(e -> updateEmployee());
        btnDelete.addActionListener(e -> deleteEmployee());
        btnClear.addActionListener(e -> clear());
        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);
        g.gridx = 0;
        g.gridy = 4;
        g.gridwidth = 6;
        form.add(btnPanel, g);
        add(form, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new String[] { "ID", "First", "Last", "Email", "Phone", "Role", "Branch", "Salary", "Join Date" }, 0) {
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
        for (Employee emp : service.getAllEmployees())
            tableModel.addRow(new Object[] { emp.getEmployeeId(), emp.getFirstName(), emp.getLastName(),
                    emp.getEmail(), emp.getPhone(), emp.getRoleName(), emp.getBranchName(),
                    emp.getSalary(), emp.getJoinDate() });
    }

    private void onSelect() {
        int row = table.getSelectedRow();
        if (row < 0)
            return;
        selectedId = (int) tableModel.getValueAt(row, 0);
        txtFirst.setText(str(row, 1));
        txtLast.setText(str(row, 2));
        txtEmail.setText(str(row, 3));
        txtPhone.setText(str(row, 4));

        String roleName = str(row, 5);
        for (int i = 0; i < cmbRole.getItemCount(); i++) {
            if (cmbRole.getItemAt(i).getRoleName().equals(roleName)) {
                cmbRole.setSelectedIndex(i);
                break;
            }
        }
        String branchName = str(row, 6);
        for (int i = 0; i < cmbBranch.getItemCount(); i++) {
            if (cmbBranch.getItemAt(i).getBranchName().equals(branchName)) {
                cmbBranch.setSelectedIndex(i);
                break;
            }
        }

        txtSalary.setText(str(row, 7));
        txtJoinDate.setText(str(row, 8));
    }

    private String str(int r, int c) {
        Object v = tableModel.getValueAt(r, c);
        return v != null ? v.toString() : "";
    }

    private void addEmployee() {
        if (txtFirst.getText().trim().isEmpty()) {
            msg("First name required.");
            return;
        }
        Employee emp = formToEmployee();
        if (service.addEmployee(emp)) {
            clear();
        } else
            msg("Failed.");
    }

    private void updateEmployee() {
        if (selectedId < 0) {
            msg("Select an employee.");
            return;
        }
        Employee emp = formToEmployee();
        emp.setEmployeeId(selectedId);
        if (service.updateEmployee(emp)) {
            clear();
        } else
            msg("Failed.");
    }

    private void deleteEmployee() {
        if (selectedId < 0)
            return;
        if (JOptionPane.showConfirmDialog(this, "Delete?", "Confirm", JOptionPane.YES_NO_OPTION) == 0)
            if (service.deleteEmployee(selectedId)) {
                clear();
            }
    }

    private Employee formToEmployee() {
        Employee emp = new Employee();
        emp.setFirstName(txtFirst.getText().trim());
        emp.setLastName(txtLast.getText().trim());
        emp.setEmail(txtEmail.getText().trim());
        emp.setPhone(txtPhone.getText().trim());
        emp.setAddress(txtAddress.getText().trim());
        Role r = (Role) cmbRole.getSelectedItem();
        if (r != null)
            emp.setRoleId(r.getRoleId());
        Branch b = (Branch) cmbBranch.getSelectedItem();
        if (b != null)
            emp.setBranchId(b.getBranchId());
        try {
            emp.setSalary(Double.parseDouble(txtSalary.getText().trim()));
        } catch (Exception ignored) {
        }
        try {
            emp.setJoinDate(Date.valueOf(txtJoinDate.getText().trim()));
        } catch (Exception ignored) {
        }
        emp.setActive(true);
        return emp;
    }

    private void clear() {
        selectedId = -1;
        txtFirst.setText("");
        txtLast.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        txtAddress.setText("");
        txtSalary.setText("");
        txtJoinDate.setText("");
        table.clearSelection();
    }

    private void msg(String m) {
        JOptionPane.showMessageDialog(this, m);
    }
}
