package com.api;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnections {
     private static final String URL = "jdbc:mysql://localhost:3306/praktikumpbo";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Gantisesuai password MySQL Anda

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
