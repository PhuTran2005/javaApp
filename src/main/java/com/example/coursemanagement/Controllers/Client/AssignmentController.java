package com.example.coursemanagement.Controllers.Client;

import com.example.coursemanagement.Controllers.Client.AssignmentsManagement.AddAssignmentController;
import com.example.coursemanagement.Controllers.Client.CartController.AssignmentCartController;
import com.example.coursemanagement.Models.Assignment;
import com.example.coursemanagement.Service.AssignmentService;
import com.example.coursemanagement.Utils.DatabaseConfig;
import com.example.coursemanagement.Utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AssignmentController {

    @FXML
    private FlowPane chartContainer;

    @FXML
    private Button addAssignment;

    private AssignmentService assignmentService;

    public AssignmentController() {
        this.assignmentService = new AssignmentService(DatabaseConfig.getConnection());
    }

    @FXML
    public void initialize() {
        try {
            loadAssignmentsToChart();

            // Gắn sự kiện click nút "Add Assignment"
            addAssignment.setOnAction(event -> openAddAssignmentForm());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Tải danh sách bài tập và hiển thị bằng PieChart
    private void loadAssignmentsToChart() throws SQLException {
        chartContainer.getChildren().clear(); // Xóa các cart cũ

        List<Assignment> assignments = assignmentService.getAllAssignmentsByInstructorId(SessionManager.getInstance().getUser().getUserId());
        for (Assignment assignment : assignments) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/Assignment/AssignmentCart.fxml"));
                Parent cartNode = loader.load();

                AssignmentCartController cartController = loader.getController();

                // Gán dữ liệu cho cart
                cartController.setAssignmentData(
                        assignment.getTitle(),
                        assignment.getCourseName(),
                        assignment.getDueDate(),
                        assignment.getCompleted(),
                        assignment.getTotal()
                );

                // Gán assignment và callback để xóa
                cartController.setAssignment(assignment); // Gán đối tượng Assignment cho cart
                cartController.setOnAssignmentDeleted(() -> {
                    try {
                        loadAssignmentsToChart(); // Reload khi xóa thành công
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });

                chartContainer.getChildren().add(cartNode);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    // Mở form thêm bài tập và cập nhật biểu đồ khi thêm xong
    @FXML
    private void openAddAssignmentForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/Assignment/AddAssignment.fxml"));
            Parent root = loader.load();

            // Truyền callback để cập nhật biểu đồ sau khi thêm bài tập
            AddAssignmentController controller = loader.getController();
            controller.setOnAssignmentAdded(() -> {
                try {
                    loadAssignmentsToChart();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            Stage stage = new Stage();
            stage.setTitle("Add New Assignment");
            stage.initModality(Modality.APPLICATION_MODAL); // Chặn cửa sổ khác khi đang mở form
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
