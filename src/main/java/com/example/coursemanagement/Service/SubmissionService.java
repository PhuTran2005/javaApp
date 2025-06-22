package com.example.coursemanagement.Service;

import com.example.coursemanagement.Dto.SubmissionStatusDTO;
import com.example.coursemanagement.Models.Submission;
import com.example.coursemanagement.Repository.SubmissionRepository;
import com.example.coursemanagement.Utils.DatabaseConfig;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class SubmissionService {

    private final SubmissionRepository submissionRepository;

    // Constructor nhận connection từ DatabaseConfig
    public SubmissionService() {
        Connection connection = DatabaseConfig.getConnection();
        this.submissionRepository = new SubmissionRepository(connection);
    }

    public List<SubmissionStatusDTO> getSubmissionStatusForAssignment(int assignmentId) throws SQLException {
        return submissionRepository.getSubmissionStatusForAssignment(assignmentId);
    }

    //dem sluong bt
    public int getUnsubmittedCount(int courseId, int studentId) {
        return submissionRepository.countUnsubmittedAssignments(courseId, studentId);
    }

    // ✅ Lấy thông tin nộp bài
    public Submission getSubmission(int studentId, int assignmentId) {
        return submissionRepository.getSubmission(studentId, assignmentId);
    }

    // ✅ Nộp bài mới
    public boolean submitAssignment(int studentId, int assignmentId, File file) {
        return submissionRepository.insertSubmission(studentId, assignmentId, file);
    }

    // ✅ Cập nhật bài đã nộp
    public boolean updateSubmission(int studentId, int assignmentId, File file) {
        return submissionRepository.updateSubmission(studentId, assignmentId, file);
    }

    public boolean deleteSubmission(int studentId, int assignmentId) {
        String sql = "DELETE FROM Submissions WHERE student_id = ? AND assignment_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, assignmentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
