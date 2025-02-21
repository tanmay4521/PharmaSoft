package com.pharma;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class AddMedicine extends JFrame implements ActionListener {
    static JLabel medNameLabel, medPriceLabel, medQtyLabel;
    static JTextField medNameField, medPriceField, medQtyField;
    static JButton submitButton, backButton;

    AddMedicine() {
        setTitle("Add Medicine");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        // Ensure the medicine table exists before proceeding
        createMedicineTable();

        // Window close event to return to Dashboard
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                new DashBoard();
                dispose();
            }
        });

        // Labels
        medNameLabel = new JLabel("Medicine Name:");
        medPriceLabel = new JLabel("Medicine Price:");
        medQtyLabel = new JLabel("Medicine Quantity:");

        medNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        medPriceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        medQtyLabel.setFont(new Font("Arial", Font.BOLD, 14));

        // Text Fields
        medNameField = new JTextField();
        medPriceField = new JTextField();
        medQtyField = new JTextField();

        // Submit Button
        submitButton = new JButton("Add Medicine");
        submitButton.setFont(new Font("Arial", Font.BOLD, 14));
        submitButton.setBackground(new Color(34, 177, 76)); // Medical Green
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.addActionListener(this);

        // Back Button
        backButton = new JButton("Back to Dashboard");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(new Color(200, 0, 0)); // Red for Back Button
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new DashBoard(); // Open Dashboard
                dispose(); // Close current window
            }
        });

        // Positioning Elements
        medNameLabel.setBounds(50, 50, 150, 30);
        medPriceLabel.setBounds(50, 100, 150, 30);
        medQtyLabel.setBounds(50, 150, 150, 30);

        medNameField.setBounds(200, 50, 200, 30);
        medPriceField.setBounds(200, 100, 200, 30);
        medQtyField.setBounds(200, 150, 200, 30);

        submitButton.setBounds(150, 220, 180, 40);
        backButton.setBounds(150, 280, 180, 40); // Adjusted below Submit Button

        // Adding Components
        add(medNameLabel);
        add(medPriceLabel);
        add(medQtyLabel);
        add(medNameField);
        add(medPriceField);
        add(medQtyField);
        add(submitButton);
        add(backButton);

        setVisible(true);
    }

    public static void main(String[] args) {
        new AddMedicine();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            String medicineName = medNameField.getText();
            String medicinePrice = medPriceField.getText();
            String medicineQty = medQtyField.getText();

            if (medicineName.isEmpty() || medicinePrice.isEmpty() || medicineQty.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please Fill All Fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Insert into database
            try (Connection con = DAO.getConnection();
                 PreparedStatement pst = con.prepareStatement("INSERT INTO medicine(med_name, med_price, med_qty) VALUES (?, ?, ?)")) {

                pst.setString(1, medicineName);
                pst.setDouble(2, Double.parseDouble(medicinePrice));
                pst.setInt(3, Integer.parseInt(medicineQty));

                int status = pst.executeUpdate();
                if (status > 0) {
                    JOptionPane.showMessageDialog(this, "Medicine added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    medNameField.setText("");
                    medPriceField.setText("");
                    medQtyField.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Something went wrong, please try again!", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input for price or quantity.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void createMedicineTable() {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS medicine (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                "med_name VARCHAR(255) NOT NULL, " +
                "med_price DOUBLE NOT NULL, " +
                "med_qty INT NOT NULL" +
                ")";

        try (Connection con = DAO.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.executeUpdate(createTableQuery);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error creating medicine table: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
