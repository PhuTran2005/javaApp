package com.example.coursemanagement.Repository;

import com.example.coursemanagement.Models.Instructor;
import com.example.coursemanagement.Models.Student;
import com.example.coursemanagement.Models.User;
import com.example.coursemanagement.Utils.DatabaseConfig;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    // Đăng ký tài khoản
    public boolean registerUser(String email, String password, int roleId, String fullname) {
        String query = "INSERT INTO Users (email, password_hash, role_id, full_name) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction

            try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

                stmt.setString(1, email);
                stmt.setString(2, passwordEncoder.encode(password));
                stmt.setInt(3, roleId);
                stmt.setString(4, fullname);

                if (stmt.executeUpdate() == 0) {
                    conn.rollback();
                    return false;
                }

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);

                        boolean insertSuccess;
                        if (roleId == 2) {
                            insertSuccess = insertInstructor(userId, conn);
                        } else {
                            insertSuccess = insertStudent(userId, conn);
                        }

                        if (!insertSuccess) {
                            conn.rollback();
                            return false;
                        }

                        conn.commit(); // Thành công toàn bộ
                        return true;
                    }
                }
            }

            conn.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean insertInstructor(int userId, Connection conn) throws SQLException {
        String query = "INSERT INTO Instructors (instructor_id) VALUES (?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean insertStudent(int userId, Connection conn) throws SQLException {
        String query = "INSERT INTO Students (student_id) VALUES (?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        }
    }


    // Kiểm tra đăng nhập
    public User loginUser(String email, String password) {
        String query = "SELECT * FROM Users WHERE email = ? ";
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                if (passwordEncoder.matches(password, rs.getString("password_hash"))) {
                    return new User(rs.getInt("user_id"), rs.getString("email"), rs.getString("full_name"), rs.getInt("role_id"), rs.getString("phonenumber"), rs.getString("create_date"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isExistEmail(String email) {
        String query = "SELECT * FROM Users WHERE email = ? ";
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

    public User getUserByEmail(String email) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String sql = "SELECT * FROM users WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(rs.getInt("user_id"), rs.getString("email"), rs.getString("full_name"), rs.getInt("roleId"), rs.getString("phonenumber"), rs.getString("create_date"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<User> getAllUserByRole(String role) {
        List<User> users = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection()) {
            String sql = "SELECT * FROM users WHERE role = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, role);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(new User(rs.getInt("user_id"), rs.getString("email"), rs.getString("full_name"), rs.getInt("roleId"), rs.getString("phonenumber"), rs.getString("create_date")));
            }
            return users;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }


    public boolean updatePasswordUser(String email, String newPassword) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String sql = "UPDATE Users SET password_hash = ? WHERE email = ?";
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

    // Kiểm tra email có tồn tại không
    public boolean checkEmailExists(String email) {
        String sql = "SELECT COUNT(*) FROM Users WHERE email = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật mật khẩu mới cho email
    public boolean updatePasswordByEmail(String email, String newPassword) {
        String query = "UPDATE Users SET password_hash = ? WHERE email = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newPassword); // nên mã hóa nếu cần
            stmt.setString(2, email);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
