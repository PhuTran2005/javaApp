package com.example.coursemanagement.Service;

import com.example.coursemanagement.Dto.CourseDetailDTO;
import com.example.coursemanagement.Models.Course;
import com.example.coursemanagement.Repository.PurchaseCourseRepository;
import com.example.coursemanagement.Utils.DatabaseConfig;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class PurchaseCourseService {

    private final PurchaseCourseRepository repository;

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    private double total = 0;

    public PurchaseCourseService() {
        this.repository = new PurchaseCourseRepository();
    }

    // Hàm xử lý thanh toán cho giỏ hàng (cart) có thể nhiều khóa học hoặc 1 khóa học
    public int purchaseCoursesFromCart(int userId, List<CourseDetailDTO> cart) {
        Connection conn = null;

        // Nếu giỏ hàng có một khóa học duy nhất, quy trình vẫn xử lý như bình thường
        double totalAmount = total;

        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);  // Bắt đầu transaction

            // 1. Insert Order
            int orderId = repository.insertOrder(conn, userId, totalAmount);
            if (orderId == -1) {
                throw new SQLException("Lỗi khi tạo đơn hàng");
            }

            // 2. Insert Order Details cho tất cả khóa học trong giỏ hàng (dù chỉ có một khóa học)
            if (!repository.insertOrderDetailsForCart(conn, orderId, cart)) {
                throw new SQLException("Lỗi khi thêm chi tiết đơn hàng");
            }

            // Commit nếu tất cả thành công
            conn.commit();
            return orderId;

        } catch (SQLException e) {
            // Rollback nếu có lỗi xảy ra
            try {
                if (conn != null) conn.rollback();
                System.out.println("Đã rollback giao dịch do lỗi: " + e.getMessage());
            } catch (SQLException rollbackEx) {
                System.out.println("Lỗi rollback: " + rollbackEx.getMessage());
            }
            e.printStackTrace();
            return -1;

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

    // Hàm xử lý thanh toán khi chỉ có một khóa học (mua ngay)
//    public boolean purchaseSingleCourse(int userId, CourseDetailDTO course) {
//        // Tạo một list chứa chỉ 1 khóa học và gọi lại phương thức purchaseCoursesFromCart
//        return purchaseCoursesFromCart(userId, Collections.singletonList(course));
//    }
}
