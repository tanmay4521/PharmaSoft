package com.pharma;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DashBoard extends JFrame implements ActionListener {
    static DashBoard instance; // Static instance to track existing dashboard

    static JLabel mainLabel;
    static JButton addMed, viewMed, updateMed, deleteMed, cart,logout;

    DashBoard() { // Private constructor to prevent multiple instances
        setTitle("Pharmasoft Dashboard");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        // Main Label
        mainLabel = new JLabel("Pharmasoft Dashboard", SwingConstants.CENTER);
        mainLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mainLabel.setBounds(250, 30, 300, 40);
        add(mainLabel);

        // Buttons
        addMed = new JButton("Add Medicine");
        viewMed = new JButton("View Medicines");
        updateMed = new JButton("Update Medicine");
        deleteMed = new JButton("Delete Medicine");
        cart=new JButton("Cart");
        logout = new JButton("Logout");

        addMed.addActionListener(this);
        viewMed.addActionListener(this);
        updateMed.addActionListener(this);
        deleteMed.addActionListener(this);
        logout.addActionListener(this);

        JButton[] buttons = {addMed, viewMed, updateMed, deleteMed, cart,logout};
        int y = 100;
        for (JButton btn : buttons) {
            btn.setBounds(300, y, 200, 40);
            btn.setFont(new Font("Arial", Font.PLAIN, 16));
            btn.setBackground(new Color(34, 177, 76));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            add(btn);
            y += 60;
        }

        setVisible(true);
    }

    // Singleton Pattern: Only One Dashboard Instance
    public static DashBoard getInstance() {
        if (instance == null) {
            instance = new DashBoard();
        }
        return instance;
    }

    // Bring existing window to front
    public void bringToFront() {
        setState(JFrame.NORMAL);
        toFront();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addMed) {
            new AddMedicine();
            dispose(); // Close current dashboard to prevent duplicate windows
        } else if (e.getSource() == viewMed) {
            new ViewMedicine();
            dispose();
        } else if (e.getSource() == updateMed) {
            // new UpdateMedicine(); // Implement update functionality
            JOptionPane.showMessageDialog(this, "Update Medicine Clicked! (Not Implemented)", "Info", JOptionPane.INFORMATION_MESSAGE);
        } else if (e.getSource() == deleteMed) {
            // new DeleteMedicine(); // Implement delete functionality
            JOptionPane.showMessageDialog(this, "Delete Medicine Clicked! (Not Implemented)", "Info", JOptionPane.INFORMATION_MESSAGE);
        } else if (e.getSource() == logout) {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                instance = null; // Reset instance on logout
                dispose(); // Close the dashboard
                new login(); // Implement Login Screen
            }
        }
    }

    public static void main(String[] args) {
        getInstance();
    }
}
