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
import java.util.List;
import java.util.stream.Collectors;


public class AddAssignmentController {
    private final LogService logService = new LogService();
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
    private Button saveAssignmentButton;

    @FXML
    private Button cancelButton;
    @FXML
    private Label fileNameLabel;

    private AssignmentService assignmentService;
    private CoursesRepository coursesRepository;

    private Runnable onAssignmentAdded; // Callback
    private File selectedFile;

    public AddAssignmentController() {

        this.assignmentService = new AssignmentService(DatabaseConfig.getConnection());
        this.coursesRepository = new CoursesRepository();
    }

    public void setOnAssignmentAdded(Runnable onAssignmentAdded) {
        this.onAssignmentAdded = onAssignmentAdded;
    }

    // Xử lý khi nhấn lưu bài tập
    @FXML
    private void handleSaveAssignment() {
        String title = assignmentTitle.getText();
        String description = assignmentDescription.getText();
        String courseName = courseComboBox.getValue();

        if (title.isEmpty() || description.isEmpty() || courseName == null || dueDate.getValue() == null) {
            showAlert("Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        Date dueDateValue = Date.valueOf(dueDate.getValue());

        try {
            int courseId = assignmentService.getCourseIdByName(courseName);

            // Lấy tên file và đường dẫn file nếu có file được chọn
            String fileName = null;
            String filePath = null;
            if (selectedFile != null) {
                fileName = selectedFile.getName(); // Lấy tên file
                filePath = selectedFile.getAbsolutePath();  // Lưu đường dẫn đầy đủ của file
            }

            // Thêm bài tập vào cơ sở dữ liệu
            assignmentService.addAssignment(title, description, SessionManager.getInstance().getUser().getUserId(), courseId, dueDateValue, fileName, filePath);
            logService.createLog(SessionManager.getInstance().getUser().getUserId(), "Giáo viên " + SessionManager.getInstance().getUser().getFullname() + " đã thêm bài tập mới");
            // Gọi callback để reload biểu đồ ở màn chính
            if (onAssignmentAdded != null) {
                onAssignmentAdded.run();
            }

            // Đóng cửa sổ sau khi lưu
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
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            selectedFile = file; // <-- LƯU lại file đã chọn
            fileNameLabel.setText(file.getName());
        } else {
            fileNameLabel.setText("Chưa chọn file");
        }
    }


    // Hiển thị danh sách khóa học cho ComboBox
    public void initialize() {
        try {
            // Lấy ID giảng viên hiện tại (giả định bạn có cách lấy ID giảng viên hiện tại)
            int currentInstructorId = SessionManager.getInstance().getUser().getUserId();

            // Lấy danh sách khóa học của giảng viên hiện tại
            List<CourseDetailDTO> courses = coursesRepository.getCoursesByInstructor(currentInstructorId);

            // Kiểm tra xem danh sách khóa học có rỗng không
            if (courses.isEmpty()) {
                System.out.println("Giảng viên chưa dạy khóa học nào.");
                return;
            }

            // Chuyển đổi danh sách CourseDetailDTO thành tên khóa học để hiển thị trong ComboBox
            List<String> courseNames = courses.stream()
                    .map(courseDetailDTO -> courseDetailDTO.getCourse().getCourseName())  // Lấy tên khóa học từ đối tượng Course
                    .collect(Collectors.toList());

            // Đưa danh sách tên khóa học vào ComboBox
            courseComboBox.getItems().clear();  // Xóa tất cả item cũ (nếu có)
            courseComboBox.getItems().addAll(courseNames);  // Thêm khóa học của giảng viên hiện tại
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi khi lấy danh sách khóa học.");
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
