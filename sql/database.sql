-- ============================================================
-- Multi-Branch Retail ERP System - Full Database Schema
-- MySQL 8.x compatible
-- ============================================================

CREATE DATABASE IF NOT EXISTS retail_erp;
USE retail_erp;

-- ============================================================
-- 1. ROLES
-- ============================================================
CREATE TABLE roles (
    role_id     INT AUTO_INCREMENT PRIMARY KEY,
    role_name   VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

INSERT INTO roles (role_name, description) VALUES
('Admin',   'Full system access'),
('Manager', 'Branch-level management'),
('Staff',   'Point-of-sale and basic operations');

-- ============================================================
-- 2. BRANCHES
-- ============================================================
CREATE TABLE branches (
    branch_id   INT AUTO_INCREMENT PRIMARY KEY,
    branch_name VARCHAR(100) NOT NULL,
    address     VARCHAR(255),
    city        VARCHAR(100),
    state       VARCHAR(100),
    phone       VARCHAR(20),
    is_active   BOOLEAN DEFAULT TRUE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

INSERT INTO branches (branch_name, address, city, state, phone) VALUES
('Main Branch', '123 Market Road', 'Mumbai', 'Maharashtra', '9876543210');

-- ============================================================
-- 3. USERS (Login accounts)
-- ============================================================
CREATE TABLE users (
    user_id    INT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(50) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    full_name  VARCHAR(100) NOT NULL,
    role_id    INT NOT NULL,
    branch_id  INT,
    is_active  BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id)   REFERENCES roles(role_id),
    FOREIGN KEY (branch_id) REFERENCES branches(branch_id)
) ENGINE=InnoDB;

-- Default admin user  (password: admin123 — plain text for demo)
INSERT INTO users (username, password, full_name, role_id, branch_id) VALUES
('admin', 'admin123', 'System Administrator', 1, 1);

-- ============================================================
-- 4. EMPLOYEES
-- ============================================================
CREATE TABLE employees (
    employee_id  INT AUTO_INCREMENT PRIMARY KEY,
    first_name   VARCHAR(50) NOT NULL,
    last_name    VARCHAR(50) NOT NULL,
    email        VARCHAR(100),
    phone        VARCHAR(20),
    address      VARCHAR(255),
    role_id      INT NOT NULL,
    branch_id    INT NOT NULL,
    salary       DECIMAL(12,2) DEFAULT 0.00,
    join_date    DATE,
    is_active    BOOLEAN DEFAULT TRUE,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id)   REFERENCES roles(role_id),
    FOREIGN KEY (branch_id) REFERENCES branches(branch_id)
) ENGINE=InnoDB;

