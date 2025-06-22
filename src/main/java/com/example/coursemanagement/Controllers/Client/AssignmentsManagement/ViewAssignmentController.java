package com.example.coursemanagement.Controllers.Client.AssignmentsManagement;

import com.example.coursemanagement.Models.Assignment;
import com.example.coursemanagement.Models.Submission;
import com.example.coursemanagement.Service.AssignmentService;
import com.example.coursemanagement.Service.SubmissionService;
import com.example.coursemanagement.Utils.Alerts;
import com.example.coursemanagement.Utils.FileUtils;
import com.example.coursemanagement.Utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;

public class ViewAssignmentController {

    @FXML private Label assignmentTitle;
    @FXML private TextArea assignmentDescription;
    @FXML private Label dueDateLabel;
    @FXML private Hyperlink downloadLink;
    @FXML private Label submissionStatus;
    @FXML private Label selectedFileName;
    @FXML private Button chooseFileButton;
    @FXML private Button cancelButton;

    @FXML private Button submitButton;
    @FXML private Button updateButton;

    private Assignment assignment;
    private File selectedFile;

    private final SubmissionService submissionService = new SubmissionService();
    private final Alerts alerts = new Alerts();
    private final int studentId = SessionManager.getInstance().getUser().getUserId();

    public void setData(Assignment assignment) {
        this.assignment = assignment;

        assignmentTitle.setText(assignment.getTitle());
        assignmentDescription.setText(assignment.getDescription());
        dueDateLabel.setText("Hạn nộp: " + assignment.getDueDate());

        // Link tải file
        if (assignment.getFilePath() != null && !assignment.getFilePath().isEmpty()) {
            downloadLink.setText(assignment.getFileName() != null ? assignment.getFileName() : "Tải bài tập");
            downloadLink.setDisable(false);
            downloadLink.setOnAction(e -> FileUtils.openFile(assignment.getFilePath()));
        } else {
            downloadLink.setText("Không có file đính kèm");
            downloadLink.setDisable(true);
        }

        // Kiểm tra hạn nộp
        boolean isOverdue = assignment.getDueDate() != null && assignment.getDueDate().isBefore(java.time.LocalDateTime.now());

        // Lấy thông tin bài nộp
        Submission submission = submissionService.getSubmission(studentId, assignment.getId());
        if (submission != null) {
            submissionStatus.setText("Đã nộp vào: " + submission.getSubmittedAt());

            if (submission.getFilePath() != null) {
                selectedFileName.setText(new File(submission.getFilePath()).getName());
            }

            if (isOverdue) {
                submissionStatus.setText(submissionStatus.getText() + " (Đã quá hạn)");
                submissionStatus.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                updateButton.setVisible(false);  // Không được cập nhật
                cancelButton.setVisible(false);  // Không được hủy
            } else {
                submissionStatus.setStyle("");   // Trạng thái bình thường
                updateButton.setVisible(true);
                cancelButton.setVisible(true);   // Cho phép hủy
            }

            submitButton.setVisible(false); // Vì đã nộp
        } else {
            if (isOverdue) {
                submissionStatus.setText("❌ Quá hạn nộp bài");
                submissionStatus.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                submitButton.setVisible(false);
                updateButton.setVisible(false);
                cancelButton.setVisible(false);
            } else {
                submissionStatus.setText("Chưa nộp");
                submissionStatus.setStyle("");
                selectedFileName.setText("Chưa chọn file");

                submitButton.setVisible(true);
                updateButton.setVisible(false);
                cancelButton.setVisible(false); // Không có gì để hủy
            }
        }
    }

    @FXML
    private void handleChooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn file bài làm");
        selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            selectedFileName.setText(selectedFile.getName());
        }
    }

    @FXML
    private void handleSubmitAssignment() {
        if (selectedFile == null) {
            alerts.showErrorAlert("Vui lòng chọn file để nộp.");
            return;
        }

        boolean success = submissionService.submitAssignment(studentId, assignment.getId(), selectedFile);
        if (success) {
            alerts.showSuccessAlert("Nộp bài thành công!");
            // Đóng cửa sổ sau khi nộp thành công
            submitButton.getScene().getWindow().hide();
        } else {
            alerts.showErrorAlert("Có lỗi xảy ra khi nộp bài.");
        }
    }


    @FXML
    private void handleUpdateSubmission() {
        if (selectedFile == null) {
            alerts.showErrorAlert("Vui lòng chọn file mới để cập nhật.");
            return;
        }

        boolean success = submissionService.updateSubmission(studentId, assignment.getId(), selectedFile);
        if (success) {
            alerts.showSuccessAlert("Cập nhật bài nộp thành công!");
            updateButton.getScene().getWindow().hide(); // đóng cửa sổ
        } else {
            alerts.showErrorAlert("Có lỗi xảy ra khi cập nhật bài nộp.");
        }
    }

    @FXML
    private void handleCancelSubmission() {
        boolean confirmed = alerts.showConfirmationWarmingAlert("Xác nhận, Bạn có chắc chắn muốn hủy bài nộp không?");
        if (!confirmed) return;

        boolean success = submissionService.deleteSubmission(studentId, assignment.getId());
        if (success) {
            alerts.showSuccessAlert("Đã hủy nộp bài.");
            cancelButton.getScene().getWindow().hide(); // Đóng cửa sổ
        } else {
            alerts.showErrorAlert("Không thể hủy bài nộp.");
        }
    }


}
