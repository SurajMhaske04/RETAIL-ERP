package com.retailerp.dao;

import com.retailerp.model.Sale;
import com.retailerp.model.SaleItem;
import com.retailerp.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SaleDAO {

    public List<Sale> getAll() {
        List<Sale> list = new ArrayList<>();
        String sql = "SELECT s.*, c.customer_name, b.branch_name FROM sales s " +
                "LEFT JOIN customers c ON s.customer_id = c.customer_id " +
                "JOIN branches b ON s.branch_id = b.branch_id ORDER BY s.sale_id DESC";
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

    public int insert(Sale sale) {
        try (Connection conn = DBConnection.getConnection()) {
            return insert(sale, conn);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int insert(Sale sale, Connection conn) throws SQLException {
        String sql = "INSERT INTO sales (invoice_number, branch_id, customer_id, user_id, subtotal, gst_amount, discount, total_amount, payment_mode) VALUES (?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, sale.getInvoiceNumber());
            ps.setInt(2, sale.getBranchId());
            if (sale.getCustomerId() > 0)
                ps.setInt(3, sale.getCustomerId());
            else
                ps.setNull(3, Types.INTEGER);
            ps.setInt(4, sale.getUserId());
            ps.setDouble(5, sale.getSubtotal());
            ps.setDouble(6, sale.getGstAmount());
            ps.setDouble(7, sale.getDiscount());
            ps.setDouble(8, sale.getTotalAmount());
            ps.setString(9, sale.getPaymentMode());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next())
                return keys.getInt(1);
        }
        return -1;
    }

    public boolean insertItem(SaleItem item) {
        try (Connection conn = DBConnection.getConnection()) {
            return insertItem(item, conn);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertItem(SaleItem item, Connection conn) throws SQLException {
        String sql = "INSERT INTO sales_items (sale_id, product_id, quantity, unit_price, gst_percent, gst_amount, discount, total_price) VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, item.getSaleId());
            ps.setInt(2, item.getProductId());
            ps.setInt(3, item.getQuantity());
            ps.setDouble(4, item.getUnitPrice());
            ps.setDouble(5, item.getGstPercent());
            ps.setDouble(6, item.getGstAmount());
            ps.setDouble(7, item.getDiscount());
            ps.setDouble(8, item.getTotalPrice());
            return ps.executeUpdate() > 0;
        }
    }

    public List<SaleItem> getItemsBySaleId(int saleId) {
        List<SaleItem> list = new ArrayList<>();
        String sql = "SELECT si.*, p.product_name FROM sales_items si " +
                "JOIN products p ON si.product_id = p.product_id WHERE si.sale_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, saleId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                SaleItem item = new SaleItem();
                item.setItemId(rs.getInt("item_id"));
                item.setSaleId(rs.getInt("sale_id"));
                item.setProductId(rs.getInt("product_id"));
                item.setQuantity(rs.getInt("quantity"));
                item.setUnitPrice(rs.getDouble("unit_price"));
                item.setGstPercent(rs.getDouble("gst_percent"));
                item.setGstAmount(rs.getDouble("gst_amount"));
                item.setDiscount(rs.getDouble("discount"));
                item.setTotalPrice(rs.getDouble("total_price"));
                item.setProductName(rs.getString("product_name"));
                list.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public double getTotalSales() {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM sales";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next())
                return rs.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double getTodaySales() {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM sales WHERE DATE(sale_date) = CURDATE()";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next())
                return rs.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double getMonthlySales() {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM sales WHERE MONTH(sale_date) = MONTH(CURDATE()) AND YEAR(sale_date) = YEAR(CURDATE())";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next())
                return rs.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String generateInvoiceNumber() {
        String sql = "SELECT MAX(sale_id) FROM sales";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            int next = 1;
            if (rs.next())
                next = rs.getInt(1) + 1;
            return String.format("INV-%06d", next);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "INV-000001";
    }

    public List<Object[]> getTopSellingProducts(int limit) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT p.product_name, SUM(si.quantity) AS total_qty, SUM(si.total_price) AS total_revenue " +
                "FROM sales_items si JOIN products p ON si.product_id = p.product_id " +
                "GROUP BY p.product_name ORDER BY total_qty DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[] { rs.getString("product_name"), rs.getInt("total_qty"),
                        rs.getDouble("total_revenue") });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Object[]> getDailySales(int days) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT DATE(sale_date) AS sale_day, COUNT(*) AS num_sales, SUM(total_amount) AS revenue " +
                "FROM sales WHERE sale_date >= DATE_SUB(CURDATE(), INTERVAL ? DAY) " +
                "GROUP BY sale_day ORDER BY sale_day DESC";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, days);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[] { rs.getString("sale_day"), rs.getInt("num_sales"), rs.getDouble("revenue") });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Sale mapRow(ResultSet rs) throws SQLException {
        Sale s = new Sale();
        s.setSaleId(rs.getInt("sale_id"));
        s.setInvoiceNumber(rs.getString("invoice_number"));
        s.setBranchId(rs.getInt("branch_id"));
        s.setCustomerId(rs.getInt("customer_id"));
        s.setUserId(rs.getInt("user_id"));
        s.setSaleDate(rs.getTimestamp("sale_date"));
        s.setSubtotal(rs.getDouble("subtotal"));
        s.setGstAmount(rs.getDouble("gst_amount"));
        s.setDiscount(rs.getDouble("discount"));
        s.setTotalAmount(rs.getDouble("total_amount"));
        s.setPaymentMode(rs.getString("payment_mode"));
        s.setCustomerName(rs.getString("customer_name"));
        s.setBranchName(rs.getString("branch_name"));
        return s;
    }
}
