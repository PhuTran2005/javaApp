package com.example.coursemanagement.Controllers.Client;


import com.example.coursemanagement.Controllers.Client.CourseClientController.MyCourseBoxController;
import com.example.coursemanagement.Dto.CourseDetailDTO;
import com.example.coursemanagement.Repository.CoursesRepository;
import com.example.coursemanagement.Service.CourseService;
import com.example.coursemanagement.Utils.Alerts;
import com.example.coursemanagement.Utils.SessionManager;
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

public class MyCourseController {

    private final CourseService courseService = new CourseService(); // Tạo repository
    private final CoursesRepository coursesRepository = new CoursesRepository(); // Tạo repository

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
            List<CourseDetailDTO> courses = coursesRepository.getAllEnrollmentCourseDetails(SessionManager.getInstance().getUser().getUserId());
            for (CourseDetailDTO course : courses) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/HelpFxml/MyCourseBox.fxml"));
                    AnchorPane courseBox = loader.load();
                    MyCourseBoxController controller = loader.getController();
                    controller.setMyCourseController(this);
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
            List<CourseDetailDTO> courses = coursesRepository.getAllEnrollmentCourseDetailsBySearch(SessionManager.getInstance().getUser().getUserId(),query);
            for (CourseDetailDTO course : courses) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/HelpFxml/MyCourseBox.fxml"));
                    AnchorPane courseBox = loader.load();
                    MyCourseBoxController controller = loader.getController();
                    controller.setMyCourseController(this);
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