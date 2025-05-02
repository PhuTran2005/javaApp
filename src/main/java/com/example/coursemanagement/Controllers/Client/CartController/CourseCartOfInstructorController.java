package com.example.coursemanagement.Controllers.Client.CartController;

import com.example.coursemanagement.Controllers.Client.ViewStudentListController;
import com.example.coursemanagement.Models.Student;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.io.IOException;

public class CourseCartOfInstructorController {

    @FXML
    private Label courseNameLabel;

    @FXML
    private Label teacherNameLabel;

    @FXML
    private Label studentCountLabel;

    @FXML
    private TableView<Student> studentTableView;

    @FXML
    private Button viewButton;

    private int courseId;

    public void setData(String courseName, String teacherName, int studentCount, int courseId) {
        courseNameLabel.setText(courseName);
        teacherNameLabel.setText(teacherName);
        studentCountLabel.setText(studentCount + " học viên");
        this.courseId = courseId;
    }

    @FXML
    public void initialize() {
        if (studentTableView != null) {
            // Tiến hành thao tác với studentTableView nếu cần
        } else {
            System.out.println("TableView is not initialized!");
        }
    }

    @FXML
    private void handleViewDetails() {
        try {
            // Load FXML của cửa sổ chi tiết học viên
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/StudentManagement/ViewStudentList.fxml"));
            Parent root = loader.load();  // Tải cửa sổ mới

            // Truyền courseId vào ViewStudentListController
            ViewStudentListController controller = loader.getController();
            controller.setCourseId(this.courseId);  // Truyền courseId vào controller

            // Tạo và hiển thị cửa sổ mới
            Stage stage = new Stage();
            stage.setTitle("Chi tiết khóa học");
            stage.setScene(new Scene(root));
            stage.show();

            // Đóng cửa sổ hiện tại nếu cần
            if (studentTableView != null && studentTableView.getScene() != null) {
                Stage currentStage = (Stage) studentTableView.getScene().getWindow();  // Sử dụng TableView để lấy Stage
                currentStage.close();  // Đóng cửa sổ hiện tại
            } else {
                // Nếu studentTableView hoặc Scene là null, không thể đóng cửa sổ hiện tại
                System.out.println("Không thể đóng cửa sổ hiện tại. studentTableView hoặc Scene là null.");
            }

        } catch (IOException e) {
            e.printStackTrace();
            // Xử lý lỗi nếu có
        }
    }

}
