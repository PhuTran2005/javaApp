package com.example.coursemanagement.Respository;

import com.example.coursemanagement.Models.Category;
import com.example.coursemanagement.Models.User;
import com.example.coursemanagement.Utils.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CategoriesRespository {
    public List<Category> getAllCategory() {
        List<Category> categories = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection()) {
            String sql = "SELECT * FROM Categories";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                categories.add(new Category(rs.getInt("categoryId"), rs.getString("categoryName"), rs.getString("categoryDescription")));
            }
            return categories;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public Category getCategoryById(int id) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String sql = "SELECT * FROM Categories where categoryId = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Category(rs.getInt("categoryId"), rs.getString("categoryName"), rs.getString("categoryDescription"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

}
