package com.example.coursemanagement.Utils;


import com.example.coursemanagement.Models.Course;
import com.example.coursemanagement.Models.Instructor;
import com.example.coursemanagement.Models.User;
import com.example.coursemanagement.Repository.CoursesRepository;
import com.example.coursemanagement.Service.CourseService;
import com.example.coursemanagement.Service.InstructorService;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.List;
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

    public static void test() {
        InstructorService instructorService = new InstructorService();
        List<Instructor> instructors = instructorService.getAllInstructor();
        for (Instructor item : instructors
        ) {
            System.out.println(item);
        }
    }

    public static void selectAllUsers() {
        String query = "SELECT * FROM Users";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            boolean hasResult = false;
            while (rs.next()) {
                hasResult = true;
                int id = rs.getInt("user_id");
                String email = rs.getString("email");
                String fullName = rs.getString("full_name");
                int roleId = rs.getInt("role_id");

                System.out.println("ID: " + id + ", Email: " + email + ", Fullname: " + fullName + ", Role ID: " + roleId);
            }
            if (!hasResult) {
                System.out.println("⚠️ Không có người dùng nào trong bảng Users.");
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String query = "SELECT * FROM Users";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            boolean hasResult = false;
            while (rs.next()) {
                hasResult = true;
                int id = rs.getInt("user_id");
                String email = rs.getString("email");
                String fullName = rs.getString("full_name");
                int roleId = rs.getInt("role_id");

                System.out.println("ID: " + id + ", Email: " + email + ", Fullname: " + fullName + ", Role ID: " + roleId);
            }
            if (!hasResult) {
                System.out.println("⚠️ Không có người dùng nào trong bảng Users.");
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        selectAllUsers();
//        test();
    }
}