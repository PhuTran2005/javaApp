package com.example.coursemanagement.Utils;


import com.example.coursemanagement.Models.Category;
import com.example.coursemanagement.Models.User;
import com.example.coursemanagement.Respository.CategoriesRespository;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
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

    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
            System.out.println("✅ Kết nối SQL Server thành công!");
        } catch (SQLException e) {
            System.out.println("❌ Lỗi kết nối: " + e.getMessage());
        }
        return conn;
    }

    public static void test() {
//        List<Category> categories = new ArrayList<>();
//        categories = categories.getAllCategory();
//        for (Category item : categories
//        ) {
//            System.out.println(item);
//        }
    }
    public static void main(String[] args) {
        test();
    }
}
