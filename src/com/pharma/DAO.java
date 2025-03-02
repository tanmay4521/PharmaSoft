package com.pharma;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DAO
{
    public static Connection getConnection() throws SQLException
    {
        Connection con= DriverManager.getConnection("jdbc:mysql://localhost:3306/PharmaSoft","root","root");
        return con;
    }
}
