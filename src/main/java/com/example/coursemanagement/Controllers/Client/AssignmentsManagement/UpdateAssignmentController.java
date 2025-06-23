package com.example.coursemanagement.Controllers.Client.AssignmentsManagement;

import com.example.coursemanagement.Dto.CourseDetailDTO;
import com.example.coursemanagement.Models.Assignment;
import com.example.coursemanagement.Repository.CoursesRepository;
import com.example.coursemanagement.Service.AssignmentService;
import com.example.coursemanagement.Service.LogService;
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
    private Label courseNameLabel;


    @FXML
    private TextField assignmentTitle;

    @FXML
    private TextArea assignmentDescription;

    @FXML
    private DatePicker dueDate;

    @FXML
    private Button uploadButton;
    private final LogService logService = new LogService();
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
    private Runnable onAssignmentUpdated;

    public void setOnAssignmentUpdated(Runnable onAssignmentUpdated) {
        this.onAssignmentUpdated = onAssignmentUpdated;
    }


    public UpdateAssignmentController() {
        this.assignmentService = new AssignmentService(DatabaseConfig.getConnection());
        this.coursesRepository = new CoursesRepository();
    }

    public void setAssignmentData(Assignment assignment) {
        this.assignment = assignment;

        // Gán dữ liệu vào các trường giao diện
        assignmentTitle.setText(assignment.getTitle());
        assignmentDescription.setText(assignment.getDescription());
        if (assignment.getDueDate() != null) {
            dueDate.setValue(assignment.getDueDate().toLocalDate());
        }

        fileNameLabel.setText(
                assignment.getFileName() != null ? assignment.getFileName() : "Chưa chọn file"
        );

        try {
            int currentInstructorId = SessionManager.getInstance().getUser().getUserId();

            List<CourseDetailDTO> courses = coursesRepository.getCoursesByInstructor(currentInstructorId);

            // Tìm tên khóa học từ assignment.getCourseId()
            String currentCourseName = courses.stream()
                    .filter(course -> course.getCourse().getCourseId() == assignment.getCourseId())
                    .map(course -> course.getCourse().getCourseName())
                    .findFirst()
                    .orElse("Không rõ khóa học");

            courseNameLabel.setText(currentCourseName);  // Gán vào Label thay vì ComboBox

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi khi tải tên khóa học.");
        }

        updateAssignmentButton.setOnAction(e -> handleUpdateAssignment());
    }


    // Xử lý khi nhấn "Cập nhật"
    @FXML
    private void handleUpdateAssignment() {
        String title = assignmentTitle.getText();
        String description = assignmentDescription.getText();

        if (title.isEmpty() || description.isEmpty() || dueDate.getValue() == null) {
            showAlert("Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        LocalDateTime dueDateValue = dueDate.getValue().atTime(23, 59);  // Giờ cố định

        if (dueDateValue.isBefore(LocalDateTime.now())) {
            showAlert("Hạn nộp không được ở quá khứ.");
            return;
        }

        try {
            // Cập nhật các trường hợp mới
            assignment.setTitle(title);
            assignment.setDescription(description);
            assignment.setDueDate(dueDateValue);
            // Không cần setCourseId lại nữa

            // Nếu có tệp mới được chọn
            if (selectedFile != null) {
                assignment.setFilePath(selectedFile.getAbsolutePath());
                assignment.setFileName(selectedFile.getName());
            } else if (fileNameLabel.getText() != null && !fileNameLabel.getText().equals("Chưa chọn file")) {
                assignment.setFilePath(fileNameLabel.getText());
                assignment.setFileName(fileNameLabel.getText());
            } else {
                assignment.setFilePath(null);
                assignment.setFileName(null);
            }

            // Gọi cập nhật
            assignmentService.updateAssignment(assignment);
            logService.createLog(
                    SessionManager.getInstance().getUser().getUserId(),
                    "Giáo viên " + SessionManager.getInstance().getUser().getFullname() + " đã cập nhật bài tập"
            );

            showAlert("Cập nhật bài tập thành công!");
            if (onAssignmentUpdated != null) {
                onAssignmentUpdated.run();  // Gọi lại hàm reload
            }


            // Đóng cửa sổ
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
