package com.retailerp.ui.panels;

import com.retailerp.model.AuditLog;
import com.retailerp.service.ERPService;
import com.retailerp.util.Refreshable;
import com.retailerp.util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AuditLogPanel extends JPanel implements Refreshable {
    private final ERPService service = new ERPService();
    private DefaultTableModel tableModel;

    public AuditLogPanel() {
        setBackground(UIConstants.BG_DARK);
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        buildUI();
        refresh();
    }

    private void buildUI() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.setBackground(UIConstants.PANEL_BG);
        topPanel.setBorder(new EmptyBorder(8, 10, 8, 10));

        JButton btnRefresh = UIConstants.createStyledButton("Refresh", UIConstants.ACCENT_BLUE);
        btnRefresh.addActionListener(e -> refresh());
        topPanel.add(UIConstants.createLabel("Audit Trail — All major system actions are logged here."));
        topPanel.add(btnRefresh);
        add(topPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new String[] { "Log ID", "User", "Action", "Table", "Record ID", "Description", "Timestamp" }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        add(UIConstants.createStyledScrollPane(table), BorderLayout.CENTER);
    }

    @Override
    public void refresh() {
        tableModel.setRowCount(0);
        for (AuditLog log : service.getAllAuditLogs())
            tableModel.addRow(new Object[] { log.getLogId(), log.getUserName(), log.getAction(),
                    log.getTableName(), log.getRecordId(), log.getDescription(), log.getCreatedAt() });
    }
}
