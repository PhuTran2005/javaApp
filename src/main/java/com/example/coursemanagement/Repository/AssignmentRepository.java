package com.example.coursemanagement.Repository;

import com.example.coursemanagement.Dto.SubmissionStatusDTO;
import com.example.coursemanagement.Models.Assignment;
import com.example.coursemanagement.Utils.DatabaseConfig;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AssignmentRepository {

    private Connection connection;

    // Constructor nhận Connection
    public AssignmentRepository(Connection connection) {
        this.connection = connection;
    }

    // Lấy danh sách tất cả các bài tập
    public List<Assignment> getAllAssignments() {
        List<Assignment> assignments = new ArrayList<>();

        // Câu lệnh SQL để lấy danh sách bài tập và số lượng học viên hoàn thành và tổng học viên
        String sql = "SELECT " +
                "a.assignment_id, " +
                "a.title, " +
                "a.description, " +
                "a.file_path, " +
                "a.file_name," +
                "c.course_name, " +
                "a.course_id, " +
                "a.due_date, " +
                "COUNT(DISTINCT s.student_id) AS completed, " +
                "COUNT(DISTINCT e.user_id) AS total " +
                "FROM Assignments a " +
                "JOIN Courses c ON a.course_id = c.course_id " +
                "LEFT JOIN Submissions s ON a.assignment_id = s.assignment_id " +
                "LEFT JOIN Enrollments e ON a.course_id = e.course_id " +
                "GROUP BY a.assignment_id, a.title,a.description, a.file_path,a.file_name, c.course_name, a.course_id, a.due_date " +
                "ORDER BY a.assignment_id DESC";


        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Lặp qua kết quả truy vấn và tạo các đối tượng Assignment
            while (rs.next()) {
                Assignment assignment = new Assignment();
                assignment.setId(rs.getInt("assignment_id"));
                assignment.setTitle(rs.getString("title"));
                assignment.setDescription(rs.getString("description"));
                assignment.setFilePath(rs.getString("file_path"));
                assignment.setFileName(rs.getString("file_name"));
                assignment.setCourseName(rs.getString("course_name"));
                assignment.setCourseId(rs.getInt("course_id"));
                Timestamp timestamp = rs.getTimestamp("due_date");
                if (timestamp != null) {
                    assignment.setDueDate(timestamp.toLocalDateTime());
                }

                assignment.setCompleted(rs.getInt("completed"));
                assignment.setTotal(rs.getInt("total"));
                assignments.add(assignment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return assignments;
    }

    public List<Assignment> getAllAssignmentsByInstructorId(int instructorId) {
        List<Assignment> assignments = new ArrayList<>();

        // Câu lệnh SQL để lấy danh sách bài tập và số lượng học viên hoàn thành và tổng học viên
        String sql = "SELECT " +
                "a.assignment_id, " +
                "a.title, " +
                "a.description, " +
                "a.file_path, " +
                "a.file_name," +
                "c.course_name, " +
                "a.course_id, " +
                "a.due_date, " +
                "COUNT(DISTINCT s.student_id) AS completed, " +
                "COUNT(DISTINCT e.user_id) AS total " +
                "FROM Assignments a " +
                "JOIN Courses c ON a.course_id = c.course_id " +
                "LEFT JOIN Submissions s ON a.assignment_id = s.assignment_id " +
                "LEFT JOIN Enrollments e ON a.course_id = e.course_id " +
                "where a.teacher_id = ? " +
                "GROUP BY a.assignment_id, a.title,a.description, a.file_path,a.file_name, c.course_name, a.course_id, a.due_date " +
                "ORDER BY a.assignment_id DESC";


        try (Connection conn = DatabaseConfig.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, instructorId);
            ResultSet rs = stmt.executeQuery();

            // Lặp qua kết quả truy vấn và tạo các đối tượng Assignment
            while (rs.next()) {
                Assignment assignment = new Assignment();
                assignment.setId(rs.getInt("assignment_id"));
                assignment.setTitle(rs.getString("title"));
                assignment.setDescription(rs.getString("description"));
                assignment.setFilePath(rs.getString("file_path"));
                assignment.setFileName(rs.getString("file_name"));
                assignment.setCourseName(rs.getString("course_name"));
                assignment.setCourseId(rs.getInt("course_id"));
                Timestamp timestamp = rs.getTimestamp("due_date");
                if (timestamp != null) {
                    assignment.setDueDate(timestamp.toLocalDateTime());
                }

                assignment.setCompleted(rs.getInt("completed"));
                assignment.setTotal(rs.getInt("total"));
                assignments.add(assignment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return assignments;
    }

}
