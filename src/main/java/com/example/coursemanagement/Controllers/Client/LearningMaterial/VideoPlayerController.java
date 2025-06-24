package com.example.coursemanagement.Controllers.Client.LearningMaterial;

import com.example.coursemanagement.Models.Model;
import com.example.coursemanagement.Repository.LearningMaterialRepository;
import com.example.coursemanagement.Utils.DatabaseConfig;
import com.example.coursemanagement.Utils.SessionManager;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.Label;

import java.io.IOException;
import java.sql.Connection;

public class VideoPlayerController {

    @FXML private WebView webView;
    @FXML private Button closeButton;
    @FXML private Button backButton;
    @FXML
    private Label titleLabel, descriptionLabel, viewsLabel;


    private Stage stage;
    private BorderPane parentPane;

    public void setParentPane(BorderPane parentPane) {
        this.parentPane = parentPane;
    }
    private int currentCourseId;
    private int currentUserId;
    private String courseName;
    private int materialId;
    public void setMaterialId(int id) {
        this.materialId = id;
    }


    public void setCourseInfo(int courseId, int userId, String courseName) {
        this.currentCourseId = courseId;
        this.currentUserId = userId;
        this.courseName = courseName;
    }

    public void setVideoInfo(String title, String description, int views) {
        titleLabel.setText(title);
        descriptionLabel.setText(description);
        viewsLabel.setText(views + " lượt xem");
    }

    public void initialize() {
        // Nếu có gán stage từ bên ngoài, thì không cần đoạn này
        // Nếu muốn tự bắt sự kiện đóng:
       // closeButton.getScene().getWindow().setOnHidden(e -> stopVideo());
        backButton.setOnAction(event -> handleBack());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        // Gắn sự kiện đóng cửa sổ để dọn webView
        stage.setOnCloseRequest(e -> stopVideo());
    }

    @FXML
    private void handleClose() {
        stopVideo();  // Dừng video
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    public void loadVideo(String videoUrl) {
        WebEngine engine = webView.getEngine();

        // Tăng view nếu là STUDENT
        if (SessionManager.getInstance().isStudent()) {
            try (Connection conn = DatabaseConfig.getConnection()) {
                new LearningMaterialRepository().increaseViewCount(conn, materialId);
                System.out.println("✅ View count +1 for materialId = " + materialId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Tiếp tục load video bình thường
        if (videoUrl.contains("youtube.com") || videoUrl.contains("youtu.be")) {
            String videoId = extractYouTubeId(videoUrl);
            String embed = """
        <html>
          <body style="margin:0">
            <iframe width="100%%" height="100%%" 
                    src="https://www.youtube.com/embed/%s?autoplay=1&mute=1" 
                    frameborder="0" allow="autoplay; encrypted-media" allowfullscreen></iframe>
          </body>
        </html>
        """.formatted(videoId);
            engine.loadContent(embed);

        } else if (videoUrl.endsWith(".mp4")) {
            String embed = """
        <html>
          <body style="margin:0">
            <video width="100%%" height="100%%" autoplay muted controls>
              <source src="%s" type="video/mp4">
              Trình duyệt không hỗ trợ video này.
            </video>
          </body>
        </html>
        """.formatted(videoUrl);
            engine.loadContent(embed);

        } else if (videoUrl.contains("drive.google.com")) {
            // Convert drive file link to preview embeddable form
            String previewUrl = convertGoogleDriveLinkToEmbed(videoUrl);
            String embed = """
        <html>
          <body style="margin:0">
            <iframe src="%s" width="100%%" height="100%%" allow="autoplay"></iframe>
          </body>
        </html>
        """.formatted(previewUrl);
            engine.loadContent(embed);

        } else {
            // Nếu không biết loại gì, thử load thẳng (có thể fail)
            engine.load(videoUrl);
        }

    }

    private String convertGoogleDriveLinkToEmbed(String url) {
        // Ví dụ: https://drive.google.com/file/d/FILE_ID/view?usp=sharing
        // → https://drive.google.com/file/d/FILE_ID/preview
        if (url.contains("/view")) {
            return url.replace("/view", "/preview");
        }
        return url;
    }


    private String extractYouTubeId(String url) {
        String id = null;
        if (url.contains("watch?v=")) {
            id = url.substring(url.indexOf("watch?v=") + 8);
        } else if (url.contains("youtu.be/")) {
            id = url.substring(url.indexOf("youtu.be/") + 9);
        } else {
            id = url;
        }

        // Cắt phần dư sau id như &t=...
        if (id.contains("&")) {
            id = id.substring(0, id.indexOf("&"));
        }

        return id;
    }
    public void stopVideo() {
        if (webView != null) {
            webView.getEngine().load(null);  // hoặc webView.getEngine().load("");
            webView = null; // OPTIONAL: giúp GC thu hồi
        }
    }

    @FXML
    private void handleBack() {
        try {
            // DỪNG video trước
            stopVideo();

            // Load lại LearningView
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/Intructor_Learing/LearningView.fxml"));
            Parent learningView = loader.load();

            LearningViewController controller = loader.getController();
            controller.setCourse(currentCourseId, currentUserId, courseName);

            if (parentPane != null) {
                parentPane.setCenter(null);  // Clear view hiện tại trước khi gán mới
                parentPane.setCenter(learningView);

                // Hiệu ứng mờ khi chuyển
                FadeTransition ft = new FadeTransition(Duration.millis(300), learningView);
                ft.setFromValue(0);
                ft.setToValue(1);
                ft.play();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
