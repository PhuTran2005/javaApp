package com.example.coursemanagement.Repository;

import com.example.coursemanagement.Models.Category;
import com.example.coursemanagement.Utils.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CategoriesRepository {
    public List<Category> getAllCategory() {
        List<Category> categories = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection()) {
            String sql = "SELECT * FROM Categories";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                categories.add(new Category(rs.getInt("category_id"), rs.getString("category_name"), rs.getString("description")));
            }
            return categories;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public Category getCategoryById(int id) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String sql = "SELECT * FROM Categories where category_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Category(rs.getInt("category_id"), rs.getString("category_name"), rs.getString("description"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

}
