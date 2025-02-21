package com.pharma;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DashBoard extends JFrame implements ActionListener {
    static JLabel mainLabel;
    static JButton addMed, viewMed, updateMed, deleteMed, logout;

    DashBoard() {
        setTitle("Pharmasoft Dashboard");
        setSize(800, 500); // Standardized size
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
        logout = new JButton("Logout");
        addMed.addActionListener(this);
        viewMed.addActionListener(this);
        updateMed.addActionListener(this);
        deleteMed.addActionListener(this);
        logout.addActionListener(this);
        JButton[] buttons = {addMed, viewMed, updateMed, deleteMed, logout};
        int y = 100;
        for (JButton btn : buttons) {
            btn.setBounds(300, y, 200, 40);
            btn.setFont(new Font("Arial", Font.PLAIN, 16));
            btn.setBackground(new Color(34, 177, 76)); // Medical Green
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            add(btn);
            y += 60;
        }

        setVisible(true);
    }

    public static void main(String[] args) {
        new DashBoard();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addMed) {
            new AddMedicine();
        }
    }

}
