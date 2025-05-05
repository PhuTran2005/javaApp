package com.example.coursemanagement.Controllers.Admin.CourseController;

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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AddCourseController {
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

    public DatePicker startDatePicker;
    @FXML

    public DatePicker endDatePicker;
    private String thumbnailField = "";
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

    @FXML
    public void initialize() {
        setCategoryVal();
        setInstructorVal();
    }

    public void setCategoryVal() {
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
        if (!categories.isEmpty()) categoryField.setValue(categories.get(0));
    }

    public void setInstructorVal() {
        List<Instructor> instructors = new ArrayList<>();
        if (SessionManager.getInstance().getUser().getRoleId() == 2) {
            instructors.add(new Instructor(SessionManager.getInstance().getUser().getUserId(), SessionManager.getInstance().getUser().getFullname()));
        } else {
            instructors = instructorService.getAllInstructor();
        }
        instructorField.getItems().addAll(instructors);
        List<Instructor> finalInstructors = instructors;
        instructorField.setConverter(new StringConverter<>() {
            @Override
            public String toString(Instructor c) {
                return c != null ? c.getFullname() : "";
            }

            @Override
            public Instructor fromString(String string) {
                return finalInstructors.stream().filter(c -> c.getFullname().equals(string)).findFirst().orElse(null);
            }
        });
        if (!instructors.isEmpty()) instructorField.setValue(instructors.get(0));
    }


    public void handleSave() {
        if (courseNameField.getText().isEmpty()) {
            alerts.showErrorAlert("Tên khóa học không được để trống.");
            return;
        }

        // Kiểm tra Tên người hướng dẫn
        if (instructorField.getValue() == null) {
            alerts.showErrorAlert("Giảng viên không được để trống..");
            return;
        }

        // Kiểm tra Chủ đề (Category) - phải có giá trị được chọn
        if (categoryField.getValue() == null) {
            alerts.showErrorAlert("Chủ đề không được để trống.");
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

        if (thumbnailField.isEmpty() || thumbnailField == null) {
            alerts.showErrorAlert("Vui lòng chọn ảnh cho khóa học.");
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
        // Kiểm tra Mô tả
        if (descriptionField.getText().isEmpty()) {
            alerts.showErrorAlert("Mô tả không được để trống.");
            return;
        }
        Course course = new Course(
                courseNameField.getText(),
                instructorField.getValue().getUserId(),
                categoryField.getValue().getCategoryId(),
                Double.parseDouble(priceField.getText()),
                thumbnailField,
                descriptionField.getText(),
                startDate,
                endDate
        );
        if (courseService.createNewCourse(course) != null) {
            alerts.showSuccessAlert("Thêm Khóa học thành công");
            if(SessionManager.getInstance().getUser().getRoleId() == 2){
                logService.createLog(SessionManager.getInstance().getUser().getUserId(), "Giáo viên " + SessionManager.getInstance().getUser().getFullname() + " đã thêm khoá học mới");

            }
            courseAdded = true; // <== Đánh dấu đã thêm
            handleCancel();     // đóng popup
        } else {
            alerts.showErrorAlert("Thêm không thành công");
        }
    }

    public void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
