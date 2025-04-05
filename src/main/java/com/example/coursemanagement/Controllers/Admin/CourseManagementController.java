package com.example.coursemanagement.Controllers.Admin;

import com.example.coursemanagement.Models.Category;
import com.example.coursemanagement.Models.Course;
import com.example.coursemanagement.Service.CategoriesService;
import com.example.coursemanagement.Service.CourseService;
import com.example.coursemanagement.Utils.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CourseManagementController {

    @FXML
    public TextField courseNameField;
    @FXML
    public TextField instructorField;
    @FXML
    public ChoiceBox<Category> categoryField;  // Đổi từ String thành Category
    @FXML
    public TextField priceField;
    @FXML
    public TextField thumbnailField;
    @FXML
    public TextArea descriptionField;
    @FXML
    public Button saveButton;
    @FXML
    public Button cancelButton;
    private final CategoriesService categoriesService = new CategoriesService(); // Tạo repository
    private final CourseService courseService = new CourseService(); // Tạo repository
    private final Alerts alerts = new Alerts(); // Tạo repository

    @FXML

    public ScrollPane listCourse;
    @FXML

    public FlowPane courseContainer;

    // Box Khóa học
    public void initialize() {
        loadCoursesList();
    }

    public void loadCoursesList() {

        if (courseContainer != null) {
            List<Course> courses = courseService.getCourseList();

            for (Course course : courses) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/HelpFxml/CourseBox.fxml"));
                    AnchorPane courseBox = loader.load();

                    CourseBoxController controller = loader.getController();
                    controller.setData(course);

                    courseContainer.getChildren().add(courseBox);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }


    }

    public void handleAddCourse() {
        try {
            // Tải FXML cho cửa sổ Thêm Khóa Học
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/HelpFxml/AddCourse.fxml"));
            Parent root = loader.load();

            // Lấy controller của cửa sổ
            CourseManagementController addCourseController = loader.getController();

            // Gọi setCategoryVal để load dữ liệu vào ChoiceBox
            addCourseController.setCategoryVal();

            // Tạo và hiển thị cửa sổ modal
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Thêm Khóa Học");
            stage.setResizable(false);

            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCategoryVal() {
        List<Category> categories = categoriesService.getListCategory();

        // Thêm tất cả các Category vào ChoiceBox
        categoryField.getItems().addAll(categories);

        // Định dạng hiển thị Category bằng categoryName
        categoryField.setConverter(new StringConverter<Category>() {
            @Override
            public String toString(Category category) {
                return category != null ? category.getCategoryName() : "";
            }

            @Override
            public Category fromString(String string) {
                // Tìm Category trong danh sách dựa trên categoryName
                return categories.stream()
                        .filter(category -> category.getCategoryName().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });

        // Đặt giá trị mặc định cho ChoiceBox (category đầu tiên)
        if (!categories.isEmpty()) {
            categoryField.setValue(categories.get(0));  // Thiết lập giá trị mặc định là category đầu tiên
        }
    }

    public void handleSave() {
        if (courseNameField.getText().isEmpty()) {
            alerts.showErrorAlert("Tên khóa học không được để trống.");
            return;
        }

        // Kiểm tra Tên người hướng dẫn
        if (instructorField.getText().isEmpty()) {
            alerts.showErrorAlert("Tên người hướng dẫn không được để trống.");
            return;
        }

        // Kiểm tra Chủ đề (Category) - phải có giá trị được chọn
        if (categoryField.getValue() == null) {
            alerts.showErrorAlert("Chủ đề không được để trống.");
            return;
        }

        // Kiểm tra Giá khóa học
        try {
            if (priceField.getText().isEmpty()) {
                alerts.showErrorAlert("Giá không được để trống.");
                return;
            }
            Double.parseDouble(priceField.getText());  // Kiểm tra nếu giá là số
        } catch (NumberFormatException e) {
            alerts.showErrorAlert("Giá phải là một số hợp lệ.");
            return;
        }

        // Kiểm tra Link ảnh mô tả
        if (thumbnailField.getText().isEmpty()) {
            alerts.showErrorAlert("Link ảnh mô tả không được để trống.");
            return;
        }

        // Kiểm tra Mô tả
        if (descriptionField.getText().isEmpty()) {
            alerts.showErrorAlert("Mô tả không được để trống.");
            return;
        }
        Course course = new Course(
                courseNameField.getText(),
                instructorField.getText(),
                categoryField.getValue().getCategoryId(),
                Double.parseDouble(priceField.getText()),
                thumbnailField.getText(),
                descriptionField.getText()
        );
        System.out.println(course);
        if (courseService.createNewCourse(course)) {
            alerts.showSuccessAlert("Thêm Khóa học thành công");
            handleCancel();
        } else {
            alerts.showErrorAlert("Thêm Khóa học không thành công");
        }

    }

    public void handleCancel() {
        if (cancelButton != null) {
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();
        }
    }


}
