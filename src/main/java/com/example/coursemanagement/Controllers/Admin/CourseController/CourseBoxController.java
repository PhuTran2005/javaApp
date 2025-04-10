package com.example.coursemanagement.Controllers.Admin.CourseController;

import com.example.coursemanagement.Models.Course;
import com.example.coursemanagement.Service.CourseService;
import com.example.coursemanagement.Utils.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;


public class CourseBoxController {
    @FXML
    public AnchorPane courseBox;
    @FXML

    public Label courseName;
    @FXML

    public Label courseInstructorName;
    @FXML

    public Label coursePrice;
    @FXML

    public ImageView courseThumbnail;
    private Course currCourse;
    private final Alerts alerts = new Alerts(); // Tạo repository
    private final CourseService courseService = new CourseService(); // Tạo repository
    private CourseManagementController courseManagementController;

    public void setCourseManagementController(CourseManagementController controller) {
        this.courseManagementController = controller;
    }

    public void setData(Course course) {
        this.currCourse = course;
        courseName.setText(course.getCourseName());
        courseInstructorName.setText("GV: " + course.getInstructor().getInstructorName());
        coursePrice.setText("Giá: " + course.getCoursePrice() + " VND");

        // Load ảnh
        try {
            Image image = new Image(course.getCourseThumbnail());
            courseThumbnail.setImage(image);
            System.out.println(course.getCourseThumbnail());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void handleView() {
        System.out.println("Chỉnh sửa: " + currCourse.getCourseName());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/HelpFxml/ViewCourse.fxml"));
            Parent root = loader.load();

            ViewCourseController viewCourseController = loader.getController();
            viewCourseController.setData(currCourse);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEdit() {
        System.out.println("Chỉnh sửa: " + currCourse.getCourseName());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/HelpFxml/EditCourse.fxml"));
            Parent root = loader.load();

            EditCourseController editController = loader.getController();
            editController.setData(currCourse);
            editController.setCourseManagementController(courseManagementController); // 👈 Truyền controller chính

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            if (editController.isCourseAdded()) {
                courseManagementController.refreshCourseList();             // <== Tải mới
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Mở giao diện chỉnh sửa -> có thể dùng lại AddCourse.fxml nhưng truyền Course vào
    }

    @FXML
    private void handleDelete() {
        System.out.println("Xóa: " + currCourse);

        if (alerts.showConfirmationSelectedAlert("Bạn có chắc muốn xóa? ")) {
            courseService.deleteCourseById(currCourse.getCourseId());
            courseManagementController.refreshCourseList();
            alerts.showSuccessAlert("Xóa khóa học thành công");
            System.out.println("Xoá" + currCourse.getCourseName() + "thành công");
        } else {
            System.out.println("Xoá" + currCourse.getCourseName() + "không thành công");

        }


    }
}
