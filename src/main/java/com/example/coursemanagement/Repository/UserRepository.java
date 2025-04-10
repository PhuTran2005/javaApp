package com.example.coursemanagement.Repository;

import com.example.coursemanagement.Models.User;
import com.example.coursemanagement.Utils.DatabaseConfig;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    // Đăng ký tài khoản
    public  boolean registerUser(String email, String password) {
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
    public  User loginUser(String email, String password) {
        String query = "SELECT * FROM Users WHERE userEmail = ? ";
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                if (passwordEncoder.matches(password, rs.getString("userPassword"))) {
                    return new User(rs.getInt("userId"), rs.getString("username"), rs.getString("userEmail"), rs.getString("role"), rs.getString("userPhoneNumber"), rs.getString("createDate"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public  boolean isExistEmail(String email) {
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

    public  User getUserByEmail(String email) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String sql = "SELECT * FROM users WHERE userEmail = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(rs.getInt("userId"), rs.getString("username"), rs.getString("userEmail"), rs.getString("role"), rs.getString("userPhoneNumber"), rs.getString("createDate"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public  List<User> getAllUserByRole(String role){
        List<User> users = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection()) {
            String sql = "SELECT * FROM users WHERE role = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, role);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                users.add(new User(rs.getInt("userId"), rs.getString("username"), rs.getString("userEmail"), rs.getString("role"), rs.getString("userPhoneNumber"), rs.getString("createDate")));
            }
            return users;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
    public  boolean updateInforUser(User user) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String sql = "UPDATE Users SET username = ?, userPhoneNumber = ? WHERE userEmail = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getUserPhoneNumber());
            stmt.setString(3, user.getUserEmail());
            return stmt.executeUpdate() > 0; // Nếu có ít nhất 1 dòng được cập nhật
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public  boolean updatePasswordUser(String email,String newPassword) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String sql = "UPDATE Users SET userPassword = ? WHERE userEmail = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

            stmt.setString(1, passwordEncoder.encode((newPassword)));
            stmt.setString(2, email);

            return stmt.executeUpdate() > 0; // Nếu có ít nhất 1 dòng được cập nhật
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


}
