package com.retailerp.dao;

import com.retailerp.model.AuditLog;
import com.retailerp.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuditLogDAO {

    public List<AuditLog> getAll() {
        List<AuditLog> list = new ArrayList<>();
        String sql = "SELECT a.*, u.username AS user_name FROM audit_logs a " +
                "LEFT JOIN users u ON a.user_id = u.user_id ORDER BY a.log_id DESC";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                AuditLog log = new AuditLog();
                log.setLogId(rs.getInt("log_id"));
                log.setUserId(rs.getInt("user_id"));
                log.setAction(rs.getString("action"));
                log.setTableName(rs.getString("table_name"));
                log.setRecordId(rs.getInt("record_id"));
                log.setOldValue(rs.getString("old_value"));
                log.setNewValue(rs.getString("new_value"));
                log.setDescription(rs.getString("description"));
                log.setCreatedAt(rs.getTimestamp("created_at"));
                log.setUserName(rs.getString("user_name"));
                list.add(log);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<AuditLog> getRecent(int limit) {
        List<AuditLog> list = new ArrayList<>();
        String sql = "SELECT a.*, u.username AS user_name FROM audit_logs a " +
                "LEFT JOIN users u ON a.user_id = u.user_id ORDER BY a.log_id DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                AuditLog log = new AuditLog();
                log.setLogId(rs.getInt("log_id"));
                log.setUserId(rs.getInt("user_id"));
                log.setAction(rs.getString("action"));
                log.setTableName(rs.getString("table_name"));
                log.setRecordId(rs.getInt("record_id"));
                log.setOldValue(rs.getString("old_value"));
                log.setNewValue(rs.getString("new_value"));
                log.setDescription(rs.getString("description"));
                log.setCreatedAt(rs.getTimestamp("created_at"));
                log.setUserName(rs.getString("user_name"));
                list.add(log);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(AuditLog log) {
        String sql = "INSERT INTO audit_logs (user_id, action, table_name, record_id, old_value, new_value, description) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, log.getUserId());
            ps.setString(2, log.getAction());
            ps.setString(3, log.getTableName());
            ps.setInt(4, log.getRecordId());
            ps.setString(5, log.getOldValue());
            ps.setString(6, log.getNewValue());
            ps.setString(7, log.getDescription());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
