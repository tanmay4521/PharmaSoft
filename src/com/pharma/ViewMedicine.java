package com.pharma;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewMedicine extends JFrame {
    JTable medicineTable;
    DefaultTableModel tableModel;
    JButton backButton;

    ViewMedicine() {
        setTitle("View Medicines");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Table Column Names
        String[] columnNames = {"ID", "Medicine Name", "Price", "Quantity"};
        tableModel = new DefaultTableModel(columnNames, 0);
        medicineTable = new JTable(tableModel);

        // Customize Table Header
        JTableHeader header = medicineTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(new Color(34, 177, 76)); // Medical Green
        header.setForeground(Color.WHITE);

        // Customize Row Height
        medicineTable.setRowHeight(25);

        // Alternating Row Colors
        medicineTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? new Color(240, 255, 240) : Color.WHITE); // Light green alternate rows
                }
                return c;
            }
        });

        // Adding Table to Scroll Pane
        JScrollPane scrollPane = new JScrollPane(medicineTable);
        add(scrollPane, BorderLayout.CENTER);

        // Back Button
        backButton = new JButton("Back to Dashboard");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(new Color(34, 177, 76)); // Medical Green
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setPreferredSize(new Dimension(200, 40));

        // Back Button Action
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new DashBoard(); // Open Dashboard
                dispose(); // Close current window
            }
        });

        // Adding Back Button at Bottom
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load Data from Database
        loadMedicineData();

        setVisible(true);
    }

    private void loadMedicineData() {
        try (Connection con = DAO.getConnection();
             PreparedStatement pst = con.prepareStatement("SELECT * FROM medicine");
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("med_name");
                double price = rs.getDouble("med_price");
                int qty = rs.getInt("med_qty");

                // Adding data to JTable
                tableModel.addRow(new Object[]{id, name, price, qty});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new ViewMedicine();
    }
}
