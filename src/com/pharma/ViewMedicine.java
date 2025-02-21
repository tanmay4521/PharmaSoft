package com.pharma;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewMedicine extends JFrame {
    JTable medicineTable;
    DefaultTableModel tableModel;

    ViewMedicine() {
        setTitle("View Medicines");
        setSize(700, 400);
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
