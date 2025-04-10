package com.example.coursemanagement.Controllers.Client.CourseClientController;

import com.example.coursemanagement.Controllers.Admin.CourseController.CourseManagementController;
import com.example.coursemanagement.Models.Category;
import com.example.coursemanagement.Models.Course;
import com.example.coursemanagement.Models.Instructor;
import com.example.coursemanagement.Service.CategoriesService;
import com.example.coursemanagement.Service.CourseService;
import com.example.coursemanagement.Utils.Alerts;
import com.example.coursemanagement.Utils.DatabaseConfig;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class FilterCourseController {

    @FXML
    public ChoiceBox<Category> categoryField;
    @FXML
    public Button cancelButton;

    private final CategoriesService categoriesService = new CategoriesService();
    private final CourseService courseService = new CourseService();
    private final Alerts alerts = new Alerts();
    @FXML

    public TextField maxPriceField;
    @FXML

    public TextField minPriceField;
    private CourseController courseController;

    public void setCourseController(CourseController courseController) {
        this.courseController = courseController;
    }

    private boolean courseAdded = false; // <== Để thông báo về sau

    public boolean isCourseAdded() {
        return courseAdded;
    }

    @FXML
    public void initialize() {
        setCategoryVal();
    }

    public void setCategoryVal() {
        List<Category> categories = categoriesService.getListCategory();
        categories.add(0, new Category(-1, "Chưa xác định", ""));
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
        if (!categories.isEmpty()) categoryField.setValue(categories.get(0));
    }

    @FXML

    public void handleFilter() {
        List<Course> courses = new ArrayList<>();
        int id = categoryField.getValue().getCategoryId();
        String minText = minPriceField.getText();
        String maxText = maxPriceField.getText();
        String sql = "SELECT * FROM Courses WHERE 1=1 " // bắt đầu bằng điều kiện đúng
                + (id != -1 ? "AND categoryId = ? " : "")
                + "AND coursePrice >= ? "
                + "AND coursePrice <= ?";
        int index = 1;
        try (Connection conn = DatabaseConfig.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            double min = (minText == null || minText.trim().isEmpty()) ? Double.MIN_VALUE : Double.parseDouble(minText);
            double max = (maxText == null || maxText.trim().isEmpty()) ? Double.MAX_VALUE : Double.parseDouble(maxText);

            if (id != -1) {
                stmt.setInt(index++, id);
            }
            stmt.setDouble(index++, min);
            stmt.setDouble(index++, max);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Category category = new Category(rs.getInt("categoryId"), rs.getString("categoryName"), rs.getString("categoryDescription"));
                Instructor instructor = new Instructor(rs.getInt("instructorId"), rs.getString("instructorName"), rs.getString("expertise"), rs.getString("instructorEmail"), rs.getString("instructorPhone"));
                Course dto = new Course(rs.getInt("courseId"), rs.getInt("categoryId"), rs.getInt("instructorId"), rs.getString("courseName"), rs.getString("courseDescription"), rs.getString("courseThumbnail"), rs.getDouble("coursePrice"), category, instructor);
                courses.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        courseController.refreshCourseList();
        courseController.loadCoursesListByFilter(courses);
        handleCancel();
        alerts.showSuccessAlert("Lọc thành công");
    }

    @FXML

    public void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
