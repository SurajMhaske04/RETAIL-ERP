package com.retailerp.dao;

import com.retailerp.model.Product;
import com.retailerp.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public List<Product> getAll() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name FROM products p " +
                "LEFT JOIN categories c ON p.category_id = c.category_id ORDER BY p.product_id";
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

    public List<Product> getByBranch(int branchId) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name FROM products p " +
                "LEFT JOIN categories c ON p.category_id = c.category_id " +
                "JOIN inventory i ON p.product_id = i.product_id " +
                "WHERE i.branch_id = ? AND p.is_active = TRUE ORDER BY p.product_name";
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

    public Product getById(int productId) {
        String sql = "SELECT p.*, c.category_name FROM products p " +
                "LEFT JOIN categories c ON p.category_id = c.category_id WHERE p.product_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return mapRow(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(Product p) {
        String sql = "INSERT INTO products (product_name, category_id, brand, unit, cost_price, sell_price, gst_percent, description, is_active) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getProductName());
            ps.setInt(2, p.getCategoryId());
            ps.setString(3, p.getBrand());
            ps.setString(4, p.getUnit());
            ps.setDouble(5, p.getCostPrice());
            ps.setDouble(6, p.getSellPrice());
            ps.setDouble(7, p.getGstPercent());
            ps.setString(8, p.getDescription());
            ps.setBoolean(9, p.isActive());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Product p) {
        String sql = "UPDATE products SET product_name=?, category_id=?, brand=?, unit=?, cost_price=?, sell_price=?, gst_percent=?, description=?, is_active=? WHERE product_id=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getProductName());
            ps.setInt(2, p.getCategoryId());
            ps.setString(3, p.getBrand());
            ps.setString(4, p.getUnit());
            ps.setDouble(5, p.getCostPrice());
            ps.setDouble(6, p.getSellPrice());
            ps.setDouble(7, p.getGstPercent());
            ps.setString(8, p.getDescription());
            ps.setBoolean(9, p.isActive());
            ps.setInt(10, p.getProductId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int productId) {
        String sql = "DELETE FROM products WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getCount() {
        String sql = "SELECT COUNT(*) FROM products WHERE is_active = TRUE";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Product mapRow(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setProductId(rs.getInt("product_id"));
        p.setProductName(rs.getString("product_name"));
        p.setCategoryId(rs.getInt("category_id"));
        p.setBrand(rs.getString("brand"));
        p.setUnit(rs.getString("unit"));
        p.setCostPrice(rs.getDouble("cost_price"));
        p.setSellPrice(rs.getDouble("sell_price"));
        p.setGstPercent(rs.getDouble("gst_percent"));
        p.setDescription(rs.getString("description"));
        p.setActive(rs.getBoolean("is_active"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        p.setCategoryName(rs.getString("category_name"));
        return p;
    }
}
