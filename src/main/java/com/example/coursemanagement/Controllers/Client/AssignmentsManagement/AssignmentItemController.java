package com.example.coursemanagement.Controllers.Client.AssignmentsManagement;

import com.example.coursemanagement.Models.Assignment;
import com.example.coursemanagement.Models.Submission;
import com.example.coursemanagement.Service.SubmissionService;
import com.example.coursemanagement.Utils.FileUtils;
import com.example.coursemanagement.Utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AssignmentItemController {

    @FXML private Label assignmentTitle;
    @FXML private Label dueDate;
    @FXML private TextArea descriptionArea;
    @FXML private Label submissionStatus;
    @FXML private Label submittedFileLabel;
    @FXML private Button submitButton;
    @FXML private Hyperlink downloadLink;

    private Assignment assignment;
    private final SubmissionService submissionService = new SubmissionService();
    private final int studentId = SessionManager.getInstance().getUser().getUserId();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void setData(Assignment assignment) {
        this.assignment = assignment;

        // Hiển thị tiêu đề
        assignmentTitle.setText(assignment.getTitle());

        // Hạn nộp (null-safe)
        LocalDateTime due = assignment.getDueDate();
        dueDate.setText("🕒 Hạn nộp: " + (due != null ? due.format(formatter) : "Không rõ"));

        // Mô tả
        descriptionArea.setText("🗒 " + assignment.getDescription());

        // File từ giảng viên
        if (assignment.getFilePath() != null && !assignment.getFilePath().isEmpty()) {
            downloadLink.setText("🔽 " + (assignment.getFileName() != null ? assignment.getFileName() : "Tải bài tập"));
            downloadLink.setOnAction(e -> FileUtils.openFile(assignment.getFilePath()));
        } else {
            downloadLink.setText("Không có file bài tập");
            downloadLink.setDisable(true);
        }

        // Trạng thái nộp bài
        Submission submission = submissionService.getSubmission(studentId, assignment.getId());
        if (submission != null) {
            String time = submission.getSubmittedAt() != null
                    ? submission.getSubmittedAt().toLocalDateTime().format(formatter)
                    : "Không rõ thời gian";
            submissionStatus.setText("✅ Đã nộp: " + time);

            if (submission.getFilePath() != null) {
                submittedFileLabel.setText("📎 " + new File(submission.getFilePath()).getName());
            } else {
                submittedFileLabel.setText("📎 (Không có file)");
            }

            submitButton.setText("Cập nhật bài nộp");
        } else {
            submissionStatus.setText("❌ Chưa nộp");
            submittedFileLabel.setText("");
            submitButton.setText("📤 Nộp bài");
        }

        // Sự kiện click nút nộp hoặc cập nhật
        submitButton.setOnAction(e -> openSubmitForm());
    }

    private void openSubmitForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/Assignment/ViewAssignment.fxml"));
            AnchorPane pane = loader.load();
            ViewAssignmentController controller = loader.getController();
            controller.setData(assignment);

            javafx.scene.Scene scene = new javafx.scene.Scene(pane);
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("📘 Chi tiết bài tập");
            stage.setScene(scene);

            // Show and wait
            stage.showAndWait();

            // Sau khi form đóng lại thì cập nhật lại trạng thái
            setData(assignment); // cập nhật lại nội dung bài tập

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
