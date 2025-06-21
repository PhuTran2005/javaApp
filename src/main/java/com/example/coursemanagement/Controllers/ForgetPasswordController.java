package com.example.coursemanagement.Controllers;

import com.example.coursemanagement.Repository.UserRepository;
import com.example.coursemanagement.Utils.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javafx.event.ActionEvent;

import java.io.IOException;

public class ForgetPasswordController {
    @FXML
    private TextField emailField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private Label messageLabel;

    private final UserRepository userRepository = new UserRepository();
    private final Alerts alerts = new Alerts();

    @FXML
    private void handleResetPassword(ActionEvent event) {
        String email = emailField.getText().trim();
        String newPassword = newPasswordField.getText();

        if (email.isEmpty() || newPassword.isEmpty()) {
            messageLabel.setText("Vui lòng nhập đầy đủ thông tin");
            messageLabel.setVisible(true);
            return;
        }

        boolean exists = userRepository.checkEmailExists(email);
        if (!exists) {
            messageLabel.setText("Email không tồn tại");
            messageLabel.setVisible(true);
            return;
        }

        boolean success = userRepository.updatePasswordByEmail(email, newPassword);
        if (success) {
            alerts.showSuccessAlert("Đổi mật khẩu thành công!");
            // Quay về đăng nhập
            switchToLogin(event);
        } else {
            alerts.showErrorAlert("Cập nhật thất bại! Vui lòng thử lại.");
        }
    }

    @FXML
    private void switchToLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("LOGIN");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleKeyPress(javafx.scene.input.KeyEvent event) {
        switch (event.getCode()) {
            case ENTER:
                handleResetPassword(null); // Giả lập khi nhấn ENTER là ấn nút
                break;
            default:
                break;
        }
    }


}
