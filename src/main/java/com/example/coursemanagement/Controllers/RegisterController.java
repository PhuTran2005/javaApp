package com.example.coursemanagement.Controllers;

import com.example.coursemanagement.Respository.UserRespository;
import com.example.coursemanagement.Utils.Alerts;
import com.example.coursemanagement.Models.Model;
import com.example.coursemanagement.Utils.GlobalVariable;
import com.example.coursemanagement.Utils.ValidatorUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterController {
    private Alerts alerts = new Alerts();
    public Button btnRegister;
    @FXML
    private TextField RGEmail;

    @FXML
    private PasswordField RGPassword;

    @FXML
    private PasswordField RGConfirmPassword;

    @FXML
    private Label messageLabel;

    private final UserRespository userRepository = new UserRespository(); // Tạo repository

    @FXML
    private void handleRegister() {
        String email = RGEmail.getText().trim();
        String password = RGPassword.getText().trim();
        String confirmPassword = RGConfirmPassword.getText().trim();

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showMessage("Vui lòng nhập đầy đủ thông tin!", "RED", 400);
            return;
        }

        if (!ValidatorUtil.isValidEmail(email)) {
            showMessage("Email không hợp lệ!", "RED", 400);
            return;
        }

        if (!ValidatorUtil.isValidPassword(password, GlobalVariable.MIN_PASSWORD_LENGTH)) {
            showMessage("Mật khẩu phải có ít nhất " +  GlobalVariable.MIN_PASSWORD_LENGTH + " ký tự!", "RED", 400);
            return;
        }

        if (!password.equals(confirmPassword)) {
            showMessage("Mật khẩu xác nhận không khớp!", "RED", 400);
            return;
        }
        if (userRepository.isExistEmail(email)) {
            showMessage("Email đã tồn tại!", "RED", 400);
            return;
        }
        if (userRepository.registerUser(email, password)) {
            showMessage("Đăng ký thành công!", "GREEN", 400);
            alerts.showSuccessAlert("Đăng ký thành công!");
            if(alerts.showConfirmationSelectedAlert("Bạn có muốn đăng nhập ngay không")){
                Stage stage = (Stage) messageLabel.getScene().getWindow();
                Model.getInstance().getViewFactory().closeStage(stage);
                Model.getInstance().getViewFactory().showClientWindow();
            }


        } else {
            alerts.showErrorAlert("Đăng ký không thành công!");
        }

    }

    private void showMessage(String message, String color, double width) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: " + color + ";");
        messageLabel.setAlignment(Pos.CENTER);
        messageLabel.setPrefWidth(width);
        messageLabel.setVisible(true);
    }

    @FXML
    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleRegister();
        } else if (event.getCode() == KeyCode.DOWN) {
            if (RGEmail.isFocused()) {
                RGPassword.requestFocus();
            } else if (RGPassword.isFocused()) {
                RGConfirmPassword.requestFocus();
            } else if (RGConfirmPassword.isFocused()) {
                btnRegister.requestFocus();
            }
        } else if (event.getCode() == KeyCode.UP) {
            if (RGPassword.isFocused()) {
                RGEmail.requestFocus();
            } else if (RGConfirmPassword.isFocused()) {
                RGPassword.requestFocus();
            } else if (btnRegister.isFocused()) {
                RGConfirmPassword.requestFocus();
            }
        }
    }

    public void switchToLogin(ActionEvent event) {
        try {
            // Tải giao diện đăng ký từ file FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Login.fxml"));
            Parent root = loader.load();

            // Lấy stage hiện tại và thay đổi scene
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("LOGIN");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
