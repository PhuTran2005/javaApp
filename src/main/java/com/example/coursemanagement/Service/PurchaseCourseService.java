package com.example.coursemanagement.Service;

import com.example.coursemanagement.Repository.PurchaseCourseRepository;
import com.example.coursemanagement.Utils.DatabaseConfig;

import java.sql.Connection;
import java.sql.SQLException;

public class PurchaseCourseService {

    private final PurchaseCourseRepository repository;

    public PurchaseCourseService() {
        this.repository = new PurchaseCourseRepository();
    }

    // Phương thức để mua khóa học
    public boolean purchaseCourse(int userId, int courseId, double amount) {
        Connection conn = null;

        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);  // Bắt đầu transaction

            // 1. Insert Enrollment
            int enrollmentId = repository.insertEnrollment(conn, userId, courseId);
            if (enrollmentId == -1) {
                throw new SQLException("Lỗi khi thêm thông tin Enrollment");
            }

            // 2. Insert Order
            int orderId = repository.insertOrder(conn, userId, amount);
            if (orderId == -1) {
                throw new SQLException("Lỗi khi tạo đơn hàng");
            }

            // 3. Insert Order Detail
            if (!repository.insertOrderDetail(conn, orderId, enrollmentId, courseId, amount)) {
                throw new SQLException("Lỗi khi thêm chi tiết đơn hàng");
            }

            // Commit nếu tất cả thành công
            conn.commit();
            return true;

        } catch (SQLException e) {
            // Rollback nếu có lỗi xảy ra
            try {
                if (conn != null) conn.rollback();
                System.out.println("Đã rollback giao dịch do lỗi: " + e.getMessage());
            } catch (SQLException rollbackEx) {
                System.out.println("Lỗi rollback: " + rollbackEx.getMessage());
            }
            e.printStackTrace();
            return false;

        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);  // Reset lại auto commit
                    conn.close();  // Đóng kết nối
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
