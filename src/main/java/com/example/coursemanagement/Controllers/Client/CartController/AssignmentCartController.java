package com.example.coursemanagement.Controllers.Client.CartController;

import com.example.coursemanagement.Controllers.Client.AssignmentsManagement.SubmissionListController;
import com.example.coursemanagement.Controllers.Client.AssignmentsManagement.UpdateAssignmentController;
import com.example.coursemanagement.Models.Assignment;
import com.example.coursemanagement.Service.AssignmentService;
import com.example.coursemanagement.Utils.DatabaseConfig;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AssignmentCartController {

    @FXML
    private PieChart assignmentChart;

    @FXML
    private Label assignmentTitleLabel;

    @FXML
    private Label courseNameLabel;

    @FXML
    private Label deadlineLabel;

    @FXML
    private Button viewButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    private AssignmentService assignmentService;

    private Assignment assignment;

    private Runnable onAssignmentDeleted;  // Callback sau khi xóa

    public AssignmentCartController() {
        this.assignmentService = new AssignmentService(DatabaseConfig.getConnection());
    }

    // Setter để truyền assignment từ controller cha
    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    // Setter callback
    public void setOnAssignmentDeleted(Runnable callback) {
        this.onAssignmentDeleted = callback;
    }

    // Xử lý khi nhấn nút Xem
    @FXML
    private void handleView() {
        if (assignment != null) {
            try {
                // Load the FXML file
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/Assignment/SubmissionList.fxml"));
                Parent root = loader.load();

                // Get the controller of the new window (SubmissionListController)
                SubmissionListController controller = loader.getController();

                // Pass the assignmentId to the controller to load the list of submissions
                controller.loadSubmissionsForAssignment(assignment.getId());

                // Create a new Stage for the new window
                Stage stage = new Stage();
                stage.setTitle("Danh sách nộp bài");
                stage.setScene(new Scene(root));
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                showError("Lỗi khi mở danh sách nộp bài!");
            }
        } else {
            showError("Vui lòng chọn bài tập.");
        }
    }


    // Xử lý khi nhấn nút Cập nhật
    @FXML
    private void handleUpdate() {
        if (assignment != null) {
            try {
                // Mở cửa sổ UpdateAssignmentController để chỉnh sửa bài tập
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/Assignment/UpdateAssignment.fxml"));
                Parent root = loader.load();

                // Lấy đối tượng controller của cửa sổ UpdateAssignment
                UpdateAssignmentController updateController = loader.getController();

                // Truyền dữ liệu bài tập cần cập nhật
                updateController.setAssignmentData(assignment);
                // Mở cửa sổ mới
                Stage stage = new Stage();
                stage.setTitle("Cập nhật bài tập");
                stage.setScene(new Scene(root));
                stage.showAndWait();

                refreshAssignmentList();

            } catch (IOException e) {
                e.printStackTrace();
                showError("Lỗi khi mở cửa sổ cập nhật bài tập.");
            }
        } else {
            showError("Vui lòng chọn một bài tập để cập nhật.");
        }
    }

    private void refreshAssignmentList() {
    }

    // Gán dữ liệu vào Card
    public void setAssignmentData(String title, String courseName, LocalDateTime deadline, int completed, int total) {
        assignmentTitleLabel.setText(title);
        courseNameLabel.setText(courseName);
        if (deadline != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            deadlineLabel.setText(deadline.format(formatter));
        } else {
            deadlineLabel.setText("Chưa có hạn");
        }

        assignmentChart.getData().clear();

        int remaining = total - completed;
        double percentDone = total > 0 ? (completed * 100.0 / total) : 0;
        double percentRemain = 100.0 - percentDone;

        PieChart.Data done = new PieChart.Data(String.format("Đã nộp (%.1f%%)", percentDone), completed);
        PieChart.Data remain = new PieChart.Data(String.format("Chưa nộp (%.1f%%)", percentRemain), remaining);

        assignmentChart.getData().addAll(done, remain);
    }

    // Xử lý khi nhấn nút Xóa
    @FXML
    private void handleDelete() {
        if (assignment != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Xác nhận xóa");
            alert.setHeaderText("Bạn có chắc chắn muốn xóa bài tập này?");
            alert.setContentText("Bài tập sẽ bị xóa vĩnh viễn.");

            alert.showAndWait().ifPresent(response -> {
                if (response.getButtonData().isDefaultButton()) {
                    try {
                        assignmentService.deleteAssignment(assignment.getId());

                        // Gọi callback để reload lại danh sách
                        if (onAssignmentDeleted != null) {
                            onAssignmentDeleted.run();
                        }

                        System.out.println("Đã xóa bài tập: " + assignment.getTitle());

                    } catch (SQLException e) {
                        e.printStackTrace();
                        showError("Lỗi khi xóa bài tập!");
                    }
                }
            });
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
