package com.example.coursemanagement.Controllers.Admin.CourseController;

import com.example.coursemanagement.Dto.CourseDetailDTO;

import com.example.coursemanagement.Models.Model;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CourseManagementController {

    private final CourseService courseService = new CourseService(); // Tạo repository
    private final Alerts alerts = new Alerts(); // Tạo repository

    @FXML

    public ScrollPane listCourse;
    @FXML

    public FlowPane courseContainer;
    @FXML

    public Button recycle_btn;

    // Box Khóa học
    public void initialize() {
        refreshCourseList();
    }

    //Load data
    public void loadCoursesList() {
        if (courseContainer != null) {
            List<CourseDetailDTO> courses = new ArrayList<>();
            if (SessionManager.getInstance().getUser().getRoleId() == 2) {
                courses = courseService.getCourseListByInstructorId(SessionManager.getInstance().getUser().getUserId());

            } else {
                courses = courseService.getCourseList(0);
            }
            for (CourseDetailDTO course : courses) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/HelpFxml/CourseBox.fxml"));
                    AnchorPane courseBox = loader.load();
                    CourseBoxController controller = loader.getController();
                    controller.setCourseManagementController(this);
                    controller.setIsDelete(false);
                    controller.setData(course);
                    courseContainer.getChildren().add(courseBox);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //    reset data
    public void refreshCourseList() {
        if (courseContainer != null) {
            courseContainer.getChildren().clear(); // Xóa hết các course đang hiển thị
            loadCoursesList();
        }        // Tải lại từ CSDL
    }

    //Xử lý thêm khóa học
    public void handleAddCourse() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/HelpFxml/AddCourse.fxml"));
            Parent root = loader.load();

            AddCourseController addCourseController = loader.getController();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Thêm Khóa Học");
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Sau khi đóng popup, kiểm tra nếu đã thêm
            if (addCourseController.isCourseAdded()) {
                refreshCourseList();               // <== Tải mới
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Xử lý khôi phục khóa học
    public void handleRecycleCourse() throws IOException {
        if (SessionManager.getInstance().getUser().getRoleId() == 2) {
            alerts.showErrorAlert("Chức năng không khả thi");
            return;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/HelpFxml/CourseDeletedManagement.fxml"));
        Parent view = loader.load();
        Model.getInstance().getViewFactory().setAdminCenterContent(view);
    }
}
