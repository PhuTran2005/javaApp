package com.example.coursemanagement.Repository;

import com.example.coursemanagement.Models.Student;
import com.example.coursemanagement.Utils.DatabaseConfig;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class StudentRepository {
    private Connection conn;

    public StudentRepository(Connection conn) {
        this.conn = conn;
    }

    public List<Student> getAllStudentsWithCourses() {
        List<Student> students = new ArrayList<>();
        String query = """
        SELECT 
            u.userId AS student_id,
            u.username AS student_name,
            u.userEmail AS student_email,
            u.userPhoneNumber AS student_phone,
            c.courseName AS course_name
        FROM Users u
        LEFT JOIN Enrollments e ON u.userId = e.userId
        LEFT JOIN Courses c ON e.courseId = c.courseId
        WHERE u.role = 'USER'
    """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int studentId = rs.getInt("student_id");
                Student student = findOrCreateStudent(students, studentId);

                student.setStudentName(rs.getString("student_name"));
                student.setStudentEmail(rs.getString("student_email"));
                student.setStudentPhone(rs.getString("student_phone"));

                String courseName = rs.getString("course_name");
                if (courseName != null && !courseName.isEmpty()) {
                    student.addCourse(courseName);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }


    private Student findOrCreateStudent(List<Student> students, int studentId) {
        for (Student student : students) {
            if (student.getStudentId() == studentId) {
                return student; // Trả về sinh viên nếu đã tồn tại
            }
        }
        // Nếu chưa có sinh viên, tạo mới và thêm vào danh sách
        Student newStudent = new Student();
        newStudent.setStudentId(studentId);
        students.add(newStudent);
        return newStudent;
    }

    // Thêm phương thức để lấy danh sách khóa học từ bảng Courses
    public List<String> getAllCourses() {
        List<String> courses = new ArrayList<>();
        String query = "SELECT courseName FROM Courses"; // Query lấy tất cả các khóa học

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                courses.add(rs.getString("courseName"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return courses;
    }

    // Cập nhật các khóa học đã chọn cho sinh viên
    public void updateStudentCourses(Student student) {
        String selectQuery = "SELECT courseId, payment_status FROM Enrollments WHERE userId = ?";
        String deleteQuery = "DELETE FROM Enrollments WHERE userId = ? AND courseId = ?";
        String insertQuery = "INSERT INTO Enrollments (userId, courseId, payment_status) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection()) {
            // 1. Lấy danh sách enrollments hiện tại từ DB
            Map<Integer, String> currentEnrollments = new HashMap<>();
            try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {
                selectStmt.setInt(1, student.getStudentId());
                ResultSet rs = selectStmt.executeQuery();
                while (rs.next()) {
                    int courseId = rs.getInt("courseId");
                    String status = rs.getString("payment_status");
                    currentEnrollments.put(courseId, status);
                }
            }

            // 2. Danh sách mới từ checkbox
            Set<Integer> newCourseIds = new HashSet<>();
            for (String courseName : student.getEnrolledCourses()) {
                int courseId = getCourseIdByName(courseName);
                if (courseId != -1) {
                    newCourseIds.add(courseId);
                }
            }

            // 3. Xóa các khóa học không còn được chọn
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                for (Integer oldCourseId : currentEnrollments.keySet()) {
                    if (!newCourseIds.contains(oldCourseId)) {
                        deleteStmt.setInt(1, student.getStudentId());
                        deleteStmt.setInt(2, oldCourseId);
                        deleteStmt.executeUpdate();
                    }
                }
            }

            String insertPaymentQuery = "INSERT INTO Payments (enrollment_id, amount, status, payment_date, method) VALUES (?, ?, ?, GETDATE(), ?)";

            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement paymentStmt = conn.prepareStatement(insertPaymentQuery)) {

                for (Integer newCourseId : newCourseIds) {
                    if (!currentEnrollments.containsKey(newCourseId)) {
                        // 4.1.1: Thêm vào Enrollments
                        insertStmt.setInt(1, student.getStudentId());
                        insertStmt.setInt(2, newCourseId);
                        insertStmt.setString(3, "PAID");
                        insertStmt.executeUpdate();

                        // 4.1.2: Lấy enrollment_id vừa tạo
                        ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            int enrollmentId = generatedKeys.getInt(1);

                            // 4.1.3: Thêm vào Payments
                            double fee = getCourseFeeById(newCourseId);
                            paymentStmt.setInt(1, enrollmentId);
                            paymentStmt.setDouble(2, fee);
                            paymentStmt.setString(3, "Success");
                            paymentStmt.setString(4, "VNPay"); // Thêm giá trị cho trường 'method'
                            paymentStmt.executeUpdate();
                        }
                    }
                }
            }



//            // (Optional) 5. Cập nhật tổng học phí (nếu muốn)
//            double totalFee = 0;
//            for (Integer courseId : newCourseIds) {
//                totalFee += getCourseFeeById(courseId); // Viết thêm hàm này nếu bạn đang dùng getCourseFee(courseName)
//            }
//            updateStudentFee(student.getStudentId(), totalFee);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Lấy học phí của khóa học
    public double getCourseFee(String courseName) {
        String query = "SELECT coursePrice FROM Courses WHERE courseName = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, courseName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("coursePrice");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private double getCourseFeeById(int courseId) {
        String query = "SELECT coursePrice FROM Courses WHERE courseId = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("coursePrice");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


//    // Cập nhật học phí của sinh viên
//    public void updateStudentFee(int studentId, double fee) {
//        String query = "UPDATE Students SET fee = ? WHERE id = ?";
//        try (Connection conn = DatabaseConfig.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(query)) {
//            stmt.setDouble(1, fee);
//            stmt.setInt(2, studentId);
//            stmt.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

    // Lấy ID của khóa học từ tên khóa học
    public int getCourseIdByName(String courseName) {
        String query = "SELECT courseId FROM Courses WHERE courseName = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, courseName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("courseId");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    //hiển thị học phí sinh viên trên UI:
    public double calculateTotalFee(int userId) {
        String query = """
        SELECT SUM(c.coursePrice)
        FROM Enrollments e
        JOIN Courses c ON e.courseId = c.courseId
        WHERE e.userId = ? AND e.payment_status = 'PAID'
    """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Student findById(int studentId) throws SQLException {
        String sql = "SELECT userId, email, name, account_balance FROM Users WHERE userId = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Student s = new Student();
                    s.setStudentId(rs.getInt("userId"));
                    s.setStudentEmail(rs.getString("email"));
                    s.setStudentName(rs.getString("name"));
                    s.setStudentPhone(rs.getString("phone"));
                    s.setStudentBalance(rs.getBigDecimal("account_balance"));
                    return s;
                }
            }
        }
        return null;
    }

    public void updateAccountBalance(int studentId, BigDecimal newBalance) throws SQLException {
        String sql = "UPDATE Users SET account_balance = ? WHERE userId = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, newBalance);
            stmt.setInt(2, studentId);
            stmt.executeUpdate();
        }
    }

}