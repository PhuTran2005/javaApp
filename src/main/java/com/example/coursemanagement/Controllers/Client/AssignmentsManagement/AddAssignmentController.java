package com.example.coursemanagement.Controllers.Client.AssignmentsManagement;

import com.example.coursemanagement.Dto.CourseDetailDTO;
import com.example.coursemanagement.Repository.CoursesRepository;
import com.example.coursemanagement.Service.AssignmentService;
import com.example.coursemanagement.Service.LogService;
import com.example.coursemanagement.Utils.DatabaseConfig;
import com.example.coursemanagement.Utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


public class AddAssignmentController {
    private final LogService logService = new LogService();
    @FXML
    private Label courseLabel;
    private String selectedCourseName;

    @FXML
    private TextField assignmentTitle;

    @FXML
    private TextArea assignmentDescription;

    @FXML
    private DatePicker dueDate;

    @FXML
    private Button uploadButton;

    @FXML
    private Button saveAssignmentButton;

    @FXML
    private Button cancelButton;
    @FXML
    private Label fileNameLabel;

    private AssignmentService assignmentService;
    private CoursesRepository coursesRepository;

    private Runnable onAssignmentAdded; // Callback
    private File selectedFile;

    private int selectedCourseId = -1;

    public void setSelectedCourse(int courseId, String courseName) {
        this.selectedCourseId = courseId;
        this.selectedCourseName = courseName;

        // Hiển thị trực tiếp
        if (courseLabel != null) {
            courseLabel.setText(selectedCourseName);
        }
    }

    @FXML
    public void initialize() {
        // Sau khi FXML load xong, nếu selectedCourseName đã được set trước đó thì hiển thị
        if (selectedCourseName != null) {
            courseLabel.setText(selectedCourseName);
        }
    }

    public AddAssignmentController() {

        this.assignmentService = new AssignmentService(DatabaseConfig.getConnection());
        this.coursesRepository = new CoursesRepository();
    }

    public void setOnAssignmentAdded(Runnable onAssignmentAdded) {
        this.onAssignmentAdded = onAssignmentAdded;
    }

    @FXML
    private void handleSaveAssignment() {
        String title = assignmentTitle.getText();
        String description = assignmentDescription.getText();

        // ✅ Dùng luôn courseId được truyền từ setSelectedCourse()
        int courseId = selectedCourseId;

        // Kiểm tra dữ liệu nhập vào
        if (title.isEmpty() || description.isEmpty() || dueDate.getValue() == null) {
            showAlert("Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        // ✅ Kiểm tra hạn nộp không nằm trong quá khứ
        LocalDate selectedDate = dueDate.getValue();
        LocalDate today = LocalDate.now();

        if (selectedDate.isBefore(today)) {
            showAlert("Hạn nộp không được là ngày trong quá khứ.");
            return;
        }

        Date dueDateValue = Date.valueOf(selectedDate);

        try {
            // ✅ Không cần getCourseIdByName nữa

            // Lấy tên file và đường dẫn nếu đã chọn file
            String fileName = null;
            String filePath = null;
            if (selectedFile != null) {
                fileName = selectedFile.getName();
                filePath = selectedFile.getAbsolutePath();
            }

            // Thêm vào DB
            assignmentService.addAssignment(
                    title, description,
                    SessionManager.getInstance().getUser().getUserId(),
                    courseId, dueDateValue,
                    fileName, filePath
            );

            logService.createLog(
                    SessionManager.getInstance().getUser().getUserId(),
                    "Giáo viên " + SessionManager.getInstance().getUser().getFullname() + " đã thêm bài tập mới"
            );

            if (onAssignmentAdded != null) {
                onAssignmentAdded.run();
            }

            // Đóng cửa sổ
            Stage stage = (Stage) saveAssignmentButton.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi khi lưu bài tập. Vui lòng thử lại.");
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
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            selectedFile = file; // <-- LƯU lại file đã chọn
            fileNameLabel.setText(file.getName());
        } else {
            fileNameLabel.setText("Chưa chọn file");
        }
    }




    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