-- ============================================================
-- 5. CATEGORIES
-- ============================================================
CREATE TABLE categories (
    category_id   INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL UNIQUE,
    description   VARCHAR(255),
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

INSERT INTO categories (category_name, description) VALUES
('Electronics', 'Electronic items and gadgets'),
('Groceries',   'Daily grocery items'),
('Clothing',    'Apparel and garments');

-- ============================================================
-- 6. PRODUCTS
-- ============================================================
CREATE TABLE products (
    product_id   INT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(150) NOT NULL,
    category_id  INT,
    brand        VARCHAR(100),
    unit         VARCHAR(30) DEFAULT 'pcs',
    cost_price   DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    sell_price   DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    gst_percent  DECIMAL(5,2) DEFAULT 18.00,
    description  VARCHAR(255),
    is_active    BOOLEAN DEFAULT TRUE,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(category_id)
) ENGINE=InnoDB;

-- ============================================================
-- 7. INVENTORY (branch-wise stock)
-- ============================================================
CREATE TABLE inventory (
    inventory_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id   INT NOT NULL,
    branch_id    INT NOT NULL,
    quantity     INT NOT NULL DEFAULT 0,
    min_stock    INT DEFAULT 10,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_product_branch (product_id, branch_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    FOREIGN KEY (branch_id)  REFERENCES branches(branch_id)
) ENGINE=InnoDB;

-- ============================================================
-- 8. CUSTOMERS
-- ============================================================
CREATE TABLE customers (
    customer_id    INT AUTO_INCREMENT PRIMARY KEY,
    customer_name  VARCHAR(100) NOT NULL,
    phone          VARCHAR(20),
    email          VARCHAR(100),
    address        VARCHAR(255),
    loyalty_points INT DEFAULT 0,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================================
-- 9. SUPPLIERS
-- ============================================================
CREATE TABLE suppliers (
    supplier_id   INT AUTO_INCREMENT PRIMARY KEY,
    supplier_name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(100),
    phone         VARCHAR(20),
    email         VARCHAR(100),
    address       VARCHAR(255),
    gst_number    VARCHAR(20),
    is_active     BOOLEAN DEFAULT TRUE,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================================
-- 10. PURCHASE ORDERS
-- ============================================================
CREATE TABLE purchase_orders (
    po_id          INT AUTO_INCREMENT PRIMARY KEY,
    supplier_id    INT NOT NULL,
    branch_id      INT NOT NULL,
    order_date     DATE NOT NULL,
    total_amount   DECIMAL(14,2) DEFAULT 0.00,
    status         VARCHAR(30) DEFAULT 'Pending',
    notes          VARCHAR(255),
    created_by     INT,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id),
    FOREIGN KEY (branch_id)   REFERENCES branches(branch_id),
    FOREIGN KEY (created_by)  REFERENCES users(user_id)
) ENGINE=InnoDB;

CREATE TABLE purchase_order_items (
    poi_id       INT AUTO_INCREMENT PRIMARY KEY,
    po_id        INT NOT NULL,
    product_id   INT NOT NULL,
    quantity     INT NOT NULL DEFAULT 0,
    unit_price   DECIMAL(12,2) DEFAULT 0.00,
    total_price  DECIMAL(14,2) DEFAULT 0.00,
    FOREIGN KEY (po_id)      REFERENCES purchase_orders(po_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
) ENGINE=InnoDB;

-- ============================================================
-- 11. SALES
-- ============================================================
CREATE TABLE sales (
    sale_id        INT AUTO_INCREMENT PRIMARY KEY,
    invoice_number VARCHAR(30) NOT NULL UNIQUE,
    branch_id      INT NOT NULL,
    customer_id    INT,
    user_id        INT NOT NULL,
    sale_date      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    subtotal       DECIMAL(14,2) DEFAULT 0.00,
    gst_amount     DECIMAL(14,2) DEFAULT 0.00,
    discount       DECIMAL(14,2) DEFAULT 0.00,
    total_amount   DECIMAL(14,2) DEFAULT 0.00,
    payment_mode   VARCHAR(30) DEFAULT 'Cash',
    FOREIGN KEY (branch_id)  REFERENCES branches(branch_id),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (user_id)    REFERENCES users(user_id)
) ENGINE=InnoDB;

-- ============================================================
-- 12. SALES ITEMS
-- ============================================================
CREATE TABLE sales_items (
    item_id      INT AUTO_INCREMENT PRIMARY KEY,
    sale_id      INT NOT NULL,
    product_id   INT NOT NULL,
    quantity     INT NOT NULL DEFAULT 1,
    unit_price   DECIMAL(12,2) DEFAULT 0.00,
    gst_percent  DECIMAL(5,2) DEFAULT 18.00,
    gst_amount   DECIMAL(12,2) DEFAULT 0.00,
    discount     DECIMAL(12,2) DEFAULT 0.00,
    total_price  DECIMAL(14,2) DEFAULT 0.00,
    FOREIGN KEY (sale_id)    REFERENCES sales(sale_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
) ENGINE=InnoDB;

-- ============================================================
-- 13. STOCK TRANSFERS
-- ============================================================
CREATE TABLE stock_transfers (
    transfer_id      INT AUTO_INCREMENT PRIMARY KEY,
    product_id       INT NOT NULL,
    from_branch_id   INT NOT NULL,
    to_branch_id     INT NOT NULL,
    quantity         INT NOT NULL,
    transfer_date    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    transferred_by   INT,
    notes            VARCHAR(255),
    FOREIGN KEY (product_id)     REFERENCES products(product_id),
    FOREIGN KEY (from_branch_id) REFERENCES branches(branch_id),
    FOREIGN KEY (to_branch_id)   REFERENCES branches(branch_id),
    FOREIGN KEY (transferred_by) REFERENCES users(user_id)
) ENGINE=InnoDB;

-- ============================================================
-- 14. AUDIT LOGS
-- ============================================================
CREATE TABLE audit_logs (
    log_id      INT AUTO_INCREMENT PRIMARY KEY,
    user_id     INT,
    action      VARCHAR(50) NOT NULL,
    table_name  VARCHAR(50),
    record_id   INT,
    old_value   TEXT,
    new_value   TEXT,
    description VARCHAR(255),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
) ENGINE=InnoDB;

-- ============================================================
-- END OF SCHEMA
-- ============================================================
