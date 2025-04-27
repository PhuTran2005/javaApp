package com.example.coursemanagement.Repository;

import java.sql.*;

public class PurchaseCourseRepository {

    // Hàm insert Enrollment vào bảng Enrollments
    public int insertEnrollment(Connection conn, int userId, int courseId) throws SQLException {
        String insertEnrollmentSQL = "INSERT INTO Enrollments (user_id, course_id, payment_status) VALUES (?, ?, 'UNPAID')";
        try (PreparedStatement ps = conn.prepareStatement(insertEnrollmentSQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setInt(2, courseId);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // trả về ID của enrollment
            }
        }
        return -1; // Nếu không thành công
    }

    // Hàm insert Order vào bảng Orders
    public int insertOrder(Connection conn, int userId, double amount) throws SQLException {
        String insertOrderSQL = "INSERT INTO Orders (user_id, order_date, total_amount) VALUES (?, GETDATE(), ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertOrderSQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setDouble(2, amount);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // trả về ID của đơn hàng
            }
        }
        return -1; // Nếu không thành công
    }

    // Hàm insert Order Detail vào bảng Order_Details
    public boolean insertOrderDetail(Connection conn, int orderId, int enrollmentId, int courseId, double amount) throws SQLException {
        String insertOrderDetailSQL = "INSERT INTO Order_Details (order_id, enrollment_id, course_id, price, quantity) VALUES (?, ?, ?, ?, 1)";
        try (PreparedStatement ps = conn.prepareStatement(insertOrderDetailSQL)) {
            ps.setInt(1, orderId);
            ps.setInt(2, enrollmentId);
            ps.setInt(3, courseId);
            ps.setDouble(4, amount);
            ps.executeUpdate();
            return true;
        }
    }
}
