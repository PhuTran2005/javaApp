package com.example.coursemanagement.Service;

import com.example.coursemanagement.Models.Assignment;
import com.example.coursemanagement.Repository.AssignmentRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssignmentService {

    private Connection connection; // Thêm connection vào lớp này
    private AssignmentRepository assignmentRepository;

    // Khởi tạo với Connection
    public AssignmentService(Connection connection) {
        this.connection = connection;  // Gán giá trị cho connection
        this.assignmentRepository = new AssignmentRepository(connection);  // Truyền connection vào AssignmentRepository
    }

    // Lấy tất cả các bài tập
    public List<Assignment> getAllAssignments() throws SQLException {
        return assignmentRepository.getAllAssignments();
    }

    public List<Assignment> getAllAssignmentsByInstructorId(int instructorId) throws SQLException {
        return assignmentRepository.getAllAssignmentsByInstructorId(instructorId);
    }

    // Thêm bài tập mới
    public void addAssignment(String title, String description,int teacherId, int courseId, Date dueDate, String fileName, String filePath) throws SQLException {
        String sql = "INSERT INTO assignments (title, description, course_id, due_date, file_name, file_path,teacher_id) VALUES (?, ?, ?, ?, ?, ?,?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, title);
            statement.setString(2, description);
            statement.setInt(3, courseId);
            statement.setDate(4, dueDate);
            statement.setInt(7, teacherId);

            // Set fileName and filePath
            statement.setString(5, fileName);

            // If filePath is null, we use setNull for the filePath column
            if (filePath != null) {
                statement.setString(6, filePath);
            } else {
                statement.setNull(6, java.sql.Types.VARCHAR);  // If there's no filePath, set it to NULL
            }

            statement.executeUpdate();
        }
    }


    // Lấy danh sách tên khóa học
    public List<String> getAllCourseNames() throws SQLException {
        List<String> courseNames = new ArrayList<>();
        String sql = "SELECT course_name FROM courses";  // Thay đổi tên bảng và cột nếu cần

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                courseNames.add(resultSet.getString("course_name"));
            }
        }

        return courseNames;
    }

    // Lấy courseId từ tên khóa học
    public int getCourseIdByName(String courseName) throws SQLException {
        String sql = "SELECT course_id FROM courses WHERE course_name = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, courseName);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("course_id");
            } else {
                throw new SQLException("Không tìm thấy khóa học với tên: " + courseName);
            }
        }
    }

    // Xóa bài tập theo ID
    public void deleteAssignment(int assignmentId) throws SQLException {
        String sql = "DELETE FROM assignments WHERE assignment_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, assignmentId);
            statement.executeUpdate();
        }
    }

    // Cập nhật bài tập theo ID
    public void updateAssignment(Assignment assignment) throws SQLException {
        String sql = "UPDATE assignments SET title = ?, description = ?, course_id = ?, due_date = ?, file_name = ? WHERE assignment_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, assignment.getTitle());
            statement.setString(2, assignment.getDescription());
            statement.setInt(3, assignment.getCourseId());
            statement.setTimestamp(4, Timestamp.valueOf(assignment.getDueDate()));  // Convert LocalDateTime to Timestamp
            statement.setString(5, assignment.getFilePath());  // Nếu bạn có fileName
            statement.setInt(6, assignment.getId());

            statement.executeUpdate();
        }
    }
}
