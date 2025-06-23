package com.example.coursemanagement.Controllers.Client.LearningMaterial;

import com.example.coursemanagement.Controllers.Client.LearningMaterial.AddMaterialController;
import com.example.coursemanagement.Controllers.Client.LearningMaterial.LearningMaterialCardController;
import com.example.coursemanagement.Models.LearningMaterial;
import com.example.coursemanagement.Models.Model;
import com.example.coursemanagement.Repository.LearningMaterialRepository;
import com.example.coursemanagement.Utils.DatabaseConfig;
import com.example.coursemanagement.Utils.SessionManager;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

public class LearningViewController {

    @FXML private VBox materialContainer;
    @FXML private Button addButton;

    @FXML
    private Button backButton;



    private int currentCourseId;  // Gán khi truyền từ màn trước
    private int currentUserId;    // Giảng viên hiện tại
    private String courseName;  // Tên khóa học

    public int getCurrentCourseId() {
        return currentCourseId;
    }


    public int getCurrentUserId() {
        return currentUserId;
    }

    public String getCourseName() {
        return courseName;
    }

    @FXML
    public void initialize() {
        addButton.setOnAction(e -> openAddMaterialForm());
        backButton.setOnAction(event -> handleBack());

        // 🔒 Ẩn nút "Thêm" nếu là STUDENT
        if (SessionManager.getInstance().getUser().getRoleId() == 3) {
            addButton.setVisible(false);
            addButton.setManaged(false); // Ẩn cả về layout (không chiếm chỗ)
        }
    }

    public void setCourse(int courseId, int userId, String courseName) {
        this.currentCourseId = courseId;
        this.currentUserId = userId;
        this.courseName = courseName;  // Gán tên khóa học
        loadMaterials();
    }

    public void loadMaterials() {
        try (Connection conn = DatabaseConfig.getConnection()) {
            List<LearningMaterial> materials = new LearningMaterialRepository().getMaterialsByCourseId(conn, currentCourseId);
            materialContainer.getChildren().clear();

            for (LearningMaterial material : materials) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/Intructor_Learing/LearningMaterialCard.fxml"));
                Parent card = loader.load();

                LearningMaterialCardController controller = loader.getController();
                controller.setData(material);
                controller.setOnMaterialDeleted(() -> loadMaterials());

                materialContainer.getChildren().add(card);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openAddMaterialForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/Intructor_Learing/AddMaterial.fxml"));
            Parent root = loader.load();

            // Truyền courseId + userId cho AddMaterialController
            AddMaterialController controller = loader.getController();
            controller.setCourseId(currentCourseId);
            controller.setCourseName(courseName);
            controller.setUploadedByUserId(currentUserId);
            controller.setOnMaterialAdded(this::loadMaterials); // callback

            Stage stage = new Stage();
            stage.setTitle("Thêm bài giảng");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack() {
        try {
            String fxmlPath;

            if (SessionManager.getInstance().getUser().getRoleId() == 2) {
                fxmlPath = "/Fxml/Admin/CourseManagement.fxml"; // Màn instructor/admin
            } else {
                fxmlPath = "/Fxml/Client/MyCourse.fxml"; // Màn hình của sinh viên
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            ((BorderPane) Model.getInstance().getViewFactory().getClientRoot()).setCenter(view);

            // Hiệu ứng mờ khi chuyển
            FadeTransition ft = new FadeTransition(Duration.millis(400), view);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
