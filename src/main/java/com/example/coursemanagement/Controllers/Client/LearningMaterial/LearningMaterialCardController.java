package com.example.coursemanagement.Controllers.Client.LearningMaterial;

import com.example.coursemanagement.Models.LearningMaterial;
import com.example.coursemanagement.Models.Model;
import com.example.coursemanagement.Repository.LearningMaterialRepository;
import com.example.coursemanagement.Service.LearningMaterialService;
import com.example.coursemanagement.Utils.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;

public class LearningMaterialCardController {

    @FXML
    private Label titleLabel, descriptionLabel, viewsLabel, videoFileNameLabel, docFileNameLabel;

    @FXML
    private Hyperlink watchVideoLink, openDocumentLink;

    @FXML
    private VBox videoSection, documentSection;

    @FXML
    private Button deleteButton, updateButton;

    private LearningMaterial material;

    private Runnable onMaterialDeleted;

    public void setOnMaterialDeleted(Runnable callback) {
        this.onMaterialDeleted = callback;
    }

    public void setData(LearningMaterial material) {
        this.material = material;

        titleLabel.setText(material.getTitle());
        descriptionLabel.setText(material.getDescription());

        // VIDEO section
        if (material.getVideoPath() != null && !material.getVideoPath().isEmpty()) {
            videoFileNameLabel.setText(material.getVideoName());
            watchVideoLink.setOnAction(e -> openInWebView(material.getVideoPath()));

            viewsLabel.setText(material.getViews() + " lượt xem");
            videoSection.setVisible(true);
            videoSection.setManaged(true);
        } else {
            videoSection.setVisible(false);
            videoSection.setManaged(false);
        }

        // DOCUMENT section
        if (material.getDocumentPath() != null && !material.getDocumentPath().isEmpty()) {
            docFileNameLabel.setText(material.getDocumentName());
            openDocumentLink.setOnAction(e -> openFile(material.getDocumentPath()));
            documentSection.setVisible(true);
            documentSection.setManaged(true);
        } else {
            documentSection.setVisible(false);
            documentSection.setManaged(false);
        }
    }

    private void openInWebView(String videoUrl) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/Intructor_Learing/VideoPlayer.fxml"));
            Parent videoView = loader.load();

            VideoPlayerController controller = loader.getController();
            controller.loadVideo(videoUrl);
            // ✅ Truyền thêm thông tin bài giảng
            controller.setVideoInfo(
                    material.getTitle(),
                    material.getDescription(),
                    material.getViews()
            );

            // ✅ Truyền thông tin khóa học cho controller để quay lại được LearningView
            controller.setCourseInfo(
                    material.getCourseId(),
                    material.getUploadedBy(),
                    material.getCourseName() != null ? material.getCourseName() : "Tên khóa học"
            );

            // ✅ Gắn parentPane để back lại
            BorderPane root = (BorderPane) Model.getInstance().getViewFactory().getClientRoot();
            controller.setParentPane(root);
            root.setCenter(videoView);

        } catch (IOException e) {
            e.printStackTrace();
            new Alerts().showErrorAlert("Không thể mở video.");
        }
    }



    private void openLink(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openFile(String path) {
        try {
            Desktop.getDesktop().open(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
Alerts alerts = new Alerts();
    @FXML
    private void handleDelete() {
        boolean confirm = alerts.showConfirmationWarmingAlert("Xác nhận Bạn có chắc muốn xóa bài giảng này?");
        if (!confirm) return;

        LearningMaterialService service = new LearningMaterialService();
        boolean success = service.deleteMaterial(material.getMaterialId());


        if (success) {
            // Gọi callback để cập nhật danh sách lại
            if (onMaterialDeleted != null) {
                onMaterialDeleted.run();
            }

            System.out.println("Đã xóa bài giảng: " + material.getTitle());
        } else {
            alerts.showErrorAlert("Lỗi Không thể xóa bài giảng.");
        }
    }



    @FXML
    private void handleUpdate() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/Intructor_Learing/AddMaterial.fxml"));
            Parent root = loader.load();

            AddMaterialController controller = loader.getController();
            controller.setMaterialForEdit(material);
            controller.setCourseId(material.getCourseId()); // cần thiết
            controller.setUploadedByUserId(material.getUploadedBy()); // nếu cần
            controller.setOnMaterialAdded(onMaterialDeleted); // Gán callback reload

            Stage stage = new Stage();
            stage.setTitle("Cập nhật bài giảng");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait(); // chờ đóng mới tiếp tục

        } catch (IOException e) {
            e.printStackTrace();
            alerts.showErrorAlert("Lỗi khi mở form cập nhật.");
        }
    }

}
