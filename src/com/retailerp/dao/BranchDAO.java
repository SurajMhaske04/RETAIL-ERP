package com.retailerp.dao;

import com.retailerp.model.Branch;
import com.retailerp.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BranchDAO {

    public List<Branch> getAll() {
        List<Branch> list = new ArrayList<>();
        String sql = "SELECT * FROM branches ORDER BY branch_id";
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

    public boolean insert(Branch b) {
        String sql = "INSERT INTO branches (branch_name, address, city, state, phone, is_active) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, b.getBranchName());
            ps.setString(2, b.getAddress());
            ps.setString(3, b.getCity());
            ps.setString(4, b.getState());
            ps.setString(5, b.getPhone());
            ps.setBoolean(6, b.isActive());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Branch b) {
        String sql = "UPDATE branches SET branch_name=?, address=?, city=?, state=?, phone=?, is_active=? WHERE branch_id=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, b.getBranchName());
            ps.setString(2, b.getAddress());
            ps.setString(3, b.getCity());
            ps.setString(4, b.getState());
            ps.setString(5, b.getPhone());
            ps.setBoolean(6, b.isActive());
            ps.setInt(7, b.getBranchId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int branchId) {
        String sql = "DELETE FROM branches WHERE branch_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Branch mapRow(ResultSet rs) throws SQLException {
        Branch b = new Branch();
        b.setBranchId(rs.getInt("branch_id"));
        b.setBranchName(rs.getString("branch_name"));
        b.setAddress(rs.getString("address"));
        b.setCity(rs.getString("city"));
        b.setState(rs.getString("state"));
        b.setPhone(rs.getString("phone"));
        b.setActive(rs.getBoolean("is_active"));
        b.setCreatedAt(rs.getTimestamp("created_at"));
        return b;
    }
}
