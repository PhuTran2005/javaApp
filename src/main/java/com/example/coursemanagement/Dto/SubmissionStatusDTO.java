package com.example.coursemanagement.Dto;

import java.time.LocalDateTime;

public class SubmissionStatusDTO {
    private String studentName;
    private String email;
    private boolean submitted;
    private LocalDateTime submittedAt;
    private String filePath;

    public SubmissionStatusDTO() {
    }

    public SubmissionStatusDTO(String studentName, String email, boolean submitted, LocalDateTime submittedAt, String filePath) {
        this.studentName = studentName;
        this.email = email;
        this.submitted = submitted;
        this.submittedAt = submittedAt;
        this.filePath = filePath;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
