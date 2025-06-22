package com.example.coursemanagement.Repository;

import com.example.coursemanagement.Dto.SubmissionStatusDTO;
import com.example.coursemanagement.Models.Submission;
import com.example.coursemanagement.Utils.DatabaseConfig;

import java.io.File;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SubmissionRepository {

    public int countUnsubmittedAssignments(int courseId, int studentId) {
        String sql = """
        SELECT COUNT(*) AS total
        FROM Assignments a
        LEFT JOIN Submissions s 
            ON a.assignment_id = s.assignment_id AND s.student_id = ?
        WHERE a.course_id = ? AND s.submission_id IS NULL
    """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


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

    public Submission getSubmission(int studentId, int assignmentId) {
        String sql = """
        SELECT * FROM Submissions
        WHERE student_id = ? AND assignment_id = ?
    """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, assignmentId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Submission submission = new Submission();
                submission.setSubmissionId(rs.getInt("submission_id"));
                submission.setStudentId(rs.getInt("student_id"));
                submission.setAssignmentId(rs.getInt("assignment_id"));
                submission.setFilePath(rs.getString("file_path"));
                submission.setSubmittedAt(Timestamp.valueOf(rs.getTimestamp("submitted_at").toLocalDateTime()));
                return submission;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insertSubmission(int studentId, int assignmentId, File file) {
        String sql = """
        INSERT INTO Submissions (student_id, assignment_id, file_path, submitted_at)
        VALUES (?, ?, ?, ?)
    """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, assignmentId);
            stmt.setString(3, file.getAbsolutePath());
            stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateSubmission(int studentId, int assignmentId, File file) {
        String sql = """
        UPDATE Submissions
        SET file_path = ?, submitted_at = ?
        WHERE student_id = ? AND assignment_id = ?
    """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, file.getAbsolutePath());
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(3, studentId);
            stmt.setInt(4, assignmentId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
