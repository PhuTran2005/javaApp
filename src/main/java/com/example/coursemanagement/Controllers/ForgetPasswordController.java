package com.example.coursemanagement.Controllers;

import com.example.coursemanagement.Repository.UserRepository;
import com.example.coursemanagement.Utils.Alerts;
import com.example.coursemanagement.Utils.EmailUtil;
import jakarta.mail.MessagingException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javafx.event.ActionEvent;

import java.io.IOException;
import java.util.Random;

public class ForgetPasswordController {
    @FXML
    private TextField emailField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private Label messageLabel;

    @FXML
    private TextField codeField;

    private final UserRepository userRepository = new UserRepository();
    private final Alerts alerts = new Alerts();
    private String generatedCode; // Lưu mã xác nhận

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

    private void sendVerificationCode(String email) {
        generatedCode = String.format("%06d", new Random().nextInt(1000000));

        String subject = "Mã xác nhận đặt lại mật khẩu";
        String body = "Mã xác nhận của bạn là: " + generatedCode;

        // Gửi email (cần cài đặt thư viện JavaMail)
        try {
            EmailUtil.sendEmail(email, subject, body); // tạo class EmailUtil riêng
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Không thể gửi mã xác nhận. Vui lòng thử lại!");
        }
    }

    private void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleSendCode(ActionEvent event) throws MessagingException {
        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            messageLabel.setText("Vui lòng nhập email");
            messageLabel.setVisible(true);
            return;
        }

        boolean exists = userRepository.checkEmailExists(email);
        if (!exists) {
            messageLabel.setText("Email không tồn tại");
            messageLabel.setVisible(true);
            return;
        }

        generatedCode = String.valueOf(new Random().nextInt(900000) + 100000); // 6 chữ số

        String subject = "Mã xác nhận đổi mật khẩu";
        String content = "Mã xác nhận của bạn là: " + generatedCode;

        EmailUtil.sendEmail(email, subject, content);

        messageLabel.setText("Đã gửi mã xác nhận qua email");
        messageLabel.setVisible(true);
    }

    @FXML
    private void handlePassword(ActionEvent event) {
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
}
