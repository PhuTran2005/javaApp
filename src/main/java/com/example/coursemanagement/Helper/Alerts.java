package com.example.coursemanagement.Helper;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.Optional;

public class Alerts {
    public void showSuccessAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, content, ButtonType.OK);
        alert.setTitle("Thông Báo");
        alert.setHeaderText(null);

        // Thêm ảnh vào Alert
        ImageView imageView = new ImageView(new Image(getClass().getResource("/Images/successful.png").toExternalForm()));
        imageView.setFitHeight(50);
        imageView.setFitWidth(50);
        alert.setGraphic(imageView);

        // Áp dụng CSS
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/Styles/Alerts.css").toExternalForm());
        alert.getDialogPane().getStyleClass().add("custom-alert");

        alert.showAndWait();
    }

    public void showErrorAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR, content, ButtonType.OK);
        alert.setTitle("Thông Báo");
        alert.setHeaderText(null);

        // Thêm ảnh vào Alert
        ImageView imageView = new ImageView(new Image(getClass().getResource("/Images/close.png").toExternalForm()));
        imageView.setFitHeight(50);
        imageView.setFitWidth(50);
        alert.setGraphic(imageView);

        // Áp dụng CSS
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/Styles/Alerts.css").toExternalForm());
        alert.getDialogPane().getStyleClass().add("custom-alert");

        alert.showAndWait();
    }

    public boolean showConfirmationWarmingAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, content);

        alert.setTitle("Thông Báo");
        alert.setHeaderText(null);

        // Thêm ảnh vào Alert
        ImageView imageView = new ImageView(new Image(getClass().getResource("/Images/alert.png").toExternalForm()));
        imageView.setFitHeight(50);
        imageView.setFitWidth(50);
        alert.setGraphic(imageView);

        // Áp dụng CSS
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/Styles/Alerts.css").toExternalForm());
        alert.getDialogPane().getStyleClass().add("custom-alert");


        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            return true;
        }
        return false;

    }
    public boolean showConfirmationSelectedAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, content);

        alert.setTitle("Thông Báo");
        alert.setHeaderText(null);

        // Thêm ảnh vào Alert
        ImageView imageView = new ImageView(new Image(getClass().getResource("/Images/question-mark.png").toExternalForm()));
        imageView.setFitHeight(50);
        imageView.setFitWidth(50);
        alert.setGraphic(imageView);

        // Áp dụng CSS
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/Styles/Alerts.css").toExternalForm());
        alert.getDialogPane().getStyleClass().add("custom-alert");


        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            return true;
        }
        return false;

    }

}
