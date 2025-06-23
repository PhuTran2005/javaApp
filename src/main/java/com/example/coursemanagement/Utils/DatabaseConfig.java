package com.example.coursemanagement.Utils;


import com.example.coursemanagement.Dto.CourseDetailDTO;
import com.example.coursemanagement.Models.Course;
import com.example.coursemanagement.Models.Instructor;
import com.example.coursemanagement.Models.Student;
import com.example.coursemanagement.Models.User;
import com.example.coursemanagement.Repository.CoursesRepository;
import com.example.coursemanagement.Repository.InstructorRepository;
import com.example.coursemanagement.Repository.StudentRepository;
import com.example.coursemanagement.Repository.UserRepository;
import com.example.coursemanagement.Service.CourseService;
import com.example.coursemanagement.Service.InstructorService;
import com.example.coursemanagement.Service.StudentService;
import org.apache.poi.ss.formula.functions.Index;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.List;
import java.util.Properties;

public class DatabaseConfig {
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=IT_Course_Management;encrypt=true;trustServerCertificate=true";

    private static final String USER = "thang1234";
        private static final String PASSWORD = "123456";

        public static Connection getConnection() {
            try {
                return DriverManager.getConnection(URL,USER,PASSWORD);
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;

        }

        public static void main(String[] args) {
            try (Connection conn = getConnection()) {
                System.out.println("Kết nối thành công!");
            } catch (SQLException e) {
                System.out.println("Kết nối thất bại!");
                e.printStackTrace();
            }
        }

    }