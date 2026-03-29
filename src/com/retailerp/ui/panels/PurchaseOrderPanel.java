package com.retailerp.ui.panels;

import com.retailerp.model.Branch;
import com.retailerp.model.PurchaseOrder;
import com.retailerp.model.Supplier;
import com.retailerp.service.ERPService;
import com.retailerp.util.EventBus;
import com.retailerp.util.Refreshable;
import com.retailerp.util.SessionManager;
import com.retailerp.util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;

public class PurchaseOrderPanel extends JPanel implements Refreshable {
    private final ERPService service = new ERPService();
    private DefaultTableModel tableModel;
    private JTable table;
    private JComboBox<Supplier> cmbSupplier;
    private JComboBox<Branch> cmbBranch;
    private JTextField txtAmount, txtNotes;
    private JComboBox<String> cmbStatus;

    public PurchaseOrderPanel() {
        setBackground(UIConstants.BG_DARK);
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        buildUI();

        EventBus.subscribe("SUPPLIER_CHANGED", e -> refresh());
        EventBus.subscribe("BRANCH_CHANGED", e -> refresh());
        EventBus.subscribe("PO_CHANGED", e -> refresh());

        refresh();
    }

    private void buildUI() {
        JPanel form = UIConstants.createFormPanel();
        form.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 8));

        cmbSupplier = UIConstants.createStyledComboBox();
        cmbBranch = UIConstants.createStyledComboBox();
        txtAmount = UIConstants.createStyledTextField();
        txtAmount.setPreferredSize(new Dimension(100, 30));
        txtNotes = UIConstants.createStyledTextField();
        txtNotes.setPreferredSize(new Dimension(160, 30));
        cmbStatus = UIConstants.createStyledComboBox();
        cmbStatus.addItem("Pending");
        cmbStatus.addItem("Approved");
        cmbStatus.addItem("Received");
        cmbStatus.addItem("Cancelled");

        form.add(UIConstants.createLabel("Supplier:"));
        form.add(cmbSupplier);
        form.add(UIConstants.createLabel("Branch:"));
        form.add(cmbBranch);
        form.add(UIConstants.createLabel("Amount:"));
        form.add(txtAmount);
        form.add(UIConstants.createLabel("Notes:"));
        form.add(txtNotes);

        JButton btnCreate = UIConstants.createStyledButton("Create PO", UIConstants.ACCENT_GREEN);
        btnCreate.addActionListener(e -> createPO());
        form.add(btnCreate);

        form.add(UIConstants.createLabel("Status:"));
        form.add(cmbStatus);
        JButton btnUpdateStatus = UIConstants.createStyledButton("Update Status", UIConstants.ACCENT_BLUE);
        btnUpdateStatus.addActionListener(e -> updateStatus());
        form.add(btnUpdateStatus);

        add(form, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new String[] { "PO ID", "Supplier", "Branch", "Date", "Amount", "Status", "Notes" }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(tableModel);
        add(UIConstants.createStyledScrollPane(table), BorderLayout.CENTER);
    }

    @Override
    public void refresh() {
        Supplier selectedSupplier = (Supplier) cmbSupplier.getSelectedItem();
        Branch selectedBranch = (Branch) cmbBranch.getSelectedItem();

        cmbSupplier.removeAllItems();
        cmbBranch.removeAllItems();

        for (Supplier s : service.getAllSuppliers())
            cmbSupplier.addItem(s);
        for (Branch b : service.getAllBranches())
            cmbBranch.addItem(b);

        if (selectedSupplier != null) {
            for (int i = 0; i < cmbSupplier.getItemCount(); i++) {
                if (cmbSupplier.getItemAt(i).getSupplierId() == selectedSupplier.getSupplierId()) {
                    cmbSupplier.setSelectedIndex(i);
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
        for (PurchaseOrder po : service.getAllPurchaseOrders())
            tableModel.addRow(new Object[] { po.getPoId(), po.getSupplierName(), po.getBranchName(),
                    po.getOrderDate(), String.format("%.2f", po.getTotalAmount()), po.getStatus(), po.getNotes() });
    }

    private void createPO() {
        Supplier s = (Supplier) cmbSupplier.getSelectedItem();
        Branch b = (Branch) cmbBranch.getSelectedItem();
        if (s == null || b == null) {
            msg("Select supplier and branch.");
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(txtAmount.getText().trim());
        } catch (NumberFormatException ex) {
            msg("Invalid amount.");
            return;
        }

        PurchaseOrder po = new PurchaseOrder();
        po.setSupplierId(s.getSupplierId());
        po.setBranchId(b.getBranchId());
        po.setOrderDate(Date.valueOf(LocalDate.now()));
        po.setTotalAmount(amount);
        po.setStatus("Pending");
        po.setNotes(txtNotes.getText().trim());
        po.setCreatedBy(SessionManager.getUserId());

        int id = service.addPurchaseOrder(po);
        if (id > 0) {
            msg("PO #" + id + " created.");
            EventBus.publish("PO_CHANGED");
        } else
            msg("Failed.");
    }

    private void updateStatus() {
        int row = table.getSelectedRow();
        if (row < 0) {
            msg("Select a PO.");
            return;
        }
        int poId = (int) tableModel.getValueAt(row, 0);
        String status = (String) cmbStatus.getSelectedItem();
        if (service.updatePOStatus(poId, status)) {
            msg("Status updated.");
            EventBus.publish("PO_CHANGED");
        } else
            msg("Failed.");
    }

    private void msg(String m) {
        JOptionPane.showMessageDialog(this, m);
    }
}
