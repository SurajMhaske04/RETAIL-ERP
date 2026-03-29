package com.retailerp.dao;

import com.retailerp.model.User;
import com.retailerp.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User authenticate(String username, String password) {
        String sql = "SELECT u.*, r.role_name, b.branch_name FROM users u " +
                "JOIN roles r ON u.role_id = r.role_id " +
                "LEFT JOIN branches b ON u.branch_id = b.branch_id " +
                "WHERE u.username = ? AND u.password = ? AND u.is_active = TRUE";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return mapRow(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<User> getAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.*, r.role_name, b.branch_name FROM users u " +
                "JOIN roles r ON u.role_id = r.role_id " +
                "LEFT JOIN branches b ON u.branch_id = b.branch_id ORDER BY u.user_id";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(User u) {
        String sql = "INSERT INTO users (username, password, full_name, role_id, branch_id, is_active) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getFullName());
            ps.setInt(4, u.getRoleId());
            ps.setInt(5, u.getBranchId());
            ps.setBoolean(6, u.isActive());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(User u) {
        String sql = "UPDATE users SET username=?, password=?, full_name=?, role_id=?, branch_id=?, is_active=? WHERE user_id=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getFullName());
            ps.setInt(4, u.getRoleId());
            ps.setInt(5, u.getBranchId());
            ps.setBoolean(6, u.isActive());
            ps.setInt(7, u.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setFullName(rs.getString("full_name"));
        u.setRoleId(rs.getInt("role_id"));
        u.setBranchId(rs.getInt("branch_id"));
        u.setActive(rs.getBoolean("is_active"));
        u.setCreatedAt(rs.getTimestamp("created_at"));
        u.setRoleName(rs.getString("role_name"));
        u.setBranchName(rs.getString("branch_name"));
        return u;
    }
}
