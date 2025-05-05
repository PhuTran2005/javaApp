package com.example.coursemanagement.Repository;

import com.example.coursemanagement.Dto.SubmissionStatusDTO;
import com.example.coursemanagement.Utils.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubmissionRepository {

    private final Connection connection;

    // Constructor nhận connection từ bên ngoài
    public SubmissionRepository(Connection connection) {
        this.connection = connection;
    }

    public List<SubmissionStatusDTO> getSubmissionStatusForAssignment(int assignmentId) throws SQLException {
        String sql = """
        SELECT u.user_id as student_id, u.full_name, u.email,
               s.submitted_at, s.file_path
        FROM Users u
        JOIN Enrollments e ON u.user_id = e.user_id
        LEFT JOIN Submissions s ON s.student_id = u.user_id AND s.assignment_id = ?
        WHERE e.course_id = (
            SELECT course_id FROM Assignments WHERE assignment_id = ?
        )
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, assignmentId);
            stmt.setInt(2, assignmentId);
            ResultSet rs = stmt.executeQuery();

            List<SubmissionStatusDTO> list = new ArrayList<>();
            while (rs.next()) {
                SubmissionStatusDTO dto = new SubmissionStatusDTO();
                dto.setStudentName(rs.getString("full_name"));
                dto.setEmail(rs.getString("email"));
                dto.setSubmittedAt(rs.getTimestamp("submitted_at") != null ? rs.getTimestamp("submitted_at").toLocalDateTime() : null);
                dto.setFilePath(rs.getString("file_path"));
                dto.setSubmitted(dto.getSubmittedAt() != null);
                list.add(dto);
            }
            return list;
        }
    }
}
