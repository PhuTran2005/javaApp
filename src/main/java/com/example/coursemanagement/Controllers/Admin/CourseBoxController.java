package com.example.coursemanagement.Controllers.Admin;

import com.example.coursemanagement.Models.Course;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.awt.*;


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


    public void setData(Course course) {
        courseName.setText(course.getCourseName());
        courseInstructorName.setText("GV: " + course.getInstructorName());
        coursePrice.setText("Giá: " + course.getCoursePrice() + " VND");

        // Load ảnh
        Image image = new Image(course.getCourseThumbnail(), true);  // 'course.getCourseThumbnail()' phải trả về đường dẫn hợp lệ
        courseThumbnail.setImage(image);
    }
}
