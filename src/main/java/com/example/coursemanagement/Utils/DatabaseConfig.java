package com.example.coursemanagement.Utils;


import com.example.coursemanagement.Models.User;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DatabaseConfig {
    private static final Properties prop = new Properties();

    static {
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new IOException("Không tìm thấy file config.properties trong resources");
            }
            prop.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static final String URL = prop.getProperty("url");
    private static final String USER = prop.getProperty("username");
    private static final String PASSWORD = prop.getProperty("password");


    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Kết nối SQL Server thành công!");
        } catch (SQLException e) {
            System.out.println("❌ Lỗi kết nối: " + e.getMessage());
        }
        return conn;
    }
    public static User test(String email, String password) {
        String query = "SELECT * FROM Users WHERE userEmail = ? AND userPassword = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(rs.getInt("userId"), rs.getString("userEmail"), rs.getString("userPassword"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void main(String[] args) {
        System.out.println(test("admin@gmail.com","admin"));
    }
}
