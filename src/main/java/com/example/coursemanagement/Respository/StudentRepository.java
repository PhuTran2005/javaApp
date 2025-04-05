package com.example.coursemanagement.Respository;

import com.example.coursemanagement.Models.Student;
import com.example.coursemanagement.Utils.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentRepository {
    public List<Student> getAllStudentsWithCourses() {
        List<Student> students = new ArrayList<>();
        String query = """
        SELECT 
            s.id AS student_id,
            s.name AS student_name,
            s.email AS student_email,
            s.phone AS student_phone,
            STRING_AGG(c.name, CHAR(13) + CHAR(10)) AS enrolled_courses
        FROM Students s
        LEFT JOIN Enrollments e ON s.id = e.student_id
        LEFT JOIN Courses c ON e.course_id = c.id
        GROUP BY s.id, s.name, s.email, s.phone
    """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Student student = new Student();
                student.setStudentId(rs.getInt("student_id"));
                student.setStudentName(rs.getString("student_name"));
                student.setStudentEmail(rs.getString("student_email"));
                student.setStudentPhone(rs.getString("student_phone"));
                student.setEnrolledCourses(rs.getString("enrolled_courses")); // Có thể split nếu muốn List
                students.add(student);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }

}
