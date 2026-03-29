package com.retailerp.ui;

import com.retailerp.model.User;
import com.retailerp.service.ERPService;
import com.retailerp.util.SessionManager;
import com.retailerp.util.UIConstants;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private final ERPService service = new ERPService();

    public LoginFrame() {
        setTitle("Retail ERP — Login");
        setSize(460, 380);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UIConstants.BG_DARK);
        initUI();
    }

    private void initUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel lblTitle = new JLabel("🔒  Retail ERP Login", SwingConstants.CENTER);
        lblTitle.setFont(UIConstants.FONT_HEADER);
        lblTitle.setForeground(UIConstants.ACCENT_BLUE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(lblTitle, gbc);

        // Spacer
        gbc.gridy = 1;
        add(Box.createVerticalStrut(10), gbc);

        // Username
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(UIConstants.createLabel("Username"), gbc);

        txtUsername = UIConstants.createStyledTextField();
        txtUsername.setPreferredSize(new Dimension(220, 34));
        gbc.gridx = 1;
        add(txtUsername, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(UIConstants.createLabel("Password"), gbc);

        txtPassword = UIConstants.createStyledPasswordField();
        txtPassword.setPreferredSize(new Dimension(220, 34));
        gbc.gridx = 1;
        add(txtPassword, gbc);

        // Login button
        JButton btnLogin = UIConstants.createStyledButton("Login", UIConstants.ACCENT_BLUE);
        btnLogin.setPreferredSize(new Dimension(220, 38));
        btnLogin.addActionListener(e -> doLogin());
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 12, 8, 12);
        add(btnLogin, gbc);

        // Enter key support
        getRootPane().setDefaultButton(btnLogin);

        // Footer
        JLabel footer = new JLabel("© 2026 RetailERP System", SwingConstants.CENTER);
        footer.setFont(UIConstants.FONT_SMALL);
        footer.setForeground(UIConstants.TEXT_SECONDARY);
        gbc.gridy = 5;
        gbc.insets = new Insets(15, 12, 8, 12);
        add(footer, gbc);
    }

    private void doLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.", "Validation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = service.authenticate(username, password);
        if (user != null) {
            SessionManager.setCurrentUser(user);
            dispose();
            new MainFrame().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials or inactive account.", "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
