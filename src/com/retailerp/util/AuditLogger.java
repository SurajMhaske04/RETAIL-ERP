package com.retailerp.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AuditLogger {
    public static void log(int userId, String action, String tableName, int recordId, String oldVal, String newVal, String desc) {
        String sql = "INSERT INTO audit_logs (user_id, action, table_name, record_id, old_value, new_value, description) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, action);
            ps.setString(3, tableName);
            ps.setInt(4, recordId);
            ps.setString(5, oldVal);
            ps.setString(6, newVal);
            ps.setString(7, desc);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
