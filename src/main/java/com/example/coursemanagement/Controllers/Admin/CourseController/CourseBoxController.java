package com.example.coursemanagement.Controllers.Admin.CourseController;

import com.example.coursemanagement.Dto.CourseDetailDTO;
import com.example.coursemanagement.Models.Course;
import com.example.coursemanagement.Service.CourseService;
import com.example.coursemanagement.Utils.Alerts;
import com.example.coursemanagement.Utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;


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
    @FXML
    public Button recover_btn;
    @FXML
    public Button delete_btn;
    private CourseDetailDTO currCourse;
    private final Alerts alerts = new Alerts(); // Tạo repository
    private final CourseService courseService = new CourseService(); // Tạo repository
    private CourseManagementController courseManagementController; //Lưu controller để xử lý load giao diện
    private boolean isDelete = false;
    private CourseDeletedController courseDeletedController; //Lưu controller để xử lý load giao diện

    public void setCourseManagementController(CourseManagementController controller) {
        this.courseManagementController = controller;
    }

    public void setCourseDeletedManagementController(CourseDeletedController controller) {
        this.courseDeletedController = controller;
    }

    public void setIsDelete(boolean val) {
        this.isDelete = val;
    }

    //Load data cho mỗi box
    public void setData(CourseDetailDTO course) {
        if (SessionManager.getInstance().getUser().getRoleId() == 2) {
            if (delete_btn != null) {
                delete_btn.setVisible(false);
                delete_btn.setManaged(false);
            }
        }
        this.currCourse = course;
        courseName.setText(course.getCourse().getCourseName());
        courseInstructorName.setText("GV: " + course.getInstructor().getFullname());
        coursePrice.setText("Giá: " + course.getCourse().getCoursePrice() + " VND");


        String thumbnailPath = "/" + course.getCourse().getCourseThumbnail();
        try {
            Image image = new Image(getClass().getResource(thumbnailPath).toExternalForm());
            courseThumbnail.setImage(image);
        } catch (Exception e) {
            System.err.println("Không tìm thấy ảnh: " + thumbnailPath + ", dùng ảnh mặc định.");
            try {
                Image defaultImage = new Image(getClass().getResource("/Images/logo.png").toExternalForm());
                courseThumbnail.setImage(defaultImage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }


    }

    //Load nút xử lý


    //Xử lý nút xem khóa học
    @FXML
    private void handleView() {
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
    //Xử lý nút chỉnh khóa học

    @FXML
    private void handleEdit() {
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
    //Xử lý nút xóa khóa học

    @FXML
    private void handleDelete() {
        System.out.println("Xóa: " + currCourse);

        if (alerts.showConfirmationSelectedAlert("Bạn có chắc muốn xóa? ")) {
            courseService.deleteCourseById(currCourse.getCourse().getCourseId());
            if (courseManagementController != null) {
                courseManagementController.refreshCourseList();
            }
            alerts.showSuccessAlert("Xóa khóa học thành công");
        }


    }
    //Xử lý nút khôi phục khóa học

    @FXML
    public void handleRecover() {
        if (alerts.showConfirmationSelectedAlert("Bạn có chắc muốn khôi phục không? ")) {
            courseService.recoveryCourse(currCourse.getCourse().getCourseId());
            if (courseDeletedController != null) {
                courseDeletedController.refreshCourseList();
            }
            alerts.showSuccessAlert("Khôi phục khóa học thành công");
        }
    }
}
