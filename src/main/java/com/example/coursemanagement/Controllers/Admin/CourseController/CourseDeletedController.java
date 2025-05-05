package com.example.coursemanagement.Controllers.Admin.CourseController;

import com.example.coursemanagement.Dto.CourseDetailDTO;
import com.example.coursemanagement.Models.Model;
import com.example.coursemanagement.Service.CourseService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class CourseDeletedController {
    private final CourseService courseService = new CourseService(); // Tạo repository

    @FXML

    public ScrollPane listCourse;
    @FXML

    public FlowPane courseContainer;

    // Box Khóa học
    public void initialize() {
        refreshCourseList();
    }

    //Load data
    public void loadCoursesList() {
        if (courseContainer != null) {
            List<CourseDetailDTO> courses = courseService.getCourseList(1);
            for (CourseDetailDTO course : courses) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/HelpFxml/CourseDeletedBox.fxml"));
                    AnchorPane courseBox = loader.load();
                    CourseBoxController controller = loader.getController();
                    controller.setCourseDeletedManagementController(this);
                    controller.setIsDelete(true);
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
            loadCoursesList();
        }        // Tải lại từ CSDL
    }

    @FXML
    public void handleBack() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/CourseManagement.fxml"));
        Parent view = loader.load();
        Model.getInstance().getViewFactory().setAdminCenterContent(view);
    }
}
