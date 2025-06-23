package com.example.coursemanagement.Controllers.Client;

import com.example.coursemanagement.Controllers.Client.AssignmentsManagement.AddAssignmentController;
import com.example.coursemanagement.Controllers.Client.CartController.AssignmentCartController;
import com.example.coursemanagement.Models.Assignment;
import com.example.coursemanagement.Models.Model;
import com.example.coursemanagement.Service.AssignmentService;
import com.example.coursemanagement.Utils.DatabaseConfig;
import com.example.coursemanagement.Utils.SessionManager;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AssignmentController {

    @FXML
    private FlowPane chartContainer;

    @FXML
    private Button addAssignment;
    @FXML
    private Button backButton;


    private int courseId = -1; // Mặc định hiển thị tất cả
    private String courseName;

    private AssignmentService assignmentService;

    public AssignmentController() {
        this.assignmentService = new AssignmentService(DatabaseConfig.getConnection());
    }

    @FXML
    public void initialize() {
        try {
            // Không load luôn ở đây nữa, vì courseId có thể chưa được set
            addAssignment.setOnAction(event -> openAddAssignmentForm());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Gọi method này sau khi set courseId
    public void initializeAssignments() {
        try {
            loadAssignmentsToChart();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Load bài tập tùy theo courseId
    private void loadAssignmentsToChart() throws SQLException {
        chartContainer.getChildren().clear();

        List<Assignment> assignments;

        if (courseId != -1) {
            // 👇 chỉ lấy bài tập theo course cụ thể
            assignments = assignmentService.getAssignmentsByCourseId(courseId);
        } else {
            // 👇 lấy theo instructor đang đăng nhập
            assignments = assignmentService.getAllAssignmentsByInstructorId(
                    SessionManager.getInstance().getUser().getUserId());
        }

        for (Assignment assignment : assignments) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/Assignment/AssignmentCart.fxml"));
                Parent cartNode = loader.load();

                AssignmentCartController cartController = loader.getController();

                cartController.setAssignmentData(
                        assignment.getTitle(),
                        assignment.getCourseName(),
                        assignment.getDueDate(),
                        assignment.getCompleted(),
                        assignment.getTotal()
                );

                cartController.setAssignment(assignment);

                // 👇 Callback khi XÓA bài tập
                cartController.setOnAssignmentDeleted(() -> {
                    try {
                        loadAssignmentsToChart();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });

                // 👇 Callback khi CẬP NHẬT bài tập
                cartController.setOnAssignmentUpdated(() -> {
                    try {
                        loadAssignmentsToChart();  // Gọi lại chính hàm này để reload toàn bộ
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


    // Mở form thêm bài tập
    @FXML
    private void openAddAssignmentForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/Assignment/AddAssignment.fxml"));
            Parent root = loader.load();

            AddAssignmentController controller = loader.getController();
            controller.setSelectedCourse(this.courseId, this.courseName);

            controller.setOnAssignmentAdded(() -> {
                try {
                    loadAssignmentsToChart(); // 👈 tự động load lại bài tập
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            Stage stage = new Stage();
            stage.setTitle("Thêm bài tập mới");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Được gọi từ CourseBoxController
    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/CourseManagement.fxml"));
            Parent myCourseView = loader.load();

            ((BorderPane) Model.getInstance().getViewFactory().getClientRoot()).setCenter(myCourseView);
            // Hiệu ứng mờ khi chuyển
            FadeTransition ft = new FadeTransition(Duration.millis(300), myCourseView);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
