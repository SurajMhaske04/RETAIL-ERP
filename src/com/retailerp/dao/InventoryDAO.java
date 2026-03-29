package com.retailerp.dao;

import com.retailerp.model.Inventory;
import com.retailerp.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryDAO {

    public List<Inventory> getAll() {
        List<Inventory> list = new ArrayList<>();
        String sql = "SELECT i.*, p.product_name, b.branch_name FROM inventory i " +
                "JOIN products p ON i.product_id = p.product_id " +
                "JOIN branches b ON i.branch_id = b.branch_id ORDER BY i.inventory_id";
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

    public List<Inventory> getByBranch(int branchId) {
        List<Inventory> list = new ArrayList<>();
        String sql = "SELECT i.*, p.product_name, b.branch_name FROM inventory i " +
                "JOIN products p ON i.product_id = p.product_id " +
                "JOIN branches b ON i.branch_id = b.branch_id WHERE i.branch_id = ? ORDER BY p.product_name";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Inventory> getLowStock() {
        List<Inventory> list = new ArrayList<>();
        String sql = "SELECT i.*, p.product_name, b.branch_name FROM inventory i " +
                "JOIN products p ON i.product_id = p.product_id " +
                "JOIN branches b ON i.branch_id = b.branch_id WHERE i.quantity <= i.min_stock ORDER BY i.quantity";
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

    public int getStock(int productId, int branchId) {
        String sql = "SELECT quantity FROM inventory WHERE product_id = ? AND branch_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.setInt(2, branchId);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt("quantity");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean upsertStock(int productId, int branchId, int quantity, int minStock) {
        String sql = "INSERT INTO inventory (product_id, branch_id, quantity, min_stock) VALUES (?,?,?,?) " +
                "ON DUPLICATE KEY UPDATE quantity = ?, min_stock = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.setInt(2, branchId);
            ps.setInt(3, quantity);
            ps.setInt(4, minStock);
            ps.setInt(5, quantity);
            ps.setInt(6, minStock);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean reduceStock(int productId, int branchId, int qty) {
        try (Connection conn = DBConnection.getConnection()) {
            return reduceStock(productId, branchId, qty, conn);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean reduceStock(int productId, int branchId, int qty, Connection conn) throws SQLException {
        String sql = "UPDATE inventory SET quantity = quantity - ? WHERE product_id = ? AND branch_id = ? AND quantity >= ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, qty);
            ps.setInt(2, productId);
            ps.setInt(3, branchId);
            ps.setInt(4, qty);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean addStock(int productId, int branchId, int qty) {

        String sql = "UPDATE inventory SET quantity = quantity + ? WHERE product_id = ? AND branch_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, qty);
            ps.setInt(2, productId);
            ps.setInt(3, branchId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int inventoryId) {
        String sql = "DELETE FROM inventory WHERE inventory_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, inventoryId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Inventory mapRow(ResultSet rs) throws SQLException {
        Inventory inv = new Inventory();
        inv.setInventoryId(rs.getInt("inventory_id"));
        inv.setProductId(rs.getInt("product_id"));
        inv.setBranchId(rs.getInt("branch_id"));
        inv.setQuantity(rs.getInt("quantity"));
        inv.setMinStock(rs.getInt("min_stock"));
        inv.setUpdatedAt(rs.getTimestamp("updated_at"));
        inv.setProductName(rs.getString("product_name"));
        inv.setBranchName(rs.getString("branch_name"));
        return inv;
    }
}
