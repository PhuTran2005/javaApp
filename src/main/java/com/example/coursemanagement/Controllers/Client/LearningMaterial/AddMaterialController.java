package com.example.coursemanagement.Controllers.Client.LearningMaterial;

import com.example.coursemanagement.Models.LearningMaterial;
import com.example.coursemanagement.Repository.LearningMaterialRepository;
import com.example.coursemanagement.Utils.Alerts;
import com.example.coursemanagement.Utils.DatabaseConfig;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.Connection;

public class AddMaterialController {

Alerts alerts = new Alerts();
    private String selectedVideoFilePath;
    private String selectedDocumentFilePath;
    private boolean isEditMode = false;

    @FXML private TextField titleField;
    @FXML private TextArea descriptionField;
    @FXML private TextField videoFileNameField;
    @FXML private TextField documentFileNameField;
    @FXML private Button cancelButton;
    @FXML private Button saveButton;

    @FXML
    private Label courseNameLabel;

    private File selectedDocumentFile;

    private int courseId;
    private int uploadedByUserId;

    private Runnable onMaterialAdded;  // Callback để reload danh sách sau khi thêm

    public void setCourseId(int id) {
        this.courseId = id;
    }

    public void setCourseName(String name) {
        courseNameLabel.setText(name);  // Gán vào label hiển thị
    }
    public void setUploadedByUserId(int userId) {
        this.uploadedByUserId = userId;
    }

    public void setOnMaterialAdded(Runnable callback) {
        this.onMaterialAdded = callback;
    }

    private LearningMaterial editingMaterial;

    public void setMaterialForEdit(LearningMaterial material) {
        this.editingMaterial = material;
        titleField.setText(material.getTitle());
        descriptionField.setText(material.getDescription());
        videoFileNameField.setText(material.getVideoPath());
        documentFileNameField.setText(material.getDocumentName());

        // Có thể lưu path nếu cần
        selectedVideoFilePath = material.getVideoPath();
        selectedDocumentFilePath = material.getDocumentPath();

        isEditMode = true;  // <-- Biến flag để xử lý khi nhấn "Lưu"
    }


    @FXML
    private void handleChooseDocument() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn tài liệu");
        selectedDocumentFile = fileChooser.showOpenDialog(null);
        if (selectedDocumentFile != null) {
            documentFileNameField.setText(selectedDocumentFile.getName());
        }
    }

    @FXML
    private void handleChooseVideo() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Thêm đường dẫn video");
        dialog.setHeaderText("Nhập URL video hoặc đường dẫn cục bộ:");
        dialog.setContentText("Đường dẫn video:");
        dialog.showAndWait().ifPresent(url -> videoFileNameField.setText(url));
    }

    @FXML
    private void handleSave() {
        String title = titleField.getText() != null ? titleField.getText().trim() : "";
        String description = descriptionField.getText() != null ? descriptionField.getText().trim() : "";

        String videoPath;
        if (videoFileNameField.getText() != null && !videoFileNameField.getText().trim().isEmpty()) {
            videoPath = videoFileNameField.getText().trim();
            selectedVideoFilePath = videoPath;  // cập nhật nếu có đường dẫn mới
        } else {
            videoPath = null;  // <- không dùng lại đường dẫn cũ
        }


        String documentPath = selectedDocumentFile != null ? selectedDocumentFile.getAbsolutePath() : selectedDocumentFilePath;
        String documentName = selectedDocumentFile != null ? selectedDocumentFile.getName() : (editingMaterial != null ? editingMaterial.getDocumentName() : null);

        // Kiểm tra điều kiện bắt buộc
        if (title.isEmpty() ||
                ((videoPath == null || videoPath.isEmpty()) && (documentPath == null || documentPath.isEmpty()))) {
            showAlert(Alert.AlertType.ERROR, "Bạn phải nhập tiêu đề và ít nhất 1 nội dung: video hoặc tài liệu.");
            return;
        }

        try (Connection conn = DatabaseConfig.getConnection()) {
            boolean success;
            if (isEditMode && editingMaterial != null) {
                editingMaterial.setTitle(title);
                editingMaterial.setDescription(description);
                editingMaterial.setVideoPath(videoPath);
                editingMaterial.setVideoName((videoPath != null && !videoPath.isEmpty()) ? "Video bài giảng" : null);
                editingMaterial.setDocumentPath(documentPath);
                editingMaterial.setDocumentName(documentName);

                success = new LearningMaterialRepository().updateMaterial(conn, editingMaterial);
            } else {
                LearningMaterial material = new LearningMaterial();
                material.setCourseId(courseId);
                material.setTitle(title);
                material.setDescription(description);
                material.setVideoPath(videoPath);
                material.setVideoName((videoPath != null && !videoPath.isEmpty()) ? "Video bài giảng" : null);
                material.setDocumentPath(documentPath);
                material.setDocumentName(documentName);
                material.setUploadedBy(uploadedByUserId);

                LearningMaterialRepository.insertMaterial(conn, material);
                success = true;
            }

            if (success) {
                alerts.showSuccessAlert("Thêm/Cập nhật bài giảng thành công!");

                if (onMaterialAdded != null) {
                    onMaterialAdded.run();
                }

                closeWindow();
            }

        } catch (Exception e) {
            e.printStackTrace();
            alerts.showErrorAlert("Lỗi: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void showAlert(Alert.AlertType type, String content) {
        Alert alert = new Alert(type);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}
