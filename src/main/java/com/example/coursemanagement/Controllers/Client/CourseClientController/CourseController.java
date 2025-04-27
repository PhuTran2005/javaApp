package com.example.coursemanagement.Controllers.Client.CourseClientController;


import com.example.coursemanagement.Dto.CourseDetailDTO;
import com.example.coursemanagement.Service.CourseService;
import com.example.coursemanagement.Utils.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;


import java.io.IOException;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class CourseController {

    private final CourseService courseService = new CourseService(); // Tạo repository
    private Alerts alerts = new Alerts();
    // Thêm thuộc tính này trong CourseController:
    private Timeline searchDelay;
    @FXML

    public ScrollPane listCourse;
    @FXML

    public FlowPane courseContainer;
    @FXML
    public TextField searchField;

    // Box Khóa học
    public void initialize() {
        loadCoursesList();
        setupSearchFieldListener(); // Gọi hàm setup search khi khởi tạo
    }

    private void setupSearchFieldListener() {
        searchDelay = new Timeline(new KeyFrame(Duration.millis(300), e -> handleSearch()));
        searchDelay.setCycleCount(1); // Chạy 1 lần duy nhất mỗi lần gõ

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (searchDelay != null) {
                searchDelay.stop();
            }
            searchDelay.playFromStart();
        });
    }

    public void loadCoursesList() {
        if (courseContainer != null) {
            List<CourseDetailDTO> courses = courseService.getCourseList(0);
            for (CourseDetailDTO course : courses) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/HelpFxml/CourseBoxExtra.fxml"));
                    AnchorPane courseBox = loader.load();
                    CourseBoxExtraController controller = loader.getController();
                    controller.setCourseController(this);
                    controller.setData(course);
                    courseContainer.getChildren().add(courseBox);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void loadCoursesListBySearch(String query) {
        if (courseContainer != null) {
            List<CourseDetailDTO> courses = courseService.getCourseListByName(query);
            for (CourseDetailDTO course : courses) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/HelpFxml/CourseBoxExtra.fxml"));
                    AnchorPane courseBox = loader.load();
                    CourseBoxExtraController controller = loader.getController();
                    controller.setCourseController(this);
                    controller.setData(course);
                    courseContainer.getChildren().add(courseBox);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void loadCoursesListByFilter(List<CourseDetailDTO> courses) {
        if (courseContainer != null) {
            for (CourseDetailDTO course : courses) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/HelpFxml/CourseBoxExtra.fxml"));
                    AnchorPane courseBox = loader.load();
                    CourseBoxExtraController controller = loader.getController();
                    controller.setCourseController(this);
                    controller.setData(course);
                    courseContainer.getChildren().add(courseBox);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void refreshCourseList() {
        if (courseContainer != null) {
            courseContainer.getChildren().clear(); // Xóa hết các course đang hiển thị
        }        // Tải lại từ CSDL
    }

    @FXML
    public void handleFillter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/HelpFxml/FilterCourse.fxml"));
            Parent root = loader.load();

            FilterCourseController filterCourseController = loader.getController();
            filterCourseController.setCourseController(this);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Lọc Khóa Học");
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Sau khi đóng popup, kiểm tra nếu đã thêm
            if (filterCourseController.isCourseAdded()) {
                refreshCourseList();
                // <== Tải mới
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @FXML
    public void handleSearch() {
        String query = searchField.getText().trim();
        if (!query.isEmpty()) {
            refreshCourseList();
            loadCoursesListBySearch(query);
        } else {
            refreshCourseList();
            loadCoursesList();
        }
    }

    @FXML
    public void handleClear() {
        refreshCourseList();
        searchField.setText("");
        loadCoursesList();
    }
}
