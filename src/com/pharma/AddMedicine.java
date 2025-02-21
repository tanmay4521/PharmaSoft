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

public class AddMedicine extends JFrame implements ActionListener {
    static JLabel medNameLabel, medPriceLabel, medQtyLabel;
    static JTextField medNameField, medPriceField, medQtyField;
    static JButton submitButton;

    AddMedicine() {
        setTitle("Add Medicine");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

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

        // Positioning Elements
        medNameLabel.setBounds(50, 50, 150, 30);
        medPriceLabel.setBounds(50, 100, 150, 30);
        medQtyLabel.setBounds(50, 150, 150, 30);

        medNameField.setBounds(200, 50, 200, 30);
        medPriceField.setBounds(200, 100, 200, 30);
        medQtyField.setBounds(200, 150, 200, 30);

        submitButton.setBounds(150, 220, 180, 40);

        // Adding Components
        add(medNameLabel);
        add(medPriceLabel);
        add(medQtyLabel);
        add(medNameField);
        add(medPriceField);
        add(medQtyField);
        add(submitButton);

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
                pst.setString(2, medicinePrice);
                pst.setString(3, medicineQty);

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
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
