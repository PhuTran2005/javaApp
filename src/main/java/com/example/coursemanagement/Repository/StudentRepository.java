package com.example.coursemanagement.Repository;

import com.example.coursemanagement.Models.Instructor;
import com.example.coursemanagement.Models.Student;
import com.example.coursemanagement.Utils.DatabaseConfig;

import java.sql.*;
import java.util.*;

public class StudentRepository {

    public List<Student> getAllStudentsWithCourses() {
        List<Student> students = new ArrayList<>();
        String query =
                """
                        SELECT 
                            u.user_id AS student_id,
                            u.full_name AS student_name,
                            u.email AS student_email,
                            u.phonenumber AS student_phone,
                            c.course_name AS course_name
                        FROM Users u
                        LEFT JOIN Enrollments e ON u.user_id = e.user_id
                        LEFT JOIN Courses c ON e.course_id = c.course_id
                        WHERE u.role_id = 3
                        """;


        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int studentId = rs.getInt("student_id");
                Student student = findOrCreateStudent(students, studentId);

                student.setFullname(rs.getString("student_name"));
                student.setUserEmail(rs.getString("student_email"));
                student.setUserPhoneNumber(rs.getString("student_phone"));

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
            if (student.getUserId() == studentId) {
                return student; // Trả về sinh viên nếu đã tồn tại
            }
        }
        // Nếu chưa có sinh viên, tạo mới và thêm vào danh sách
        Student newStudent = new Student();
        newStudent.setUserId(studentId);
        students.add(newStudent);
        return newStudent;
    }

    // Thêm phương thức để lấy danh sách khóa học từ bảng Courses
    public List<String> getAllCourses() {
        List<String> courses = new ArrayList<>();
        String query = "SELECT course_name FROM Courses"; // Query lấy tất cả các khóa học

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                courses.add(rs.getString("course_name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return courses;
    }

    // Cập nhật các khóa học đã chọn cho sinh viên
    public void updateStudentCourses(Student student) {
        String selectQuery = "SELECT course_id, payment_status FROM Enrollments WHERE user_id = ?";
        String deleteQuery = "DELETE FROM Enrollments WHERE enrollments_id = ? AND course_id = ?";
        String insertQuery = "INSERT INTO Enrollments (user_id, course_id, payment_status) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection()) {
            // 1. Lấy danh sách enrollments hiện tại từ DB
            Map<Integer, String> currentEnrollments = new HashMap<>();
            try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {
                selectStmt.setInt(1, student.getUserId());
                ResultSet rs = selectStmt.executeQuery();
                while (rs.next()) {
                    int courseId = rs.getInt("course_id");
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
                        deleteStmt.setInt(1, student.getUserId());
                        deleteStmt.setInt(2, oldCourseId);
                        deleteStmt.executeUpdate();
                    }
                }
            }

            String insertPaymentQuery = "INSERT INTO Payments (payments_id, amount, status, payment_date, method) VALUES (?, ?, ?, GETDATE(), ?)";

            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement paymentStmt = conn.prepareStatement(insertPaymentQuery)) {

                for (Integer newCourseId : newCourseIds) {
                    if (!currentEnrollments.containsKey(newCourseId)) {
                        // 4.1.1: Thêm vào Enrollments
                        insertStmt.setInt(1, student.getUserId());
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
        String query = "SELECT fee FROM Courses WHERE course_name = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, courseName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("fee");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private double getCourseFeeById(int courseId) {
        String query = "SELECT fee FROM Courses WHERE course_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("fee");
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
        String query = "SELECT course_id FROM Courses WHERE course_name = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, courseName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("course_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    //hiển thị học phí sinh viên trên UI:
    public double calculateTotalFee(int userId) {
        String query = """
                    SELECT SUM(c.fee)
                    FROM Enrollments e
                    JOIN Courses c ON e.course_id = c.course_id
                    WHERE e.user_id = ? AND e.payment_status = 'PAID'
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

    public Student getStudentByEmail(String email) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String sql = "SELECT * FROM users u " +
                    "join Students s on s.student_id = u.user_id " +
                    "WHERE u.email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Student(rs.getInt("user_id"), rs.getString("email"), rs.getString("full_name"), rs.getInt("role_id"), rs.getString("phonenumber"), rs.getString("create_date"), rs.getString("class"), rs.getInt("enrollment_year"), rs.getFloat("gpa"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateInforStudent(Student student) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false); // Bắt đầu transaction

            // Cập nhật bảng Users
            String userSql = "UPDATE Users SET full_name = ?, phonenumber = ? WHERE user_id = ?";
            try (PreparedStatement userStmt = conn.prepareStatement(userSql)) {
                userStmt.setString(1, student.getFullname());
                userStmt.setString(2, student.getUserPhoneNumber());
                userStmt.setInt(3, student.getUserId());
                if (userStmt.executeUpdate() == 0) {
                    conn.rollback(); // Nếu không có dòng nào được cập nhật
                    return false;
                }
            }

            // Cập nhật bảng Students
            String studentSql = "UPDATE Students SET class = ?, enrollment_year = ?, gpa = ? WHERE student_id = ?";
            try (PreparedStatement studentStmt = conn.prepareStatement(studentSql)) {
                studentStmt.setString(1, student.getClasses());
                studentStmt.setInt(2, student.getEnrollment_year());
                studentStmt.setFloat(3, student.getGpa());
                studentStmt.setInt(4, student.getUserId());
                if (studentStmt.executeUpdate() == 0) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit(); // Mọi lệnh đều thành công -> commit thay đổi
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                // Nếu có lỗi xảy ra -> rollback toàn bộ
                if (!e.getSQLState().equals("08003")) { // Tránh rollback nếu kết nối đã đóng
                    DatabaseConfig.getConnection().rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }

    public List<Student> getStudentsByCourseId(int courseId) {
        List<Student> students = new ArrayList<>();
        String query = "SELECT u.user_id, u.full_name, u.email, " +
                "CAST(100.0 * COUNT(DISTINCT s.assignment_id) / " +
                "NULLIF(COUNT(DISTINCT a.assignment_id), 0) AS FLOAT) AS progress " +
                "FROM Users u " +
                "LEFT JOIN Enrollments e ON u.user_id = e.user_id " +
                "LEFT JOIN Assignments a ON e.course_id = a.course_id " +
                "LEFT JOIN Submissions s ON u.user_id = s.student_id AND s.assignment_id = a.assignment_id " +
                "WHERE u.role_id = 3 AND e.course_id = ? " +  // Thêm điều kiện lọc theo courseId
                "GROUP BY u.user_id, u.full_name, u.email";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, courseId);  // Đặt giá trị courseId vào câu truy vấn

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int userId = rs.getInt("user_id");
                    String fullname = rs.getString("full_name");
                    String email = rs.getString("email");
                    float progress = rs.getFloat("progress");

                    Student student = new Student();
                    student.setUserId(userId);
                    student.setFullname(fullname);
                    student.setUserEmail(email);
                    student.setProgress(progress);

                    students.add(student);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }



}