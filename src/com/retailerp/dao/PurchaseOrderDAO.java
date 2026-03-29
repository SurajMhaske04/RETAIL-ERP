package com.retailerp.dao;

import com.retailerp.model.PurchaseOrder;
import com.retailerp.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PurchaseOrderDAO {

    public List<PurchaseOrder> getAll() {
        List<PurchaseOrder> list = new ArrayList<>();
        String sql = "SELECT po.*, s.supplier_name, b.branch_name FROM purchase_orders po " +
                "JOIN suppliers s ON po.supplier_id = s.supplier_id " +
                "JOIN branches b ON po.branch_id = b.branch_id ORDER BY po.po_id DESC";
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

    public int insert(PurchaseOrder po) {
        String sql = "INSERT INTO purchase_orders (supplier_id, branch_id, order_date, total_amount, status, notes, created_by) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, po.getSupplierId());
            ps.setInt(2, po.getBranchId());
            ps.setDate(3, po.getOrderDate());
            ps.setDouble(4, po.getTotalAmount());
            ps.setString(5, po.getStatus());
            ps.setString(6, po.getNotes());
            ps.setInt(7, po.getCreatedBy());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next())
                return keys.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean updateStatus(int poId, String status) {
        String sql = "UPDATE purchase_orders SET status = ? WHERE po_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, poId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int poId) {
        String sql = "DELETE FROM purchase_orders WHERE po_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, poId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private PurchaseOrder mapRow(ResultSet rs) throws SQLException {
        PurchaseOrder po = new PurchaseOrder();
        po.setPoId(rs.getInt("po_id"));
        po.setSupplierId(rs.getInt("supplier_id"));
        po.setBranchId(rs.getInt("branch_id"));
        po.setOrderDate(rs.getDate("order_date"));
        po.setTotalAmount(rs.getDouble("total_amount"));
        po.setStatus(rs.getString("status"));
        po.setNotes(rs.getString("notes"));
        po.setCreatedBy(rs.getInt("created_by"));
        po.setCreatedAt(rs.getTimestamp("created_at"));
        po.setSupplierName(rs.getString("supplier_name"));
        po.setBranchName(rs.getString("branch_name"));
        return po;
    }
}
