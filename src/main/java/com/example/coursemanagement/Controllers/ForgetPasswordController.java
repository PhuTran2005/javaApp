package com.example.coursemanagement.Controllers;

import com.example.coursemanagement.Repository.UserRepository;
import com.example.coursemanagement.Utils.Alerts;
import com.example.coursemanagement.Utils.EmailUtil;
import jakarta.mail.MessagingException;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class ForgetPasswordController implements Initializable {
    @FXML
    public AnchorPane forgetPasswordForm;
    @FXML

    public Button sendCodeButton;
    @FXML
    public Button btnResetPassword;
    @FXML
    private TextField emailField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private Label messageLabel;

    @FXML
    private TextField codeField;

    private String otpCode = "";

    private final UserRepository userRepository = new UserRepository();
    private final Alerts alerts = new Alerts();
    private String generatedCode; // Lưu mã xác nhận
    // Cho ImageView logo xoay khi vừa load
    public void initialize(URL url, ResourceBundle resourceBundle) {
        pulseButton(sendCodeButton);
        pulseButton(btnResetPassword);
    }
    public void shake(Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(50), node);
        tt.setFromX(0);
        tt.setByX(10);
        tt.setCycleCount(6);
        tt.setAutoReverse(true);
        tt.play();
    }
    public void pulseButton(Button button) {
        ScaleTransition st = new ScaleTransition(Duration.seconds(1), button);
        st.setFromX(1);
        st.setToX(1.05);
        st.setFromY(1);
        st.setToY(1.05);
        st.setAutoReverse(true);
        st.setCycleCount(ScaleTransition.INDEFINITE);
        st.play();
    }
    @FXML
    private void handleResetPassword(ActionEvent event) {
        String email = emailField.getText().trim();
        String code = codeField.getText().trim();
        String newPassword = newPasswordField.getText();
        if (otpCode.trim().isEmpty()) {
            alerts.showConfirmationWarmingAlert("Vui lòng ấn nhận mã xác nhận");
            return;
        }
        if (email.isEmpty() || newPassword.isEmpty() || code.isEmpty()) {
            shake(forgetPasswordForm);
            messageLabel.setText("Vui lòng nhập đầy đủ thông tin");
            messageLabel.setVisible(true);
            return;
        }

        boolean exists = userRepository.checkEmailExists(email);
        if (!exists) {
            shake(forgetPasswordForm);
            messageLabel.setText("Email không tồn tại");
            messageLabel.setVisible(true);
            return;
        }
        if (!otpCode.equals(code)) {
            shake(forgetPasswordForm);
            messageLabel.setText("Mã xác nhận không hợp lệ!");
            messageLabel.setVisible(true);
            return;
        }
        boolean success = userRepository.updatePasswordUser(email, newPassword);
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
        otpCode = generatedCode;
        String subject = "Mã xác nhận đặt lại mật khẩu";
        String body = "Mã xác nhận của bạn là: " + generatedCode;

        // Gửi email (cần cài đặt thư viện JavaMail)
        try {
            EmailUtil.sendEmail(email, subject, body); // tạo class EmailUtil riêng
            alerts.showSuccessAlert("Đã gửi mã xác nhận qua email");
        } catch (Exception e) {
            e.printStackTrace();
            alerts.showErrorAlert("Không thể gửi mã xác nhận. Vui lòng thử lại!");
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
            shake(forgetPasswordForm);
            messageLabel.setText("Vui lòng nhập email");
            messageLabel.setVisible(true);
            return;
        }

        boolean exists = userRepository.checkEmailExists(email);
        if (!exists) {
            shake(forgetPasswordForm);
            messageLabel.setText("Email không tồn tại");
            messageLabel.setVisible(true);
            return;
        }
        sendVerificationCode(email);

    }
}
