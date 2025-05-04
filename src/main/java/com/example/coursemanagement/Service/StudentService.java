package com.example.coursemanagement.Service;

import com.example.coursemanagement.Models.Student;
import com.example.coursemanagement.Repository.StudentRepository;
import com.example.coursemanagement.Utils.DatabaseConfig;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;

public class StudentService {

    private final StudentRepository repo;

    public StudentService() {
        // Lấy kết nối từ DatabaseConfig (static method)
        Connection conn = DatabaseConfig.getConnection();
        // Khởi tạo repository với connection này
        this.repo = new StudentRepository(conn);
    }

    /** Lấy danh sách tất cả học viên kèm khóa học */
    public List<Student> getAllStudents() {
        return repo.getAllStudentsWithCourses();
    }

    /** Lấy danh sách tên tất cả khóa học */
    public List<String> getAllCourses() {
        return repo.getAllCourses();
    }

    /** Trả về số dư của học viên */
    public BigDecimal getBalance(int studentId) {
        try {
            Student s = repo.findById(studentId);
            if (s != null) {
                return s.getStudentBalance();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    /** Cộng thêm số tiền nạp vào tài khoản và cập nhật vào DB */
    public void addToBalance(int studentId, BigDecimal amount) {
        try {
            Student s = repo.findById(studentId);
            if (s != null) {
                BigDecimal newBal = s.getStudentBalance().add(amount);
                repo.updateAccountBalance(studentId, newBal);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Student getStudentById(int studentId) {
        // Giả lập dữ liệu sinh viên — bạn có thể thay bằng truy vấn DB nếu cần
        Student student = new Student();
        student.setStudentId(studentId);
        student.setStudentName("Nguyễn Văn A");
        student.setStudentEmail("nguyenvana@example.com");
        student.setStudentPhone("0123456789");
        student.getStudentBalance(); // Ví dụ: 100k VND
        return student;
    }

}
