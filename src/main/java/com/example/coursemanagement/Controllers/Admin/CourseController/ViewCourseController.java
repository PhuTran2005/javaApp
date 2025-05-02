package com.example.coursemanagement.Controllers.Admin.CourseController;

import com.example.coursemanagement.Dto.CourseDetailDTO;
import com.example.coursemanagement.Models.Course;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.URL;


public class ViewCourseController {
    @FXML
    public TextField courseNameField;
    @FXML
    public TextField instructorField;
    @FXML
    public TextField categoryField;
    @FXML
    public TextField priceField;
    @FXML
    public TextArea descriptionField;
    @FXML
    public Button cancelButton;
    @FXML
    public ImageView courseThumbnail;
    @FXML

    public Label instructorDetailLabel;
    @FXML

    public Label categoryDetailLabel;
    @FXML

    public TextField startDate;
    @FXML

    public TextField endDate;

    private boolean courseAdded = false; // <== Để thông báo về sau

    private CourseDetailDTO currCourse;


    @FXML
    private void handleToggleInstructorDetail() {
        boolean isVisible = instructorDetailLabel.isVisible();
        instructorDetailLabel.setVisible(!isVisible);
        instructorDetailLabel.setManaged(!isVisible);
    }

    @FXML
    private void handleToggleCategoryDetail() {
        boolean isVisible = categoryDetailLabel.isVisible();
        categoryDetailLabel.setVisible(!isVisible);
        categoryDetailLabel.setManaged(!isVisible);
    }

    @FXML
    public void initialize() {
        if (currCourse != null) {
            initDataFromCurrCourse(currCourse);
        }
    }

    public void setData(CourseDetailDTO course) {
        this.currCourse = course;
        initialize();
    }

    public void initDataFromCurrCourse(CourseDetailDTO course) {
        if (course != null) {
            categoryDetailLabel.setText("Mô tả: " + course.getCategory().getCategoryDescription());
            instructorDetailLabel.setText("Chuyên ngành: " + course.getInstructor().getExpertise());
            courseNameField.setText(course.getCourse().getCourseName());
            instructorField.setText(course.getInstructor().getFullname());
            categoryField.setText(course.getCategory().getCategoryName());
            startDate.setText(course.getCourse().getStartDate() + "");
            endDate.setText(course.getCourse().getEndDate() + "");
            priceField.setText(course.getCourse().getCoursePrice() + " VND");
            descriptionField.setText(course.getCourse().getCourseDescription());
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
    }

    public void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
