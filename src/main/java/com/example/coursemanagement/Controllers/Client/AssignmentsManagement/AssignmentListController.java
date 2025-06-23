package com.example.coursemanagement.Controllers.Client.AssignmentsManagement;

import com.example.coursemanagement.Models.Assignment;
import com.example.coursemanagement.Models.Model;
import com.example.coursemanagement.Service.AssignmentService;
import com.example.coursemanagement.Utils.DatabaseConfig;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.Parent;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

public class AssignmentListController {

    @FXML
    private VBox assignmentContainer;

    private final AssignmentService assignmentService = new AssignmentService(DatabaseConfig.getConnection());

    /**
     * Khởi tạo danh sách bài tập cho một khóa học cụ thể.
     *
     * @param courseId ID của khóa học cần load bài tập
     */
    public void initialize(int courseId) {
        try {
            // Lấy danh sách bài tập từ service
            List<Assignment> assignments = assignmentService.getAssignmentsByCourseId(courseId);

            // Xóa nội dung cũ (nếu có)
            assignmentContainer.getChildren().clear();

            // Duyệt từng bài tập và load FXML cho từng item
            for (Assignment assignment : assignments) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(
                        "/Fxml/Client/Assignment/AssignmentItem.fxml"));

                Node itemNode = loader.load();

                // Lấy controller của AssignmentItem và gán dữ liệu
                AssignmentItemController itemController = loader.getController();
                itemController.setData(assignment);

                // Thêm vào danh sách hiển thị
                assignmentContainer.getChildren().add(itemNode);
            }

        } catch (IOException e) {
            e.printStackTrace();
            // Optionally: hiển thị cảnh báo lỗi cho người dùng
        }
    }
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/MyCourse.fxml"));
            Parent myCourseView = loader.load();

            ((BorderPane) Model.getInstance().getViewFactory().getClientRoot()).setCenter(myCourseView);

            // Hiệu ứng mờ khi chuyển
            FadeTransition ft = new FadeTransition(Duration.millis(400), myCourseView);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
