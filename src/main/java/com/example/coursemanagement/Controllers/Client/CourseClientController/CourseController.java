package com.example.coursemanagement.Controllers.Client.CourseClientController;

import com.example.coursemanagement.Controllers.Admin.CourseController.AddCourseController;
import com.example.coursemanagement.Controllers.Admin.CourseController.CourseBoxController;
import com.example.coursemanagement.Dto.CourseDetailDTO;
import com.example.coursemanagement.Models.Course;
import com.example.coursemanagement.Service.CourseService;
import com.example.coursemanagement.Utils.Alerts;
import javafx.event.ActionEvent;
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
import java.util.Collections;
import java.util.List;

public class CourseController {

    private final CourseService courseService = new CourseService(); // Tạo repository
    private Alerts alerts = new Alerts();

    @FXML

    public ScrollPane listCourse;
    @FXML

    public FlowPane courseContainer;
    @FXML

    public TextField searchField;

    // Box Khóa học
    public void initialize() {
        loadCoursesList();
    }

    public void loadCoursesList() {
        if (courseContainer != null) {
            List<CourseDetailDTO> courses = courseService.getCourseList(0);
            Collections.reverse(courses);
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
            Collections.reverse(courses);
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
            Collections.reverse(courses);
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
            searchField.setText("");
            alerts.showSuccessAlert("Kết quả tìm kiếm cho: " + query);
        }

        System.out.println("find");
    }

    @FXML

    public void handleClear() {
        refreshCourseList();
        searchField.setText("");
        loadCoursesList();
    }
}
