package com.example.coursemanagement.Controllers.Client.AssignmentsManagement;

import com.example.coursemanagement.Dto.CourseDetailDTO;
import com.example.coursemanagement.Models.Assignment;
import com.example.coursemanagement.Repository.CoursesRepository;
import com.example.coursemanagement.Service.AssignmentService;
import com.example.coursemanagement.Utils.DatabaseConfig;
import com.example.coursemanagement.Utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


public class UpdateAssignmentController {

    @FXML
    private ComboBox<String> courseComboBox;

    @FXML
    private TextField assignmentTitle;

    @FXML
    private TextArea assignmentDescription;

    @FXML
    private DatePicker dueDate;

    @FXML
    private Button uploadButton;

    @FXML
    private Button updateAssignmentButton;

    @FXML
    private Button cancelButton;
    @FXML
    private Label fileNameLabel;

    private AssignmentService assignmentService;
    private CoursesRepository coursesRepository;
    private Assignment assignment;
    private File selectedFile;

    public UpdateAssignmentController() {
        this.assignmentService = new AssignmentService(DatabaseConfig.getConnection());
        this.coursesRepository = new CoursesRepository();
    }

    public void setAssignmentData(Assignment assignment) {
        this.assignment = assignment;
        // Điền thông tin bài tập vào giao diện
        assignmentTitle.setText(assignment.getTitle());
        assignmentDescription.setText(assignment.getDescription());
        if (assignment.getDueDate() != null) {
            dueDate.setValue(assignment.getDueDate().toLocalDate());
        }

        // Hiển thị tên file, sử dụng file_name từ Assignment
        fileNameLabel.setText(assignment.getFileName() != null ? assignment.getFileName() : "Chưa chọn file");

        try {
            int currentInstructorId = SessionManager.getInstance().getUser().getUserId();

            List<CourseDetailDTO> courses = coursesRepository.getCoursesByInstructor(currentInstructorId);

            // Chuyển đổi danh sách CourseDetailDTO thành tên khóa học để hiển thị trong ComboBox
            List<String> courseNames = courses.stream()
                    .map(courseDetailDTO -> courseDetailDTO.getCourse().getCourseName())
                    .collect(Collectors.toList());

            courseComboBox.getItems().clear();
            courseComboBox.getItems().addAll(courseNames);

            String currentCourseName = courses.stream()
                    .filter(course -> course.getCourse().getCourseId() == assignment.getCourseId())
                    .map(course -> course.getCourse().getCourseName())
                    .findFirst()
                    .orElse("");
            courseComboBox.setValue(currentCourseName);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi khi tải danh sách khóa học.");
        }

        System.out.println("DEBUG | Mô tả: " + assignment.getDescription());
        System.out.println("DEBUG | Tên file: " + assignment.getFileName());

        updateAssignmentButton.setOnAction(e -> handleUpdateAssignment());
    }

    // Xử lý khi nhấn "Cập nhật"
    @FXML
    private void handleUpdateAssignment() {
        String title = assignmentTitle.getText();
        String description = assignmentDescription.getText();
        String courseName = courseComboBox.getValue();

        if (title.isEmpty() || description.isEmpty() || courseName == null || dueDate.getValue() == null) {
            showAlert("Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        LocalDateTime dueDateValue = dueDate.getValue().atTime(23, 59);  // Đặt giờ là 23:59

        try {
            int courseId = assignmentService.getCourseIdByName(courseName);

            // Cập nhật bài tập với các thông tin mới
            assignment.setTitle(title);
            assignment.setDescription(description);
            assignment.setCourseId(courseId);
            assignment.setDueDate(dueDateValue);

            // Nếu có tệp mới được chọn, cập nhật đường dẫn tệp
            if (selectedFile != null) {
                assignment.setFilePath(selectedFile.getAbsolutePath()); // Lưu đường dẫn đầy đủ của file
                assignment.setFileName(selectedFile.getName());
            } else if (fileNameLabel.getText() != null && !fileNameLabel.getText().equals("Chưa chọn file")) {
                assignment.setFilePath(fileNameLabel.getText());
                assignment.setFileName(fileNameLabel.getText());
            } else {
                assignment.setFilePath(null);
                assignment.setFileName(null);
            }

            // Cập nhật bài tập vào cơ sở dữ liệu
            assignmentService.updateAssignment(assignment);

            // Hiển thị thông báo thành công
            showAlert("Cập nhật bài tập thành công!");

            // Đóng cửa sổ sau khi cập nhật
            Stage stage = (Stage) updateAssignmentButton.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi khi cập nhật bài tập. Vui lòng thử lại.");
        }
    }

    // Xử lý khi nhấn nút hủy
    @FXML
    private void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    // Xử lý chọn file tải lên
    @FXML
    private void handleUploadFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            fileNameLabel.setText(selectedFile.getName()); // hoặc getAbsolutePath() nếu muốn đường dẫn đầy đủ
        } else {
            fileNameLabel.setText("Chưa chọn file");
        }
    }

    // Hiển thị thông báo
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
