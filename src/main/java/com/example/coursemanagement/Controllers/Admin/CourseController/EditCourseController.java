package com.example.coursemanagement.Controllers.Admin.CourseController;

import com.example.coursemanagement.Dto.CourseDetailDTO;
import com.example.coursemanagement.Models.Category;
import com.example.coursemanagement.Models.Course;
import com.example.coursemanagement.Models.Instructor;
import com.example.coursemanagement.Service.CategoriesService;
import com.example.coursemanagement.Service.CourseService;
import com.example.coursemanagement.Service.InstructorService;
import com.example.coursemanagement.Service.LogService;
import com.example.coursemanagement.Utils.Alerts;
import com.example.coursemanagement.Utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;

public class EditCourseController {
    private final LogService logService = new LogService();
    @FXML
    public TextField courseNameField;
    @FXML
    public ChoiceBox<Instructor> instructorField;
    @FXML
    public ChoiceBox<Category> categoryField;
    @FXML
    public TextField priceField;
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
    @FXML
    public DatePicker startDatePicker;
    @FXML

    public DatePicker endDatePicker;

    private boolean courseAdded = false; // <== Để thông báo về sau

    public boolean isCourseAdded() {
        return courseAdded;
    }

    private CourseDetailDTO currCourse;
    private CourseManagementController courseManagementController;
    private String thumbnailField = "";
    @FXML
    public ImageView thumbnailImageView;

    @FXML
    public void handleChooseThumbnail() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh thumbnail");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Hình ảnh", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                // 1. Đường dẫn thư mục đích (nơi lưu ảnh)
                String fileName = file.getName(); // Tên file
                File destDir = new File("src/main/resources/Images");
                if (!destDir.exists()) destDir.mkdirs();

                // 2. Copy file ảnh vào thư mục resource
                File destFile = new File(destDir, fileName);
                java.nio.file.Files.copy(file.toPath(), destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                // 3. Set ảnh hiển thị
                Image image = new Image(destFile.toURI().toString());
                thumbnailImageView.setImage(image);

                // 4. Lưu đường dẫn tương đối (để lưu vào DB)
                thumbnailField = "Images/" + fileName;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setCourseManagementController(CourseManagementController controller) {
        this.courseManagementController = controller;
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
                return c != null ? c.getFullname() : "";
            }

            @Override
            public Instructor fromString(String string) {
                return instructors.stream().filter(c -> c.getFullname().equals(string)).findFirst().orElse(null);
            }
        });
        if (!instructors.isEmpty()) {
            instructorField.setValue(instructors.get(0));
            for (Instructor item : instructors
            ) {
                if (instructorId == item.getUserId()) {
                    instructorField.setValue(item);
                    break;
                }
            }
        }
    }

    public void initDataFromCurrCourse(CourseDetailDTO course) {
        if (course != null) {
            setCategoryVal(course.getCourse().getCategoryId());
            courseNameField.setText(course.getCourse().getCourseName());
            setInstructorVal(course.getInstructor().getUserId());
            priceField.setText(course.getCourse().getCoursePrice() + "");
            startDatePicker.setValue(course.getCourse().getStartDate());
            endDatePicker.setValue(course.getCourse().getEndDate());
            thumbnailField = (course.getCourse().getCourseThumbnail());
            descriptionField.setText(course.getCategory().getCategoryDescription());
            String thumbnailPath = "/" + course.getCourse().getCourseThumbnail();
            try {
                Image image = new Image(getClass().getResource(thumbnailPath).toExternalForm());
                thumbnailImageView.setImage(image);
            } catch (Exception e) {
                System.err.println("Không tìm thấy ảnh: " + thumbnailPath + ", dùng ảnh mặc định.");
                try {
                    Image defaultImage = new Image(getClass().getResource("/Images/logo.png").toExternalForm());
                    thumbnailImageView.setImage(defaultImage);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
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
        //kiểm tra valid date
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        LocalDate today = LocalDate.now();

        if (startDate == null || endDate == null) {
            alerts.showErrorAlert("Vui lòng chọn cả ngày bắt đầu và kết thúc.");
            return;
        }

        if (startDate.isBefore(today)) {
            alerts.showErrorAlert("Ngày bắt đầu không thể trong quá khứ.");
            return;
        }

        if (endDate.isBefore(startDate)) {
            alerts.showErrorAlert("Ngày kết thúc phải sau ngày bắt đầu.");
            return;
        }

        // Khoảng cách tối thiểu giữa ngày bắt đầu và kết thúc (ví dụ: 1 ngày)
        if (startDate.equals(endDate)) {
            alerts.showErrorAlert("Ngày bắt đầu và kết thúc không thể giống nhau.");
            return;
        }
        // Kiểm tra Link ảnh mô tả
        if (thumbnailField.isEmpty() || thumbnailField == null) {
            alerts.showErrorAlert("Vui lòng chọn ảnh mô tả.");
            return;
        }

        // Kiểm tra Mô tả
        if (descriptionField.getText().isEmpty()) {
            alerts.showErrorAlert("Mô tả không được để trống.");
            return;
        }
        Course course = new Course(
                currCourse.getCourse().getCourseId(),
                courseNameField.getText(),
                instructorField.getValue().getUserId(),
                categoryField.getValue().getCategoryId(),
                Double.parseDouble(priceField.getText()),
                thumbnailField,
                descriptionField.getText(),
                startDate,
                endDate
        );
        if (courseService.updateInforCourse(course)) {
            alerts.showSuccessAlert("Chỉnh sửa Khóa học thành công");
            if(SessionManager.getInstance().getUser().getRoleId() == 2){
                logService.createLog(SessionManager.getInstance().getUser().getUserId(), "Giáo viên " + SessionManager.getInstance().getUser().getFullname() + " đã chỉnh sửa bài tập");

            }
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
