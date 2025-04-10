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

    public static void main(String[] args) {
        test();
    }
}