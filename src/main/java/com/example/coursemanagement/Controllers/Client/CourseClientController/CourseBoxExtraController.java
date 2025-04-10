package com.example.coursemanagement.Controllers.Client.CourseClientController;

import com.example.coursemanagement.Controllers.Admin.CourseController.ViewCourseController;
import com.example.coursemanagement.Controllers.Client.ClientMenuController;
import com.example.coursemanagement.Models.Course;
import com.example.coursemanagement.Service.CartService;
import com.example.coursemanagement.Service.CourseService;
import com.example.coursemanagement.Utils.Alerts;
import com.example.coursemanagement.Utils.SessionManager;
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


public class CourseBoxExtraController {
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
    private final CartService cartService = new CartService(); // Tạo repository
    private CourseController courseController;

    public void setCourseController(CourseController courseController) {
        this.courseController = courseController;
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
    private void handleAddCart() {
        if (cartService.isExistedInCart(SessionManager.getInstance().getUser().getUserId(), currCourse.getCourseId())) {
            alerts.showErrorAlert("Khóa học đã được thêm vào cart");
        } else {
            if (cartService.addToCart(SessionManager.getInstance().getUser().getUserId(), currCourse.getCourseId())) {
                SessionManager.getInstance().setCartSize();
                ClientMenuController.getInstance().refreshCartSize();
                alerts.showSuccessAlert("Thêm vào cart thành công");
            } else {
                alerts.showErrorAlert("Thêm vào cart không thành công");
            }
        }
    }

    @FXML
    private void handleBuy() {
        System.out.println("Buy now");


    }
}
