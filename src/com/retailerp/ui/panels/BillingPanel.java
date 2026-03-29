package com.retailerp.ui.panels;

import com.retailerp.model.*;
import com.retailerp.service.ERPService;
import com.retailerp.util.EventBus;
import com.retailerp.util.Refreshable;
import com.retailerp.util.SessionManager;
import com.retailerp.util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.List;

public class BillingPanel extends JPanel implements Refreshable {
    private final ERPService service = new ERPService();
    private DefaultTableModel cartModel;
    private JTable cartTable;
    private JComboBox<Product> cmbProduct;
    private JComboBox<Customer> cmbCustomer;
    private JTextField txtQty, txtDiscount;
    private JLabel lblSubtotal, lblGst, lblDiscount, lblTotal, lblInvoice;
    private JComboBox<String> cmbPayment;

    private final List<SaleItem> cartItems = new ArrayList<>();

    public BillingPanel() {
        setBackground(UIConstants.BG_DARK);
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        buildUI();

        EventBus.subscribe("PRODUCT_CHANGED", e -> refresh());
        EventBus.subscribe("CUSTOMER_CHANGED", e -> refresh());

        refresh(); // Load initial data
    }

    private void buildUI() {
        // ─── TOP: product selector ───
        JPanel topPanel = UIConstants.createFormPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 8));

        cmbProduct = UIConstants.createStyledComboBox();
        cmbProduct.setPreferredSize(new Dimension(220, 30));

        txtQty = UIConstants.createStyledTextField();
        txtQty.setPreferredSize(new Dimension(60, 30));
        txtQty.setText("1");

        JButton btnAddToCart = UIConstants.createStyledButton("Add to Cart", UIConstants.ACCENT_GREEN);
        btnAddToCart.addActionListener(e -> addToCart());

        JButton btnRemove = UIConstants.createStyledButton("Remove", UIConstants.ACCENT_RED);
        btnRemove.addActionListener(e -> removeFromCart());

        topPanel.add(UIConstants.createLabel("Product:"));
        topPanel.add(cmbProduct);
        topPanel.add(UIConstants.createLabel("Qty:"));
        topPanel.add(txtQty);
        topPanel.add(btnAddToCart);
        topPanel.add(btnRemove);

        add(topPanel, BorderLayout.NORTH);

        // ─── CENTER: cart table ───
        cartModel = new DefaultTableModel(new String[] { "#", "Product", "Price", "Qty", "GST%", "GST Amt", "Total" },
                0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        cartTable = new JTable(cartModel);
        add(UIConstants.createStyledScrollPane(cartTable), BorderLayout.CENTER);

        // ─── RIGHT: summary + checkout ───
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(UIConstants.PANEL_BG);
        rightPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        rightPanel.setPreferredSize(new Dimension(280, 0));

        lblInvoice = new JLabel("Invoice: —");
        lblInvoice.setFont(UIConstants.FONT_SUBTITLE);
        lblInvoice.setForeground(UIConstants.ACCENT_BLUE);
        rightPanel.add(lblInvoice);
        rightPanel.add(Box.createVerticalStrut(15));

        rightPanel.add(UIConstants.createLabel("Customer (optional):"));
        cmbCustomer = UIConstants.createStyledComboBox();
        rightPanel.add(cmbCustomer);
        rightPanel.add(Box.createVerticalStrut(10));

        rightPanel.add(UIConstants.createLabel("Discount (₹):"));
        txtDiscount = UIConstants.createStyledTextField();
        txtDiscount.setText("0");
        txtDiscount.setMaximumSize(new Dimension(260, 30));
        rightPanel.add(txtDiscount);
        rightPanel.add(Box.createVerticalStrut(10));

        rightPanel.add(UIConstants.createLabel("Payment Mode:"));
        cmbPayment = UIConstants.createStyledComboBox();
        cmbPayment.addItem("Cash");
        cmbPayment.addItem("Card");
        cmbPayment.addItem("UPI");
        cmbPayment.addItem("Other");
        rightPanel.add(cmbPayment);
        rightPanel.add(Box.createVerticalStrut(20));

        lblSubtotal = summaryLabel("Subtotal: ₹ 0.00");
        lblGst = summaryLabel("GST: ₹ 0.00");
        lblDiscount = summaryLabel("Discount: ₹ 0.00");
        lblTotal = summaryLabel("TOTAL: ₹ 0.00");
        lblTotal.setFont(UIConstants.FONT_CARD_NUM);
        lblTotal.setForeground(UIConstants.ACCENT_GREEN);

        rightPanel.add(lblSubtotal);
        rightPanel.add(Box.createVerticalStrut(5));
        rightPanel.add(lblGst);
        rightPanel.add(Box.createVerticalStrut(5));
        rightPanel.add(lblDiscount);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(lblTotal);
        rightPanel.add(Box.createVerticalStrut(20));

        JButton btnCheckout = UIConstants.createStyledButton("Checkout & Save", UIConstants.ACCENT_BLUE);
        btnCheckout.setPreferredSize(new Dimension(260, 40));
        btnCheckout.setMaximumSize(new Dimension(260, 40));
        btnCheckout.addActionListener(e -> checkout());
        rightPanel.add(btnCheckout);
        rightPanel.add(Box.createVerticalStrut(10));

        JButton btnPrint = UIConstants.createStyledButton("Print Invoice", UIConstants.CARD_BG);
        btnPrint.setPreferredSize(new Dimension(260, 36));
        btnPrint.setMaximumSize(new Dimension(260, 36));
        btnPrint.addActionListener(e -> printInvoice());
        rightPanel.add(btnPrint);

        JButton btnNewBill = UIConstants.createStyledButton("New Bill", UIConstants.ACCENT_ORANGE);
        btnNewBill.setPreferredSize(new Dimension(260, 36));
        btnNewBill.setMaximumSize(new Dimension(260, 36));
        btnNewBill.addActionListener(e -> newBill());
        rightPanel.add(Box.createVerticalStrut(8));
        rightPanel.add(btnNewBill);

        add(rightPanel, BorderLayout.EAST);
    }

    @Override
    public void refresh() {
        // Preserve selected items
        Product p = (Product) cmbProduct.getSelectedItem();
        Customer c = (Customer) cmbCustomer.getSelectedItem();

        cmbProduct.removeAllItems();
        int branchId = SessionManager.getBranchId();
        List<Product> products = branchId > 0 ? service.getProductsByBranch(branchId) : service.getAllProducts();
        for (Product pr : products)
            cmbProduct.addItem(pr);

        cmbCustomer.removeAllItems();
        cmbCustomer.addItem(null); // Walk-in
        for (Customer cu : service.getAllCustomers())
            cmbCustomer.addItem(cu);

        // Restore if possible
        if (p != null) {
            for (int i = 0; i < cmbProduct.getItemCount(); i++) {
                if (cmbProduct.getItemAt(i).getProductId() == p.getProductId()) {
                    cmbProduct.setSelectedIndex(i);
                    break;
                }
            }
        }
        if (c != null) {
            for (int i = 0; i < cmbCustomer.getItemCount(); i++) {
                Customer curr = cmbCustomer.getItemAt(i);
                if (curr != null && curr.getCustomerId() == c.getCustomerId()) {
                    cmbCustomer.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void addToCart() {
        Product p = (Product) cmbProduct.getSelectedItem();
        if (p == null) {
            msg("Select a product.");
            return;
        }
        int qty;
        try {
            qty = Integer.parseInt(txtQty.getText().trim());
            if (qty <= 0)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            msg("Enter valid quantity.");
            return;
        }

        // Check stock
        int stock = service.getStock(p.getProductId(), SessionManager.getBranchId());
        if (stock < qty && SessionManager.getBranchId() > 0) {
            msg("Insufficient stock. Available: " + stock);
            return;
        }

        SaleItem item = new SaleItem();
        item.setProductId(p.getProductId());
        item.setProductName(p.getProductName());
        item.setQuantity(qty);
        item.setUnitPrice(p.getSellPrice());
        item.setGstPercent(p.getGstPercent());
        double baseTotal = p.getSellPrice() * qty;
        double gstAmt = baseTotal * p.getGstPercent() / 100.0;
        item.setGstAmount(gstAmt);
        item.setTotalPrice(baseTotal + gstAmt);

        cartItems.add(item);
        refreshCart();
    }

    private void removeFromCart() {
        int row = cartTable.getSelectedRow();
        if (row >= 0) {
            cartItems.remove(row);
            refreshCart();
        }
    }

    private void refreshCart() {
        cartModel.setRowCount(0);
        double subtotal = 0, gstTotal = 0;
        int idx = 1;
        for (SaleItem item : cartItems) {
            cartModel.addRow(new Object[] { idx++, item.getProductName(),
                    String.format("%.2f", item.getUnitPrice()), item.getQuantity(),
                    item.getGstPercent(), String.format("%.2f", item.getGstAmount()),
                    String.format("%.2f", item.getTotalPrice()) });
            subtotal += item.getUnitPrice() * item.getQuantity();
            gstTotal += item.getGstAmount();
        }
        double disc = 0;
        try {
            disc = Double.parseDouble(txtDiscount.getText().trim());
        } catch (Exception ignored) {
        }
        double total = subtotal + gstTotal - disc;

        lblSubtotal.setText(String.format("Subtotal: ₹ %.2f", subtotal));
        lblGst.setText(String.format("GST: ₹ %.2f", gstTotal));
        lblDiscount.setText(String.format("Discount: ₹ %.2f", disc));
        lblTotal.setText(String.format("₹ %.2f", total));
    }

    private void checkout() {
        if (cartItems.isEmpty()) {
            msg("Cart is empty.");
            return;
        }

        String invoiceNo = service.generateInvoiceNumber();
        lblInvoice.setText("Invoice: " + invoiceNo);

        double subtotal = 0, gstTotal = 0;
        for (SaleItem item : cartItems) {
            subtotal += item.getUnitPrice() * item.getQuantity();
            gstTotal += item.getGstAmount();
        }
        double disc = 0;
        try {
            disc = Double.parseDouble(txtDiscount.getText().trim());
        } catch (Exception ignored) {
        }
        double total = subtotal + gstTotal - disc;

        Sale sale = new Sale();
        sale.setInvoiceNumber(invoiceNo);
        sale.setBranchId(SessionManager.getBranchId());
        Customer selCust = (Customer) cmbCustomer.getSelectedItem();
        sale.setCustomerId(selCust != null ? selCust.getCustomerId() : 0);
        sale.setUserId(SessionManager.getUserId());
        sale.setSubtotal(subtotal);
        sale.setGstAmount(gstTotal);
        sale.setDiscount(disc);
        sale.setTotalAmount(total);
        sale.setPaymentMode((String) cmbPayment.getSelectedItem());

        boolean success = service.checkout(sale, new ArrayList<>(cartItems), selCust);
        if (success) {
            msg("Sale completed! Invoice: " + invoiceNo);
            cartItems.clear();
            refreshCart();
        } else {
            msg("Failed to checkout. (Transaction rolled back - usually due to race-condition stock out)");
        }
    }

    private void printInvoice() {
        if (cartItems.isEmpty() && lblInvoice.getText().equals("Invoice: —")) {
            msg("Nothing to print.");
            return;
        }
        // Build printable text using JTextArea
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("           RETAIL ERP - TAX INVOICE\n");
        sb.append("═══════════════════════════════════════\n");
        sb.append("Invoice: ").append(lblInvoice.getText().replace("Invoice: ", "")).append("\n");
        sb.append("Date: ").append(java.time.LocalDateTime.now().toString().replace("T", " ").substring(0, 19))
                .append("\n");
        sb.append("Cashier: ").append(
                SessionManager.getCurrentUser() != null ? SessionManager.getCurrentUser().getFullName() : "Admin")
                .append("\n");
        sb.append("───────────────────────────────────────\n");
        sb.append(String.format("%-20s %5s %10s\n", "Product", "Qty", "Total"));
        sb.append("───────────────────────────────────────\n");
        for (SaleItem item : cartItems) {
            sb.append(String.format("%-20s %5d %10.2f\n", item.getProductName(), item.getQuantity(),
                    item.getTotalPrice()));
        }
        sb.append("───────────────────────────────────────\n");
        sb.append(lblSubtotal.getText()).append("\n");
        sb.append(lblGst.getText()).append("\n");
        sb.append(lblDiscount.getText()).append("\n");
        sb.append("TOTAL: ").append(lblTotal.getText()).append("\n");
        sb.append("═══════════════════════════════════════\n");
        sb.append("        Thank you for shopping!\n");

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        try {
            textArea.print();
        } catch (PrinterException e) {
            msg("Print error: " + e.getMessage());
        }
    }

    private void newBill() {
        cartItems.clear();
        cartModel.setRowCount(0);
        txtQty.setText("1");
        txtDiscount.setText("0");
        lblInvoice.setText("Invoice: —");
        lblSubtotal.setText("Subtotal: ₹ 0.00");
        lblGst.setText("GST: ₹ 0.00");
        lblDiscount.setText("Discount: ₹ 0.00");
        lblTotal.setText("₹ 0.00");
        refresh(); // refetch products and customers just to be safe
    }

    private JLabel summaryLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UIConstants.FONT_SUBTITLE);
        lbl.setForeground(UIConstants.TEXT_PRIMARY);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
    }

    private void msg(String m) {
        JOptionPane.showMessageDialog(this, m);
    }
}
