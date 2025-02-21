package com.pharma;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewMedicine extends JFrame {
    JTable medicineTable;
    DefaultTableModel tableModel;
    JButton backButton, deleteButton;

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
        header.setBackground(new Color(34, 177, 76));
        header.setForeground(Color.WHITE);

        // Customize Row Height
        medicineTable.setRowHeight(25);

        // Alternating Row Colors
        medicineTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? new Color(240, 255, 240) : Color.WHITE);
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
        backButton.setBackground(new Color(200, 0, 0));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setPreferredSize(new Dimension(200, 40));

        // Delete Button
        deleteButton = new JButton("Delete Selected");
        deleteButton.setFont(new Font("Arial", Font.BOLD, 14));
        deleteButton.setBackground(new Color(255, 69, 0));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.setPreferredSize(new Dimension(200, 40));
        deleteButton.setEnabled(false); // Initially disabled

        // Back Button Action
        backButton.addActionListener(e -> {
            new DashBoard();
            dispose();
        });

        // Delete Button Action
        deleteButton.addActionListener(e -> deleteSelectedMedicine());

        // Adding Buttons at Bottom
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Table Selection Listener
        medicineTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && medicineTable.getSelectedRow() != -1) {
                deleteButton.setEnabled(true);
            } else {
                deleteButton.setEnabled(false);
            }
        });

        // Load Data from Database
        loadMedicineData();

        setVisible(true);
    }

    private void loadMedicineData() {
        try (Connection con = DAO.getConnection();
             PreparedStatement pst = con.prepareStatement("SELECT * FROM medicine");
             ResultSet rs = pst.executeQuery()) {

            tableModel.setRowCount(0); // Clear previous data
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

    private void deleteSelectedMedicine() {
        int selectedRow = medicineTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a medicine to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int medicineId = (int) tableModel.getValueAt(selectedRow, 0);
        String medicineName = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete " + medicineName + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection con = DAO.getConnection();
                 PreparedStatement pst = con.prepareStatement("DELETE FROM medicine WHERE id = ?")) {
                pst.setInt(1, medicineId);
                int rowsAffected = pst.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Medicine deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadMedicineData(); // Refresh table after deletion
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete medicine.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        new ViewMedicine();
    }
}
