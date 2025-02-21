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
    static JLabel username, password;
    static JTextField tfname;
    static JPasswordField tfpassword;
    static JButton submit, cancel;

    login() {
        setTitle("Pharmasoft Login");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        username = new JLabel("Username:");
        password = new JLabel("Password:");
        tfname = new JTextField();
        tfpassword = new JPasswordField();  // Changed to JPasswordField
        submit = new JButton("Submit");
        cancel = new JButton("Cancel");
        username.setBounds(300, 160, 100, 30);
        password.setBounds(300, 210, 100, 30);
        tfname.setBounds(400, 160, 150, 30);
        tfpassword.setBounds(400, 210, 150, 30);
        submit.setBounds(350, 270, 100, 40);
        submit.setBackground(new Color(34, 177, 76));
        submit.setForeground(Color.WHITE);
        cancel.setBounds(470, 270, 100, 40);
        cancel.setBackground(new Color(34, 177, 76));
        cancel.setBackground(new Color(34, 177, 76));
        cancel.setForeground(Color.WHITE);
        submit.addActionListener(this);
        cancel.addActionListener(e -> System.exit(0));
        add(username);
        add(password);
        add(tfname);
        add(tfpassword);
        add(submit);
        add(cancel);
        setVisible(true);
    }

    public static void main(String[] args) {
        new login();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String enteredUsername = tfname.getText();
        String enteredPassword = new String(tfpassword.getPassword());  // Convert password to String

        try (Connection con = DAO.getConnection()) {  // Auto-close connection
            String query = "SELECT password FROM user WHERE username=?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, enteredUsername);
            ResultSet rs = pst.executeQuery();

            String dbPass = null;
            if (rs.next()) {  // Check if user exists
                dbPass = rs.getString("password");
            }

            if (dbPass != null && enteredPassword.equals(dbPass))
            {
                new DashBoard();
                setVisible(false);
            } else
            {
                JOptionPane.showMessageDialog(this, "Invalid Username or Password", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
