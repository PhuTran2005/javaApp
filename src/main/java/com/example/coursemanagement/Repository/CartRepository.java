package com.example.coursemanagement.Repository;

import com.example.coursemanagement.Models.Cart;
import com.example.coursemanagement.Utils.DatabaseConfig;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CartRepository {

    // ✅ Thêm một mục vào giỏ hàng
    public boolean addToCart(int userId, int courseId) {
        String sql = "INSERT INTO Cart (userId, courseId, quantity, status) VALUES (?, ?, 1, 'PENDING')";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, courseId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ✅ Cập nhật số lượng khóa học trong giỏ
    public void updateQuantity(int cartId, int quantity) {
        String sql = "UPDATE Cart SET quantity = ? WHERE cartId = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quantity);
            stmt.setInt(2, cartId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ✅ Xóa 1 mục khỏi giỏ hàng
    public boolean removeFromCart(int cartId) {
        String sql = "DELETE FROM Cart WHERE cartId = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cartId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ✅ Lấy tất cả mục giỏ hàng của 1 user (PENDING)
    public List<Cart> getCartByUser(int userId) {
        List<Cart> cartList = new ArrayList<>();
        String sql = "SELECT * FROM Cart WHERE userId = ? AND status = 'PENDING'";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Cart cart = new Cart(
                        rs.getInt("cartId"),
                        rs.getInt("userId"),
                        rs.getInt("courseId"),
                        rs.getInt("quantity"),
                        rs.getTimestamp("added_at").toLocalDateTime(),
                        rs.getString("status")
                );
                cartList.add(cart);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cartList;
    }

    public boolean isExistInCart(int userId, int courseId) {
        String sql = "SELECT * FROM Cart WHERE userId = ? and courseId = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, courseId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    // ✅ Đánh dấu toàn bộ giỏ hàng đã thanh toán
    public void checkoutCart(int userId) {
        String sql = "UPDATE Cart SET status = 'CHECKOUT' WHERE userId = ? AND status = 'PENDING'";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getCartSize(int userId) {
        String sql = "SELECT * FROM Cart WHERE userId = ? AND status = 'PENDING'";
        int count = 0;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                count++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }


}
