package com.example.coursemanagement.Service;

import com.example.coursemanagement.Dto.SubmissionStatusDTO;
import com.example.coursemanagement.Repository.SubmissionRepository;
import com.example.coursemanagement.Utils.DatabaseConfig;

import java.sql.Connection;
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
}
