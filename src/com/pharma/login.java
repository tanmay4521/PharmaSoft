package com.pharma;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class login extends JFrame implements ActionListener {
    private JTextField tfUsername;
    private JPasswordField tfPassword;
    private JButton btnSubmit, btnCancel;

    public login() {
        setTitle("Pharmasoft Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center window on screen

        // Using GridBagLayout for center alignment
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Labels & Fields
        JLabel lblUsername = new JLabel("Username:");
        JLabel lblPassword = new JLabel("Password:");
        tfUsername = new JTextField(15);
        tfPassword = new JPasswordField(15);

        // Buttons
        btnSubmit = new JButton("Login");
        btnCancel = new JButton("Exit");

        btnSubmit.setBackground(new Color(34, 177, 76));
        btnSubmit.setForeground(Color.WHITE);
        btnCancel.setBackground(new Color(200, 0, 0)); // Red for exit
        btnCancel.setForeground(Color.WHITE);

        btnSubmit.addActionListener(this);
        btnCancel.addActionListener(e -> System.exit(0));

        // Adding components to layout
        gbc.gridx = 0; gbc.gridy = 0; add(lblUsername, gbc);
        gbc.gridx = 1; gbc.gridy = 0; add(tfUsername, gbc);
        gbc.gridx = 0; gbc.gridy = 1; add(lblPassword, gbc);
        gbc.gridx = 1; gbc.gridy = 1; add(tfPassword, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        add(btnSubmit, gbc);
        gbc.gridy = 3; add(btnCancel, gbc);

        setVisible(true);
    }

    public static void main(String[] args) {
        new login();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String enteredUsername = tfUsername.getText();
        String enteredPassword = new String(tfPassword.getPassword());

        try (Connection con = DAO.getConnection()) {
            String query = "SELECT password FROM user WHERE username=?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, enteredUsername);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String dbPass = rs.getString("password");

                if (enteredPassword.equals(dbPass)) {
                    DashBoard.getInstance().bringToFront();
                    dispose(); // Close login window
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Username or Password", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "User not found", "Error", JOptionPane.ERROR_MESSAGE);
            }

            pst.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
