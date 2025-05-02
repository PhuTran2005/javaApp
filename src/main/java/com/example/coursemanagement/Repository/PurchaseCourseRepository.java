package com.example.coursemanagement.Repository;

import com.example.coursemanagement.Dto.CourseDetailDTO;
import com.example.coursemanagement.Models.Course;
import com.example.coursemanagement.Utils.SessionManager;

import java.sql.*;
import java.util.List;

public class PurchaseCourseRepository {

    // Hàm insert Enrollment vào bảng Enrollments
    public int insertEnrollment(Connection conn, int userId, int courseId) throws SQLException {
        String insertEnrollmentSQL = "INSERT INTO Enrollments (user_id, course_id, payment_status) VALUES (?, ?, 'PAID')";
        try (PreparedStatement ps = conn.prepareStatement(insertEnrollmentSQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setInt(2, courseId);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // Trả về enrollments_id từ bảng Enrollments
            }
        }
        return -1; // Nếu không thành công
    }

    // Hàm insert Order vào bảng Orders
    public int insertOrder(Connection conn, int userId, double amount) throws SQLException {
        String insertOrderSQL = "INSERT INTO Orders (user_id, total_amount, status,payment_method) VALUES (?, ?, 'Paid','Banking')";
        try (PreparedStatement ps = conn.prepareStatement(insertOrderSQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setDouble(2, amount);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // Trả về order_id từ bảng Orders
            }
        }
        return -1; // Nếu không thành công
    }

    // Hàm insert Order Detail vào bảng Order_Details
    public boolean insertOrderDetail(Connection conn, int orderId, int enrollmentId, double amount) throws SQLException {
        // Sử dụng chính xác tên cột trong database: enrollment_id
        String insertOrderDetailSQL = "INSERT INTO Order_Details (order_id, enrollments_id, price) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertOrderDetailSQL)) {
            ps.setInt(1, orderId);
            ps.setInt(2, enrollmentId);
            ps.setDouble(3, amount);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Hàm xử lý giỏ hàng (cart) với nhiều khóa học
    public boolean insertOrderDetailsForCart(Connection conn, int orderId, List<CourseDetailDTO> cart) throws SQLException {
        boolean success = true;

        // Thiết lập transaction
        conn.setAutoCommit(false);

        try {
            for (CourseDetailDTO course : cart) {
                int userId = SessionManager.getInstance().getUser().getUserId();
                int courseId = course.getCourse().getCourseId();

                // Thêm enrollment và lấy enrollment_id
                int enrollmentId = insertEnrollment(conn, userId, courseId);
                if (enrollmentId == -1) {
                    System.out.println("Lỗi khi thêm enrollment cho khóa học: " + courseId);
                    success = false;
                    break;
                }

                // Thêm order detail với enrollment_id đã lấy được
                if (!insertOrderDetail(conn, orderId, enrollmentId, course.getCourse().getCoursePrice())) {
                    System.out.println("Lỗi khi thêm order detail cho enrollment: " + enrollmentId);
                    success = false;
                    break;
                }
            }

            if (success) {
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (SQLException e) {
            System.out.println("Lỗi SQL: " + e.getMessage());
            conn.rollback();
            success = false;
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }

        return success;
    }
}