package com.example.coursemanagement.Controllers.Client.CourseClientController;

import com.example.coursemanagement.Controllers.Admin.CourseController.CourseManagementController;
import com.example.coursemanagement.Dto.CourseDetailDTO;
import com.example.coursemanagement.Models.Category;
import com.example.coursemanagement.Models.Course;
import com.example.coursemanagement.Models.Instructor;
import com.example.coursemanagement.Service.CategoriesService;
import com.example.coursemanagement.Service.CourseService;
import com.example.coursemanagement.Service.InstructorService;
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
    private final InstructorService instructorService = new InstructorService();
    private final CourseService courseService = new CourseService();
    private final Alerts alerts = new Alerts();
    @FXML

    public TextField maxPriceField;
    @FXML

    public TextField minPriceField;
    @FXML

    public ChoiceBox<Instructor> instructorField;
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
        setInstructorVal();
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

    public void setInstructorVal() {
        List<Instructor> instructors = instructorService.getAllInstructor();
        instructors.add(0, new Instructor(-1, "Chưa xác định"));
        instructorField.getItems().addAll(instructors);
        instructorField.setConverter(new StringConverter<>() {
            @Override
            public String toString(Instructor c) {
                return c != null ? c.getFullname() : "";
            }

            @Override
            public Instructor fromString(String string) {
                return instructors.stream().filter(c -> c.getFullname().equals(string)).findFirst().orElse(null);
            }
        });
        if (!instructors.isEmpty()) instructorField.setValue(instructors.get(0));
    }

    @FXML

    public void handleFilter() {
        List<CourseDetailDTO> courses = new ArrayList<>();
        int id = categoryField.getValue().getCategoryId();
        int instructorId = instructorField.getValue().getUserId();
        String minText = minPriceField.getText();
        String maxText = maxPriceField.getText();
        String sql = "SELECT " +
                "    c.course_id, " +
                "    c.course_name, " +
                "    c.description AS course_description, " +
                "    c.fee, " +
                "    c.start_date, " +
                "    c.end_date, " +
                "    c.create_date AS course_create_date, " +
                "    c.course_thumbnail, " +
                "    c.is_deleted, " +

                // Instructor Info
                "    i.specialty, " +
                "    i.degree, " +
                "    i.years_of_experience, " +

                // User Info (người giảng dạy)
                "    u.user_id, " +
                "    u.full_name AS instructor_name, " +
                "    u.email AS instructor_email, " +
                "    u.role_id, " +
                "    u.phonenumber, " +
                "    u.create_date, " +

                // Category Info
                "    cat.category_id, " +
                "    cat.category_name, " +
                "    cat.description AS category_description " +  // ← Không có dấu phẩy ở cuối dòng này

                "FROM Courses c " +  // ← Nhớ thêm khoảng trắng
                "LEFT JOIN Instructors i ON c.instructor_id = i.instructor_id " +
                "LEFT JOIN Users u ON i.instructor_id = u.user_id " +
                "LEFT JOIN Categories cat ON c.category_id = cat.category_id " +
                "WHERE 1=1 " // bắt đầu bằng điều kiện đúng
                + (id != -1 ? "AND cat.category_id = ? " : "")
                + "AND fee >= ? "
                + "AND fee <= ? " +
                (instructorId != -1 ? "AND i.instructor_id = ? " : "") +
                "and c.is_deleted = 0";
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
            if (instructorId != -1) {
                stmt.setInt(index++, instructorId);
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Course course = new Course(rs.getInt("course_id"), rs.getInt("category_id"), rs.getInt("user_id"), rs.getString("course_name"), rs.getString("course_description"), rs.getString("course_thumbnail"), rs.getDouble("fee"), rs.getString("course_create_date"), rs.getDate("start_date").toLocalDate(), rs.getDate("end_date").toLocalDate(), rs.getBoolean("is_deleted"));

                Category category = new Category(rs.getInt("category_id"), rs.getString("category_name"), rs.getString("category_description"));
                Instructor instructor = new Instructor(rs.getInt("user_id"), rs.getString("instructor_email"), rs.getString("instructor_name"), rs.getInt("role_id"), rs.getString("phonenumber"), rs.getString("create_date"), rs.getString("specialty"), rs.getString("degree"), rs.getInt("years_of_experience"));
                CourseDetailDTO dto = new CourseDetailDTO(course, category, instructor);
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
