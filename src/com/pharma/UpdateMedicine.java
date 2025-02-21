package com.pharma;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateMedicine extends JFrame implements ActionListener {
    JTextField searchBar, updatedName, updatedPrice, updatedQty;
    JLabel searchLabel, nameLabel, priceLabel, qtyLabel;
    JButton searchButton, updateButton, resetButton, backButton;

    UpdateMedicine() {
        setTitle("Update Medicine");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // Search Section
        searchLabel = new JLabel("Search Medicine (ID/Name):");
        searchLabel.setBounds(50, 30, 200, 30);
        searchBar = new JTextField();
        searchBar.setBounds(230, 30, 150, 30);
        searchButton = new JButton("Search");
        searchButton.setBounds(390, 30, 80, 30);
        searchButton.addActionListener(this);

        // Labels
        nameLabel = new JLabel("Medicine Name:");
        priceLabel = new JLabel("Price:");
        qtyLabel = new JLabel("Quantity:");

        nameLabel.setBounds(50, 80, 150, 30);
        priceLabel.setBounds(50, 130, 150, 30);
        qtyLabel.setBounds(50, 180, 150, 30);

        // Text Fields
        updatedName = new JTextField();
        updatedPrice = new JTextField();
        updatedQty = new JTextField();

        updatedName.setBounds(200, 80, 200, 30);
        updatedPrice.setBounds(200, 130, 200, 30);
        updatedQty.setBounds(200, 180, 200, 30);

        // Buttons
        updateButton = new JButton("Update");
        resetButton = new JButton("Reset");
        backButton = new JButton("Back");

        updateButton.setBounds(50, 250, 120, 40);
        resetButton.setBounds(190, 250, 120, 40);
        backButton.setBounds(330, 250, 120, 40);

        updateButton.setBackground(new Color(34, 177, 76));
        updateButton.setForeground(Color.WHITE);
        resetButton.setBackground(new Color(255, 140, 0));
        resetButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(200, 0, 0));
        backButton.setForeground(Color.WHITE);

        updateButton.addActionListener(this);
        resetButton.addActionListener(this);
        backButton.addActionListener(e -> {
            DashBoard.getInstance().bringToFront(); // Use Singleton Dashboard Instance
            dispose();
        });

        // Add Components
        add(searchLabel);
        add(searchBar);
        add(searchButton);
        add(nameLabel);
        add(updatedName);
        add(priceLabel);
        add(updatedPrice);
        add(qtyLabel);
        add(updatedQty);
        add(updateButton);
        add(resetButton);
        add(backButton);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == searchButton) {
            searchMedicine();
        } else if (e.getSource() == updateButton) {
            updateMedicine();
        } else if (e.getSource() == resetButton) {
            clearFields();
        }
    }

    private void searchMedicine() {
        String searchQuery = searchBar.getText().trim();
        if (searchQuery.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an ID or name to search.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection con = DAO.getConnection();
             PreparedStatement pst = con.prepareStatement("SELECT * FROM medicine WHERE id=? OR med_name=?")) {

            pst.setString(1, searchQuery);
            pst.setString(2, searchQuery);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                updatedName.setText(rs.getString("med_name"));
                updatedPrice.setText(String.valueOf(rs.getDouble("med_price")));
                updatedQty.setText(String.valueOf(rs.getInt("med_qty")));
            } else {
                JOptionPane.showMessageDialog(this, "Medicine not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateMedicine() {
        String medName = updatedName.getText();
        String medPrice = updatedPrice.getText();
        String medQty = updatedQty.getText();
        String searchQuery = searchBar.getText().trim();

        if (medName.isEmpty() || medPrice.isEmpty() || medQty.isEmpty() || searchQuery.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection con = DAO.getConnection();
             PreparedStatement pst = con.prepareStatement("UPDATE medicine SET med_name=?, med_price=?, med_qty=? WHERE id=? OR med_name=?")) {

            pst.setString(1, medName);
            pst.setDouble(2, Double.parseDouble(medPrice));
            pst.setInt(3, Integer.parseInt(medQty));
            pst.setString(4, searchQuery);
            pst.setString(5, searchQuery);

            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Medicine updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update medicine.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input for price or quantity.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        searchBar.setText("");
        updatedName.setText("");
        updatedPrice.setText("");
        updatedQty.setText("");
    }

    public static void main(String[] args) {
        new UpdateMedicine();
    }
}
