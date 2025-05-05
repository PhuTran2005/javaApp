package com.example.coursemanagement.Controllers.Client.CourseClientController;

import com.example.coursemanagement.Controllers.Admin.CourseController.AddCourseController;
import com.example.coursemanagement.Controllers.Admin.CourseController.ViewCourseController;
import com.example.coursemanagement.Controllers.Client.ClientMenuController;
import com.example.coursemanagement.Controllers.Client.MyCourseController;
import com.example.coursemanagement.Controllers.PaymentDetailController;
import com.example.coursemanagement.Dto.CourseDetailDTO;
import com.example.coursemanagement.Models.Course;
import com.example.coursemanagement.Service.CartService;
import com.example.coursemanagement.Service.CourseService;
import com.example.coursemanagement.Utils.Alerts;
import com.example.coursemanagement.Utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import java.util.Collections;
import java.util.ResourceBundle;


public class MyCourseBoxController implements Initializable {
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

    private CourseDetailDTO currCourse;
    private final Alerts alerts = new Alerts(); // Tạo repository
    private final CourseService courseService = new CourseService(); // Tạo repository
    private final CartService cartService = new CartService(); // Tạo repository
    private MyCourseController myCourseController;

    public void setMyCourseController(MyCourseController myCourseController) {
        this.myCourseController = myCourseController;
    }

    //load data
    public void setData(CourseDetailDTO course) {
        this.currCourse = course;
        courseName.setText(course.getCourse().getCourseName());
        courseInstructorName.setText("GV: " + course.getInstructor().getFullname());
        coursePrice.setText("Giá: " + course.getCourse().getCoursePrice() + " VND");

        // Load ảnh
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

    //Xử lý View
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



    //Xử lý mua

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void handleStudy(ActionEvent event) {

    }
}
