package com.pharma;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SearchMedicine extends JFrame implements ActionListener {
    private JTextField searchBar;
    private JButton searchButton, backButton;
    private JTable resultsTable;
    private DefaultTableModel tableModel;

    SearchMedicine() {
        setTitle("Search Medicine");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // **TOP PANEL - Search Bar & Button**
        JPanel topPanel = new JPanel(new FlowLayout());
        JLabel searchLabel = new JLabel("Search Medicine:");
        searchBar = new JTextField(20);
        searchButton = new JButton("Search");
        searchButton.addActionListener(this);

        topPanel.add(searchLabel);
        topPanel.add(searchBar);
        topPanel.add(searchButton);

        // **TABLE - Display Search Results**
        String[] columnNames = {"ID", "Medicine Name", "Price", "Quantity"};
        tableModel = new DefaultTableModel(columnNames, 0);
        resultsTable = new JTable(tableModel);

        // **Table Styling**
        customizeTable();

        JScrollPane tableScrollPane = new JScrollPane(resultsTable);

        // **BOTTOM PANEL - Back Button**
        JPanel bottomPanel = new JPanel();
        backButton = new JButton("Back to Dashboard");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(new Color(200, 0, 0));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setPreferredSize(new Dimension(200, 40));

        // Back Button Action
        backButton.addActionListener(e -> {
            DashBoard.getInstance().bringToFront();
            dispose();
        });

        bottomPanel.add(backButton);

        // **ADDING COMPONENTS TO FRAME**
        add(topPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void customizeTable() {
        // **Header Styling**
        JTableHeader header = resultsTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(new Color(34, 177, 76));
        header.setForeground(Color.WHITE);

        // **Row Height**
        resultsTable.setRowHeight(25);

        // **Column Width Adjustments**
        resultsTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        resultsTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Medicine Name
        resultsTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Price
        resultsTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Quantity

        // **Cell Alignment**
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        resultsTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // ID Centered

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        resultsTable.getColumnModel().getColumn(2).setCellRenderer(rightRenderer); // Price Right
        resultsTable.getColumnModel().getColumn(3).setCellRenderer(rightRenderer); // Quantity Right

        // **Alternating Row Colors**
        resultsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? new Color(240, 255, 240) : Color.WHITE);
                }
                return c;
            }
        });

        // **Disable Editing**
        resultsTable.setDefaultEditor(Object.class, null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == searchButton) {
            searchMedicine();
        }
    }

    private void searchMedicine() {
        String searchQuery = searchBar.getText().trim();

        if (searchQuery.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an ID or name to search.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        tableModel.setRowCount(0); // Clear previous results

        try (Connection con = DAO.getConnection();
             PreparedStatement pst = con.prepareStatement("SELECT * FROM medicine WHERE id LIKE ? OR med_name LIKE ?")) {

            pst.setString(1, "%" + searchQuery + "%"); // Supports partial matching
            pst.setString(2, "%" + searchQuery + "%");

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("med_name");
                double price = rs.getDouble("med_price");
                int qty = rs.getInt("med_qty");

                tableModel.addRow(new Object[]{id, name, price, qty});
            }

            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No medicine found!", "Info", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new SearchMedicine();
    }
}
