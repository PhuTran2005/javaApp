package com.example.coursemanagement.Controllers.Client.AssignmentsManagement;

import com.example.coursemanagement.Dto.SubmissionStatusDTO;
import com.example.coursemanagement.Service.SubmissionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import java.io.File;
import java.awt.Desktop;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SubmissionListController {

    @FXML
    private TableView<SubmissionStatusDTO> submissionTable;

    @FXML
    private TableColumn<SubmissionStatusDTO, String> nameCol;

    @FXML
    private TableColumn<SubmissionStatusDTO, String> emailCol;

    @FXML
    private TableColumn<SubmissionStatusDTO, String> statusCol;

    @FXML
    private TableColumn<SubmissionStatusDTO, String> timeCol;

    @FXML
    private TableColumn<SubmissionStatusDTO, String> fileCol;

    private final ObservableList<SubmissionStatusDTO> submissionList = FXCollections.observableArrayList();

    private final SubmissionService submissionService;

    public SubmissionListController() {
        this.submissionService = new SubmissionService();
    }

    // Initialize method to set up TableView columns
    public void initialize() {
        // Set up columns
        nameCol.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Submitted or not
        statusCol.setCellValueFactory(cellData -> {
            boolean submitted = cellData.getValue().isSubmitted();
            return javafx.beans.binding.Bindings.createStringBinding(() -> submitted ? "✔" : "✘");
        });

        // Format submission time
        timeCol.setCellValueFactory(cellData -> {
            var submittedAt = cellData.getValue().getSubmittedAt();
            if (submittedAt != null) {
                String formatted = submittedAt.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
                return javafx.beans.binding.Bindings.createStringBinding(() -> formatted);
            } else {
                return javafx.beans.binding.Bindings.createStringBinding(() -> "-");
            }
        });

        // File name (or empty if not submitted)
        fileCol.setCellValueFactory(cellData -> {
            String filePath = cellData.getValue().getFilePath();
            return javafx.beans.binding.Bindings.createStringBinding(() ->
                    (filePath != null && !filePath.isEmpty()) ? extractFileName(filePath) : "-"
            );
        });

        // Cập nhật cell factory cho cột "File nộp"
        fileCol.setCellFactory(new Callback<TableColumn<SubmissionStatusDTO, String>, TableCell<SubmissionStatusDTO, String>>() {
            @Override
            public TableCell<SubmissionStatusDTO, String> call(TableColumn<SubmissionStatusDTO, String> param) {
                return new TableCell<SubmissionStatusDTO, String>() {
                    private final Button btnOpen = new Button("Mở");

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                        } else {
                            // Kiểm tra nếu học viên đã nộp bài
                            SubmissionStatusDTO submission = getTableRow() != null ? getTableRow().getItem() : null;
                            if (submission != null && submission.isSubmitted()) {
                                setGraphic(btnOpen);  // Hiển thị nút "Mở" nếu đã nộp
                                btnOpen.setOnAction(event -> openFile(submission.getFilePath()));  // Mở file khi nhấn nút
                            } else {
                                setGraphic(null);  // Không hiển thị nút "Mở" nếu chưa nộp
                            }
                        }
                    }
                };
            }
        });

        // Set data in TableView
        submissionTable.setItems(submissionList);
    }


    // Method to load submissions for a specific assignment
    public void loadSubmissionsForAssignment(int assignmentId) {
        try {
            List<SubmissionStatusDTO> list = submissionService.getSubmissionStatusForAssignment(assignmentId);
            setSubmissionList(FXCollections.observableArrayList(list));
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Lỗi khi tải danh sách nộp bài.");
        }
    }

    // Show error alert
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Update submission list in TableView
    public void setSubmissionList(ObservableList<SubmissionStatusDTO> submissions) {
        submissionList.setAll(submissions);
    }

    // Extract file name from file path
    private String extractFileName(String filePath) {
        if (filePath == null || filePath.isEmpty()) return "";
        int lastSlash = filePath.lastIndexOf("\\");
        return (lastSlash >= 0) ? filePath.substring(lastSlash + 1) : filePath;
    }

    // Open the file
    private void openFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            try {
                Desktop.getDesktop().open(file);  // Open file with default system application
            } catch (Exception e) {
                e.printStackTrace();
                showError("Không thể mở file.");
            }
        } else {
            showError("File không tồn tại.");
        }
    }
}
