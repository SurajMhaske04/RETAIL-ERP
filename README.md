# 🏪 RetailERP – Multi-Branch Retail Management System

RetailERP is a modern **Java Swing–based desktop ERP application** designed to manage multi-branch retail operations including billing, inventory, and user management.
The system demonstrates **clean layered architecture, JDBC–MySQL integration, role-based security, and real-time UI updates**.

This project is built as a **professional desktop ERP system** suitable for retail chains and also serves as a **strong portfolio project for Java developers**.

---

# ⭐ Key Highlights

* Multi-branch enterprise ERP system (real-world simulation)
* Atomic billing transactions with rollback support
* Event-driven UI updates using EventBus
* Role-Based Access Control (RBAC)
* Clean layered architecture (UI → Service → DAO → DB)

---

# 🚀 Features

## 💰 Point of Sale (Billing)

* Atomic billing transactions (safe rollback if failure occurs)
* Automatic invoice number generation
* Real-time revenue updates on dashboard
* Inventory deduction during checkout

## 📦 Inventory Management

* Multi-branch product inventory tracking
* Stock transfer between branches
* Prevents negative stock using database-level locking
* Product creation and modification

## 👥 User & Role Management

* Secure login system
* Role-Based Access Control (Admin, Manager, Staff)
* Branch-specific data access restriction
* Session-based user authentication

## 📊 Dashboard

* Global revenue tracking
* Inventory visibility across branches
* Real-time UI updates using EventBus

## 🧾 Audit Logging

* Automatic tracking of all system activities
* Records user actions with timestamps
* Useful for monitoring and accountability

---

# 🔐 Security Features

* PreparedStatement to prevent SQL Injection
* Role-Based Access Control (RBAC)
* Session-based authentication
* Controlled branch-level data access

---

# 🛠 Technologies Used

* Java (Core Java)
* Java Swing
* JDBC
* MySQL
* Event-Driven Architecture (EventBus)
* Git & GitHub

---

# 🗂 Project Structure

```
RetailERP/
├── bin/                # Compiled .class files
├── lib/                # External libraries (MySQL JDBC driver)
├── sql/                # Database schema & seed scripts
├── src/
│   └── com/retailerp/
│       ├── dao/        # Database queries
│       ├── model/      # Entity classes
│       ├── service/    # Business logic layer
│       ├── ui/         # Java Swing UI panels
│       └── util/       # Utility classes (DBConnection, EventBus, Session)
├── build.bat           # Windows build script
├── run.bat             # Windows launcher
├── Main.java           # Application entry point
└── README.md
```

This project follows **industry-standard layered architecture** with clear separation of concerns.

---

# ⚙️ Detailed Setup Guide

Follow these steps carefully to run the **RetailERP Desktop Application**.

---

# 🛠 Prerequisites

Before running the project, make sure the following software is installed.

## 1️⃣ Install Java (JDK 11 or Higher)

Verify installation:

```
java -version
javac -version
```

---

## 2️⃣ Install MySQL Server

Verify MySQL installation:

```
mysql -u root -p
```

---

## 3️⃣ Download MySQL JDBC Driver

Place the `.jar` file inside the **lib folder**:

```
RetailERP/
lib/
mysql-connector-j-9.x.x.jar
```

---

# 🗄 Database Setup

### Step 1 – Open Terminal

```
cd D:\RetailERP
```

### Step 2 – Run Database Script

```
mysql -u root -p < sql/retail_erp_schema.sql
```

### Step 3 – Verify Database

```
SHOW DATABASES;
USE retail_erp;
SHOW TABLES;
```

---

# ⚙️ Configure Database Connection

Open:

```
src/com/retailerp/util/DBConnection.java
```

Update:

```java
private static final String PASS = "your_password_here";
```

---

# 📦 Compile the Project

## Option 1 – Using Script

```
build.bat
```

## Option 2 – Manual

```
javac -d bin -cp "bin;lib/*" -sourcepath src src/com/retailerp/Main.java
```

---

# ▶️ Run the Application

## Method 1

```
run.bat
```

## Method 2

```
java -cp "bin;lib/*" com.retailerp.Main
```

---

# 🔐 Default Login Credentials

| Username | Password | Role  |
| -------- | -------- | ----- |
| admin    | admin123 | ADMIN |

---

# 🧪 First Test After Setup

1. Go to **Products Tab**
2. Add a new product
3. Go to **Billing Tab**
4. Create a sale
5. Return to **Dashboard**

✔ Revenue should update instantly

---

# 🧩 Code Architecture

## UI Layer → Service Layer → DAO Layer → Database

**UI Layer**
Handles all Swing interfaces

**Service Layer**
Business logic and validation

**DAO Layer**
Database operations using JDBC

**Database Layer**
MySQL relational database

---

# 📘 Key Java Concepts Used

* Java Swing layouts (BorderLayout, GridLayout)
* JDBC with PreparedStatement
* DAO Design Pattern
* Event-Driven Programming (EventBus)
* Database Transactions
* Role-Based Access Control

---

# 🎓 Learning Outcomes

* Strong understanding of Java Swing desktop development
* Practical experience with JDBC & MySQL
* Implementation of layered architecture
* Database transaction handling
* Real-world ERP system design

---

# 👨‍💻 Author

**Suraj Mhaske**
Computer Engineering Student

GitHub:
https://github.com/SurajMhaske04

---

# 📌 Resume Description

Developed a multi-branch Retail ERP system using Java Swing, JDBC, and MySQL with layered architecture, role-based access control, and real-time event-driven updates.

---

# 📌 Project Purpose

This project was developed to practice Java Swing, JDBC, and MySQL integration while implementing real-world enterprise architecture concepts.

It is designed as a portfolio project for software development roles and technical interviews.
