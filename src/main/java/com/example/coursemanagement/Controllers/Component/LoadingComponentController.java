package com.example.coursemanagement.Controllers.Component;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoadingComponentController extends StackPane implements Initializable {

    @FXML
    private VBox loadingContainer;
    @FXML
    private Label loadingText;
    @FXML
    private HBox spinnerContainer;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private VBox contentContainer;

    private Timeline spinnerAnimation;
    private Timeline pulseAnimation;
    private boolean isLoading = false;
    private String currentTheme = "default";

    public LoadingComponentController() {
        loadFXML();
        setupDefaultStyles();
    }

    private void loadFXML() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/HelpFxml/LoadingComponent.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            System.err.println("Không thể load Loading.fxml: " + e.getMessage());
            e.printStackTrace();
            createUIFallback();
        }
    }

    private void createUIFallback() {
        this.getChildren().clear();

        // Main container
        loadingContainer = new VBox();
        loadingContainer.setAlignment(Pos.CENTER);
        loadingContainer.setSpacing(25);
        loadingContainer.setPrefSize(300, 200);
        loadingContainer.setMaxSize(300, 200);

        // Content container for animations
        contentContainer = new VBox();
        contentContainer.setAlignment(Pos.CENTER);
        contentContainer.setSpacing(20);

        // Spinner container
        spinnerContainer = new HBox();
        spinnerContainer.setAlignment(Pos.CENTER);
        spinnerContainer.setSpacing(8);

        // Loading text
        loadingText = new Label("Đang tải...");
        loadingText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Progress bar
        progressBar = new ProgressBar();
        progressBar.setPrefWidth(200);
        progressBar.setProgress(-1); // Indeterminate

        // Assemble UI
        contentContainer.getChildren().addAll(spinnerContainer, loadingText, progressBar);
        loadingContainer.getChildren().add(contentContainer);
        this.getChildren().add(loadingContainer);

        setupDefaultStyles();
    }

    private void setupDefaultStyles() {
        this.setAlignment(Pos.CENTER);
        this.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        this.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // Apply blur effect to background
        this.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");

        if (loadingContainer != null) {
            loadingContainer.setStyle(
                    "-fx-background-color: white;" +
                            "-fx-background-radius: 15;" +
                            "-fx-padding: 30;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 20, 0.3, 0, 5);"
            );
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupLoadingAnimation();
        setupPulseAnimation();
        setVisible(false);
        setDisable(true);
        applyTheme(currentTheme);
    }

    private void setupLoadingAnimation() {
        createAdvancedSpinner();
        createSpinnerAnimation();
    }

    private void createAdvancedSpinner() {
        if (spinnerContainer != null) {
            spinnerContainer.getChildren().clear();
            spinnerContainer.setSpacing(6);

            // Create circles with gradient effect
            for (int i = 0; i < 8; i++) {
                Circle circle = new Circle(4);
                circle.setFill(Color.web("#18CFC4"));
                circle.setOpacity(0.2);

                // Add shadow effect
                DropShadow shadow = new DropShadow();
                shadow.setColor(Color.web("#18CFC4", 0.5));
                shadow.setRadius(3);
                shadow.setSpread(0.2);
                circle.setEffect(shadow);

                spinnerContainer.getChildren().add(circle);
            }
        }
    }

    private void createSpinnerAnimation() {
        if (spinnerContainer == null || spinnerContainer.getChildren().isEmpty()) {
            return;
        }

        spinnerAnimation = new Timeline();
        spinnerAnimation.setCycleCount(Timeline.INDEFINITE);
        spinnerAnimation.getKeyFrames().clear();

        int circleCount = spinnerContainer.getChildren().size();

        for (int i = 0; i < circleCount; i++) {
            Node node = spinnerContainer.getChildren().get(i);
            if (!(node instanceof Circle)) continue;

            Circle circle = (Circle) node;
            double delay = i * 100;

            // Phase 1: start wave
            KeyFrame kf1 = new KeyFrame(
                    Duration.millis(delay),
                    new KeyValue(circle.opacityProperty(), 0.2),
                    new KeyValue(circle.scaleXProperty(), 1.0),
                    new KeyValue(circle.scaleYProperty(), 1.0)
            );

            // Phase 2: expand
            KeyFrame kf2 = new KeyFrame(
                    Duration.millis(delay + 400),
                    new KeyValue(circle.opacityProperty(), 1.0, Interpolator.EASE_BOTH),
                    new KeyValue(circle.scaleXProperty(), 1.5, Interpolator.EASE_BOTH),
                    new KeyValue(circle.scaleYProperty(), 1.5, Interpolator.EASE_BOTH)
            );

            // Phase 3: shrink back
            KeyFrame kf3 = new KeyFrame(
                    Duration.millis(delay + 800),
                    new KeyValue(circle.opacityProperty(), 0.2, Interpolator.EASE_BOTH),
                    new KeyValue(circle.scaleXProperty(), 1.0, Interpolator.EASE_BOTH),
                    new KeyValue(circle.scaleYProperty(), 1.0, Interpolator.EASE_BOTH)
            );

            spinnerAnimation.getKeyFrames().addAll(kf1, kf2, kf3);
        }

        // KHÔNG dùng setDuration, Timeline sẽ lặp lại toàn bộ theo keyframe cuối cùng
    }

    private void setupPulseAnimation() {
        if (contentContainer != null) {
            pulseAnimation = new Timeline();
            pulseAnimation.setCycleCount(Timeline.INDEFINITE);
            pulseAnimation.setAutoReverse(true);

            KeyFrame kf1 = new KeyFrame(
                    Duration.ZERO,
                    new KeyValue(contentContainer.scaleXProperty(), 1.0),
                    new KeyValue(contentContainer.scaleYProperty(), 1.0)
            );

            KeyFrame kf2 = new KeyFrame(
                    Duration.millis(1000),
                    new KeyValue(contentContainer.scaleXProperty(), 1.02, Interpolator.EASE_BOTH),
                    new KeyValue(contentContainer.scaleYProperty(), 1.02, Interpolator.EASE_BOTH)
            );

            pulseAnimation.getKeyFrames().addAll(kf1, kf2);
        }
    }

    // Public methods for controlling loading
    public void show() {
        show("Đang tải...");
    }

    public void show(String message) {
        Platform.runLater(() -> {
            if (isLoading) return;

            isLoading = true;
            setLoadingMessage(message);
            setVisible(true);
            setDisable(false);
            toFront();

            // Start animations
            if (spinnerAnimation != null) {
                spinnerAnimation.play();
            }
            if (pulseAnimation != null) {
                pulseAnimation.play();
            }

            // Entrance animation
            playEntranceAnimation();
        });
    }

    public void hide() {
        Platform.runLater(() -> {
            if (!isLoading) return;

            playExitAnimation(() -> {
                setVisible(false);
                setDisable(true);
                isLoading = false;

                // Stop animations
                if (spinnerAnimation != null) {
                    spinnerAnimation.stop();
                }
                if (pulseAnimation != null) {
                    pulseAnimation.stop();
                }

                // Reset progress
                if (progressBar != null) {
                    progressBar.setProgress(-1);
                }
            });
        });
    }

    private void playEntranceAnimation() {
        // Background fade in
        FadeTransition backgroundFade = new FadeTransition(Duration.millis(300), this);
        backgroundFade.setFromValue(0.0);
        backgroundFade.setToValue(1.0);

        // Container scale and fade
        if (loadingContainer != null) {
            loadingContainer.setScaleX(0.7);
            loadingContainer.setScaleY(0.7);
            loadingContainer.setOpacity(0.0);

            ScaleTransition scale = new ScaleTransition(Duration.millis(400), loadingContainer);
            scale.setFromX(0.7);
            scale.setFromY(0.7);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.setInterpolator(Interpolator.SPLINE(0.25, 0.46, 0.45, 0.94));

            FadeTransition fade = new FadeTransition(Duration.millis(400), loadingContainer);
            fade.setFromValue(0.0);
            fade.setToValue(1.0);

            ParallelTransition parallel = new ParallelTransition(backgroundFade, scale, fade);
            parallel.play();
        } else {
            backgroundFade.play();
        }
    }

    private void playExitAnimation(Runnable onFinished) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(250), this);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> onFinished.run());

        if (loadingContainer != null) {
            ScaleTransition scaleOut = new ScaleTransition(Duration.millis(250), loadingContainer);
            scaleOut.setFromX(1.0);
            scaleOut.setFromY(1.0);
            scaleOut.setToX(0.9);
            scaleOut.setToY(0.9);

            ParallelTransition parallel = new ParallelTransition(fadeOut, scaleOut);
            parallel.play();
        } else {
            fadeOut.play();
        }
    }

    // Utility methods
    public void setLoadingMessage(String message) {
        Platform.runLater(() -> {
            if (loadingText != null) {
                loadingText.setText(message);
            }
        });
    }

    public void setProgress(double progress) {
        Platform.runLater(() -> {
            if (progressBar != null) {
                progressBar.setProgress(progress);
            }
        });
    }

    public boolean isLoading() {
        return isLoading;
    }

    // Theme methods
    public void applyTheme(String theme) {
        Platform.runLater(() -> {
            currentTheme = theme;
            if (loadingContainer == null) return;

            switch (theme.toLowerCase()) {
                case "dark":
                    loadingContainer.setStyle(
                            "-fx-background-color: #2d3748;" +
                                    "-fx-background-radius: 15;" +
                                    "-fx-padding: 30;" +
                                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 20, 0.3, 0, 5);"
                    );
                    if (loadingText != null) {
                        loadingText.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
                    }
                    this.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8);");
                    break;

                case "glass":
                    loadingContainer.setStyle(
                            "-fx-background-color: rgba(255, 255, 255, 0.1);" +
                                    "-fx-background-radius: 15;" +
                                    "-fx-padding: 30;" +
                                    "-fx-border-color: rgba(255, 255, 255, 0.2);" +
                                    "-fx-border-width: 1;" +
                                    "-fx-border-radius: 15;" +
                                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 20, 0.3, 0, 5);"
                    );
                    if (loadingText != null) {
                        loadingText.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
                    }
                    this.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3);");

                    // Add blur effect
                    GaussianBlur blur = new GaussianBlur(10);
                    this.setEffect(blur);
                    break;

                default: // light theme
                    loadingContainer.setStyle(
                            "-fx-background-color: white;" +
                                    "-fx-background-radius: 15;" +
                                    "-fx-padding: 30;" +
                                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 20, 0.3, 0, 5);"
                    );
                    if (loadingText != null) {
                        loadingText.setStyle("-fx-text-fill: #2d3748; -fx-font-size: 16px; -fx-font-weight: bold;");
                    }
                    this.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");
                    this.setEffect(null);
                    break;
            }
        });
    }

    public void setSpinnerColor(String color) {
        Platform.runLater(() -> {
            if (spinnerContainer != null) {
                for (javafx.scene.Node node : spinnerContainer.getChildren()) {
                    if (node instanceof Circle) {
                        ((Circle) node).setFill(Color.web(color));

                        // Update shadow color
                        DropShadow shadow = new DropShadow();
                        shadow.setColor(Color.web(color, 0.5));
                        shadow.setRadius(3);
                        shadow.setSpread(0.2);
                        node.setEffect(shadow);
                    }
                }
            }
        });
    }

    // Static utility methods
    public static void addToPane(Pane parent, LoadingComponentController loading) {
        Platform.runLater(() -> {
            if (!parent.getChildren().contains(loading)) {
                parent.getChildren().add(loading);
            }

            if (parent instanceof AnchorPane) {
                AnchorPane.setTopAnchor(loading, 0.0);
                AnchorPane.setBottomAnchor(loading, 0.0);
                AnchorPane.setLeftAnchor(loading, 0.0);
                AnchorPane.setRightAnchor(loading, 0.0);
            }

            loading.toFront();
        });
    }

    public void removeFromParent() {
        Platform.runLater(() -> {
            if (this.getParent() instanceof Pane) {
                ((Pane) this.getParent()).getChildren().remove(this);
            }
        });
    }
}