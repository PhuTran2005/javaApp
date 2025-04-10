package com.example.coursemanagement.Controllers.Admin.CourseController;

import com.example.coursemanagement.Models.Course;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;



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

    private boolean courseAdded = false; // <== Để thông báo về sau

    private Course currCourse;


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
    public void setData(Course course) {
        this.currCourse = course;
        initialize();
    }
    public void initDataFromCurrCourse(Course course) {
        if (course != null) {
            categoryDetailLabel.setText(course.getCategory().getCategoryDescription());
            instructorDetailLabel.setText(course.getInstructor().getExpertise());
            courseNameField.setText(course.getCourseName());
            instructorField.setText(course.getInstructor().getInstructorName());
            categoryField.setText(course.getCategory().getCategoryName());
            priceField.setText(course.getCoursePrice() + " VND");
            descriptionField.setText(course.getCourseDescription());
            try {
                Image image = new Image(course.getCourseThumbnail());
                courseThumbnail.setImage(image);
                System.out.println(course.getCourseThumbnail());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
