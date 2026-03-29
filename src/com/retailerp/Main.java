package com.retailerp;

import com.retailerp.ui.LoginFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Set system look and feel tweaks
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.put("OptionPane.background", new java.awt.Color(0x2A2A40));
            UIManager.put("Panel.background", new java.awt.Color(0x2A2A40));
            UIManager.put("OptionPane.messageForeground", java.awt.Color.WHITE);
            UIManager.put("Button.background", new java.awt.Color(0x4A90D9));
            UIManager.put("Button.foreground", java.awt.Color.WHITE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
