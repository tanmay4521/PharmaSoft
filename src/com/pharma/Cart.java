package com.pharma;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Cart extends JFrame implements ActionListener {
    private JTextField searchBar, idField, nameField, priceField, qtyField;
    private JButton searchButton, addToCartButton, checkoutButton, clearCartButton, removeSelectedButton;
    private JTable medicineTable, cartTable;
    private DefaultTableModel medicineTableModel, cartTableModel;
    private JLabel totalLabel;
    private double totalAmount = 0.0;

    Cart() {
        setTitle("Pharma Cart");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(230, 242, 230));

        // Header Panel
        JLabel headerLabel = new JLabel("Pharmacy Medicine Cart", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setForeground(Color.WHITE);
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(34, 177, 76));
        headerPanel.add(headerLabel);
        add(headerPanel, BorderLayout.NORTH);

        JPanel containerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        containerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // LEFT PANEL (Medicine List)
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(new LineBorder(new Color(34, 177, 76), 2, true));

        JPanel searchPanel = new JPanel(new FlowLayout());
        searchBar = new JTextField(15);
        searchButton = new JButton("Search");
        searchButton.setBackground(new Color(0, 153, 76));
        searchButton.setForeground(Color.WHITE);
        searchButton.addActionListener(this);

        searchPanel.add(new JLabel("🔍 Search Medicine:"));
        searchPanel.add(searchBar);
        searchPanel.add(searchButton);

        String[] columns = {"ID", "Name", "Price", "Stock"};
        medicineTableModel = new DefaultTableModel(columns, 0);
        medicineTable = new JTable(medicineTableModel);
        customizeTable(medicineTable);

        medicineTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                int selectedRow = medicineTable.getSelectedRow();
                if (selectedRow != -1) {
                    idField.setText(medicineTableModel.getValueAt(selectedRow, 0).toString());
                    nameField.setText(medicineTableModel.getValueAt(selectedRow, 1).toString());
                    priceField.setText(medicineTableModel.getValueAt(selectedRow, 2).toString());
                    qtyField.setText(""); // Reset quantity field
                }
            }
        });

        leftPanel.add(searchPanel, BorderLayout.NORTH);
        leftPanel.add(new JScrollPane(medicineTable), BorderLayout.CENTER);

        // RIGHT PANEL (Cart & Billing)
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(new LineBorder(new Color(34, 177, 76), 2, true));

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        idField = createTextField(false);
        nameField = createTextField(false);
        priceField = createTextField(false);
        qtyField = createTextField(true);

        formPanel.add(new JLabel("📋 Medicine ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("💊 Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("💲 Price:"));
        formPanel.add(priceField);
        formPanel.add(new JLabel("🔢 Quantity:"));
        formPanel.add(qtyField);

        addToCartButton = new JButton("🛒 Add to Cart");
        addToCartButton.setBackground(new Color(0, 153, 76));
        addToCartButton.setForeground(Color.WHITE);
        addToCartButton.addActionListener(this);

        checkoutButton = new JButton("💳 Checkout");
        checkoutButton.setBackground(new Color(255, 102, 0));
        checkoutButton.setForeground(Color.WHITE);
        checkoutButton.addActionListener(this);

        clearCartButton = new JButton("🗑 Clear Cart");
        clearCartButton.setBackground(Color.RED);
        clearCartButton.setForeground(Color.WHITE);
        clearCartButton.addActionListener(this);

        removeSelectedButton = new JButton("❌ Remove Selected");
        removeSelectedButton.setBackground(Color.DARK_GRAY);
        removeSelectedButton.setForeground(Color.WHITE);
        removeSelectedButton.addActionListener(this);

        formPanel.add(addToCartButton);
        formPanel.add(checkoutButton);
        formPanel.add(clearCartButton);
        formPanel.add(removeSelectedButton);

        String[] cartColumns = {"ID", "Name", "Price", "Quantity", "Total"};
        cartTableModel = new DefaultTableModel(cartColumns, 0);
        cartTable = new JTable(cartTableModel);
        customizeTable(cartTable);

        totalLabel = new JLabel("Total: ₹0.00", JLabel.RIGHT);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));

        rightPanel.add(formPanel, BorderLayout.NORTH);
        rightPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);
        rightPanel.add(totalLabel, BorderLayout.SOUTH);

        containerPanel.add(leftPanel);
        containerPanel.add(rightPanel);
        add(containerPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private JTextField createTextField(boolean editable) {
        JTextField field = new JTextField();
        field.setEditable(editable);
        return field;
    }

    private void customizeTable(JTable table) {
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(new Color(34, 177, 76));
        header.setForeground(Color.WHITE);
        table.setRowHeight(25);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == searchButton) {
            searchMedicine();
        } else if (e.getSource() == addToCartButton) {
            addToCart();
        } else if (e.getSource() == checkoutButton) {
            processCheckout();
        } else if (e.getSource() == clearCartButton) {
            clearCart();
        } else if (e.getSource() == removeSelectedButton) {
            removeSelectedItem();
        }
    }
    private void removeSelectedItem() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to remove!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double itemTotal = (double) cartTableModel.getValueAt(selectedRow, 4); // Get total price of selected item
        totalAmount -= itemTotal; // Deduct from total
        totalLabel.setText("Total: ₹" + totalAmount); // Update total

        cartTableModel.removeRow(selectedRow); // Remove row from table
    }

    private void clearCart() {
        if (cartTableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Cart is already empty!", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to clear the cart?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            cartTableModel.setRowCount(0); // Clear table data
            totalAmount = 0.0; // Reset total amount
            totalLabel.setText("Total: ₹0.00"); // Update total label
        }
    }


    private void addToCart() {
        if (idField.getText().isEmpty() || qtyField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a medicine and enter quantity.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int qty = Integer.parseInt(qtyField.getText());
            double price = Double.parseDouble(priceField.getText());
            double total = qty * price;
            cartTableModel.addRow(new Object[]{idField.getText(), nameField.getText(), price, qty, total});
            totalAmount += total;
            totalLabel.setText("Total: ₹" + totalAmount);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid quantity format.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchMedicine() {
        String searchQuery = searchBar.getText().trim();
        medicineTableModel.setRowCount(0);

        try (Connection con = DAO.getConnection();
             PreparedStatement pst = con.prepareStatement("SELECT * FROM medicine WHERE id LIKE ? OR med_name LIKE ?")) {

            pst.setString(1, "%" + searchQuery + "%");
            pst.setString(2, "%" + searchQuery + "%");

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                medicineTableModel.addRow(new Object[]{rs.getInt("id"), rs.getString("med_name"), rs.getDouble("med_price"), rs.getInt("med_qty")});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void processCheckout() {
        if (cartTableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Cart is empty!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection con = DAO.getConnection()) {
            con.setAutoCommit(false); // Start transaction

            for (int i = 0; i < cartTableModel.getRowCount(); i++) {
                int medId = Integer.parseInt(cartTableModel.getValueAt(i, 0).toString());
                int qtyPurchased = Integer.parseInt(cartTableModel.getValueAt(i, 3).toString());

                // Fetch current stock
                PreparedStatement checkStockStmt = con.prepareStatement("SELECT med_qty FROM medicine WHERE id = ?");
                checkStockStmt.setInt(1, medId);
                ResultSet rs = checkStockStmt.executeQuery();

                if (rs.next()) {
                    int currentStock = rs.getInt("med_qty");
                    if (currentStock < qtyPurchased) {
                        JOptionPane.showMessageDialog(this, "Not enough stock for Medicine ID: " + medId, "Stock Error", JOptionPane.ERROR_MESSAGE);
                        con.rollback(); // Cancel transaction
                        return;
                    }

                    // Update stock
                    PreparedStatement updateStockStmt = con.prepareStatement("UPDATE medicine SET med_qty = med_qty - ? WHERE id = ?");
                    updateStockStmt.setInt(1, qtyPurchased);
                    updateStockStmt.setInt(2, medId);
                    updateStockStmt.executeUpdate();
                }
            }

            con.commit(); // Commit transaction
            JOptionPane.showMessageDialog(this, "Checkout Successful! Total Amount: ₹" + totalAmount, "Success", JOptionPane.INFORMATION_MESSAGE);

            cartTableModel.setRowCount(0); // Clear cart
            totalAmount = 0.0;
            totalLabel.setText("Total: ₹0.00");


        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    public static void main(String[] args) {
        new Cart();
    }
}
