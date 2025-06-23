package com.example.coursemanagement.Controllers.Client.LearningMaterial;

import com.example.coursemanagement.Models.Model;
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

        if (videoUrl.contains("youtube.com") || videoUrl.contains("youtu.be")) {
            String videoId = extractYouTubeId(videoUrl);
            String embed = """
                <html>
                  <body style="margin:0">
                    <iframe width="100%%" height="100%%" 
                            src="https://www.youtube.com/embed/%s" 
                            frameborder="0" allow="autoplay; encrypted-media" allowfullscreen></iframe>
                  </body>
                </html>
                """.formatted(videoId);

            engine.loadContent(embed);
        } else {
            engine.load(videoUrl); // Cho các link nhúng khác như Google Drive
        }
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
