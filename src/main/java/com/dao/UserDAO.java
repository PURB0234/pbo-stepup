// package com.dao;

// import java.sql.Connection;
// import java.sql.PreparedStatement;
// import java.sql.ResultSet;

// import com.api.DatabaseConnections;

// public class UserDAO {

// public boolean login(
// String nama,
// String email,
// String password) {

// String sql = "SELECT * FROM users " +
// "WHERE nama=? AND email=? AND password=?";

// try {

// Connection conn = DatabaseConnections.getConnection();

// PreparedStatement ps = conn.prepareStatement(sql);

// ps.setString(1, nama);
// ps.setString(2, email);
// ps.setString(3, password);

// ResultSet rs = ps.executeQuery();

// return rs.next();

// } catch (Exception e) {

// e.printStackTrace();
// return false;
// }
// }
// }