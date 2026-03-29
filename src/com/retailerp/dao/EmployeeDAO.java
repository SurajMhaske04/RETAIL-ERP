package com.retailerp.dao;

import com.retailerp.model.Employee;
import com.retailerp.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    public List<Employee> getAll() {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT e.*, r.role_name, b.branch_name FROM employees e " +
                "JOIN roles r ON e.role_id = r.role_id " +
                "JOIN branches b ON e.branch_id = b.branch_id ORDER BY e.employee_id";
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

    public boolean insert(Employee emp) {
        String sql = "INSERT INTO employees (first_name, last_name, email, phone, address, role_id, branch_id, salary, join_date, is_active) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, emp.getFirstName());
            ps.setString(2, emp.getLastName());
            ps.setString(3, emp.getEmail());
            ps.setString(4, emp.getPhone());
            ps.setString(5, emp.getAddress());
            ps.setInt(6, emp.getRoleId());
            ps.setInt(7, emp.getBranchId());
            ps.setDouble(8, emp.getSalary());
            ps.setDate(9, emp.getJoinDate());
            ps.setBoolean(10, emp.isActive());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Employee emp) {
        String sql = "UPDATE employees SET first_name=?, last_name=?, email=?, phone=?, address=?, role_id=?, branch_id=?, salary=?, join_date=?, is_active=? WHERE employee_id=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, emp.getFirstName());
            ps.setString(2, emp.getLastName());
            ps.setString(3, emp.getEmail());
            ps.setString(4, emp.getPhone());
            ps.setString(5, emp.getAddress());
            ps.setInt(6, emp.getRoleId());
            ps.setInt(7, emp.getBranchId());
            ps.setDouble(8, emp.getSalary());
            ps.setDate(9, emp.getJoinDate());
            ps.setBoolean(10, emp.isActive());
            ps.setInt(11, emp.getEmployeeId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int employeeId) {
        String sql = "DELETE FROM employees WHERE employee_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Employee mapRow(ResultSet rs) throws SQLException {
        Employee e = new Employee();
        e.setEmployeeId(rs.getInt("employee_id"));
        e.setFirstName(rs.getString("first_name"));
        e.setLastName(rs.getString("last_name"));
        e.setEmail(rs.getString("email"));
        e.setPhone(rs.getString("phone"));
        e.setAddress(rs.getString("address"));
        e.setRoleId(rs.getInt("role_id"));
        e.setBranchId(rs.getInt("branch_id"));
        e.setSalary(rs.getDouble("salary"));
        e.setJoinDate(rs.getDate("join_date"));
        e.setActive(rs.getBoolean("is_active"));
        e.setCreatedAt(rs.getTimestamp("created_at"));
        e.setRoleName(rs.getString("role_name"));
        e.setBranchName(rs.getString("branch_name"));
        return e;
    }
}
