package com.example.coursemanagement.Controllers.Client.CourseClientController;

import com.example.coursemanagement.Controllers.Admin.CourseController.ViewCourseController;
import com.example.coursemanagement.Controllers.Client.AssignmentsManagement.AssignmentListController;
import com.example.coursemanagement.Controllers.Client.ClientController;
import com.example.coursemanagement.Controllers.Client.MyCourseController;
import com.example.coursemanagement.Models.Model;
import com.example.coursemanagement.Dto.CourseDetailDTO;
import com.example.coursemanagement.Models.Course;
import com.example.coursemanagement.Service.CartService;
import com.example.coursemanagement.Service.CourseService;
import com.example.coursemanagement.Service.SubmissionService;
import com.example.coursemanagement.Utils.Alerts;
import com.example.coursemanagement.Utils.SessionManager;
import javafx.animation.FadeTransition;
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
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MyCourseBoxController implements Initializable {
    @FXML public AnchorPane courseBox;
    @FXML public Label courseName;
    @FXML public Label courseInstructorName;
    @FXML public Label coursePrice;
    @FXML public ImageView courseThumbnail;
    @FXML public Label assignmentCountLabel;
    @FXML public Button Study_btn1;

    private CourseDetailDTO currCourse;
    private MyCourseController myCourseController;
    private final Alerts alerts = new Alerts();
    private final CourseService courseService = new CourseService();
    private final CartService cartService = new CartService();
    private final SubmissionService submissionService = new SubmissionService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // No-op
    }

    public void setData(CourseDetailDTO course) {
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

        int studentId = SessionManager.getInstance().getUser().getUserId();
        int courseId = course.getCourse().getCourseId();
        int unsubmittedCount = submissionService.getUnsubmittedCount(courseId, studentId);

        if (unsubmittedCount > 0) {
            assignmentCountLabel.setText("(" + unsubmittedCount + ")");
            assignmentCountLabel.setVisible(true);
        } else {
            assignmentCountLabel.setVisible(false);
        }
    }

    public void setMyCourseController(MyCourseController controller) {
        this.myCourseController = controller;
    }

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

    @FXML
    public void handleStudy(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/Assignment/StudentListAssignment.fxml"));
            Parent assignmentListView = loader.load();

            AssignmentListController controller = loader.getController();
            controller.initialize(currCourse.getCourse().getCourseId());

            ((BorderPane) Model.getInstance().getViewFactory().getClientRoot()).setCenter(assignmentListView);
            FadeTransition ft = new FadeTransition(Duration.millis(400), assignmentListView);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleAssignment(ActionEvent event) {
        handleStudy(event);
    }
}
