package com.example.coursemanagement.Dao;

import com.example.coursemanagement.Models.User;
import com.example.coursemanagement.Controllers.DatabaseConfig;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.*;

public class UserDAO {

    // Đăng ký tài khoản
    public static boolean registerUser(String email, String password) {
        String query = "INSERT INTO Users (userEmail, userPassword, role) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

            stmt.setString(1, email);
            stmt.setString(2, passwordEncoder.encode((password)));
            stmt.setString(3, "USER");
            return stmt.executeUpdate() > 0; // Trả về true nếu đăng ký thành công

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Kiểm tra đăng nhập
    public static User loginUser(String email, String password) {
        String query = "SELECT * FROM Users WHERE userEmail = ? ";
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                if (passwordEncoder.matches(password, rs.getString("userPassword"))) {
                    return new User(rs.getString("userEmail"),rs.getString("role"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isExistEmail(String email) {
        String query = "SELECT * FROM Users WHERE userEmail = ? ";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
