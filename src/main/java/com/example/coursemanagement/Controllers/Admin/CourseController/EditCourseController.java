package com.example.coursemanagement.Controllers.Admin.CourseController;

import com.example.coursemanagement.Models.Category;
import com.example.coursemanagement.Models.Course;
import com.example.coursemanagement.Models.Instructor;
import com.example.coursemanagement.Service.CategoriesService;
import com.example.coursemanagement.Service.CourseService;
import com.example.coursemanagement.Service.InstructorService;
import com.example.coursemanagement.Utils.Alerts;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.List;

public class EditCourseController {
    @FXML
    public TextField courseNameField;
    @FXML
    public ChoiceBox<Instructor> instructorField;
    @FXML
    public ChoiceBox<Category> categoryField;
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

    private final CategoriesService categoriesService = new CategoriesService();
    private final InstructorService instructorService = new InstructorService();
    private final CourseService courseService = new CourseService();
    private final Alerts alerts = new Alerts();

    private boolean courseAdded = false; // <== Để thông báo về sau

    public boolean isCourseAdded() {
        return courseAdded;
    }

    private Course currCourse;
    private CourseManagementController courseManagementController;

    public void setCourseManagementController(CourseManagementController controller) {
        this.courseManagementController = controller;
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

    public void setCategoryVal(int categoryId) {
        List<Category> categories = categoriesService.getListCategory();
        categoryField.getItems().addAll(categories);
        categoryField.setConverter(new StringConverter<>() {
            @Override
            public String toString(Category c) {
                return c != null ? c.getCategoryName() : "";
            }

            @Override
            public Category fromString(String string) {
                return categories.stream().filter(c -> c.getCategoryName().equals(string)).findFirst().orElse(null);
            }
        });
        if (!categories.isEmpty()) {
            categoryField.setValue(categories.get(0));
            for (Category item : categories
            ) {
                if (categoryId == item.getCategoryId()) {
                    categoryField.setValue(item);
                    break;
                }
            }
        }
    }

    public void setInstructorVal(int instructorId) {
        List<Instructor> instructors = instructorService.getAllInstructor();
        instructorField.getItems().addAll(instructors);
        instructorField.setConverter(new StringConverter<>() {
            @Override
            public String toString(Instructor c) {
                return c != null ? c.getInstructorName() : "";
            }

            @Override
            public Instructor fromString(String string) {
                return instructors.stream().filter(c -> c.getInstructorName().equals(string)).findFirst().orElse(null);
            }
        });
        if (!instructors.isEmpty()) {
            instructorField.setValue(instructors.get(0));
            for (Instructor item : instructors
            ) {
                if (instructorId == item.getInstructorId()) {
                    instructorField.setValue(item);
                    break;
                }
            }
        }
    }

    public void initDataFromCurrCourse(Course course) {
        if (course != null) {
            setCategoryVal(course.getCategoryId());
            courseNameField.setText(course.getCourseName());
            setInstructorVal(course.getInstructorId());
            priceField.setText(course.getCoursePrice() + "");
            thumbnailField.setText(course.getCourseThumbnail());
            descriptionField.setText(course.getCourseDescription());
        }
    }

    public void handleSave() {
        if (courseNameField.getText().isEmpty()) {
            alerts.showErrorAlert("Tên khóa học không được để trống.");
            return;
        }

        // Kiểm tra Tên người hướng dẫn
        if (instructorField.getValue() == null) {
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
                currCourse.getCourseId(),
                courseNameField.getText(),
                instructorField.getValue().getInstructorId(),
                categoryField.getValue().getCategoryId(),
                Double.parseDouble(priceField.getText()),
                thumbnailField.getText(),
                descriptionField.getText()
        );
        if (courseService.updateInforCourse(course)) {
            alerts.showSuccessAlert("Chỉnh sửa Khóa học thành công");
            if (courseManagementController != null) {
                courseManagementController.refreshCourseList(); // 👈 Gọi lại giao diện
            }
            instructorService.updateRelCourseAndInstructor(course.getCourseId(), course.getInstructorId());
            courseAdded = true; // <== Đánh dấu đã thêm
            handleCancel();     // đóng popup
        } else {
            alerts.showErrorAlert("Chỉnh sửa Khóa học không thành công");
        }
    }

    public void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
