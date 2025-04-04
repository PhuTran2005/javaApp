package com.example.coursemanagement.Controllers;

import com.example.coursemanagement.Controllers.Client.ProfileController;
import com.example.coursemanagement.Respository.UserRespository;
import com.example.coursemanagement.Utils.Alerts;
import com.example.coursemanagement.Models.Model;
import com.example.coursemanagement.Models.User;
import com.example.coursemanagement.Utils.SessionManager;
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


public class LoginController {
    private Alerts alerts = new Alerts();
    @FXML

    public TextField LGUsername;
    @FXML

    public PasswordField LGPassword;

    @FXML
    private Button btnLogin;
    @FXML
    private Label messageLabel;

    private void showMessage(String message, String color, double width) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: " + color + ";");
        messageLabel.setAlignment(Pos.CENTER);
        messageLabel.setPrefWidth(width);
        messageLabel.setVisible(true);
    }

    @FXML
    private void handleLogin() {
        String username = LGUsername.getText().trim();
        String password = LGPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showMessage("Vui lòng nhập đầy đủ thông tin!", "RED", 400);
            return;
        }
        User response = UserRespository.loginUser(username, password);
        loginProcess(response);
    }

    public void loginProcess(User response) {
        if (response != null) {
            showMessage("Đăng nhập thành công!", "GREEN", 400);
            SessionManager.getInstance().setUser(response); // Cập nhật user mới
            alerts.showSuccessAlert("Đăng nhập thành công!");
            Stage stage = (Stage) messageLabel.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            openDashboard(response.getRole());
        } else {
            showMessage("Sai tên đăng nhập hoặc mật khẩu!", "RED", 400);
        }

    }

    // Xử lý phím Enter (⏎), Lên (↑), Xuống (↓)
    @FXML
    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleLogin();
        } else if (event.getCode() == KeyCode.DOWN) {
            if (LGUsername.isFocused()) {
                LGPassword.requestFocus();
            } else if (LGPassword.isFocused()) {
                btnLogin.requestFocus();
            }
        } else if (event.getCode() == KeyCode.UP) {
            if (LGPassword.isFocused()) {
                LGUsername.requestFocus();
            } else if (btnLogin.isFocused()) {
                LGPassword.requestFocus();
            }
        }
    }


    public void openDashboard(String role) {
        try {
            if (role.equals("USER")) {
                Model.getInstance().getViewFactory().showClientWindow();
            } else {
                Model.getInstance().getViewFactory().showAdminWindow();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void switchToRegister(ActionEvent event) {
        try {
            // Tải giao diện đăng ký từ file FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Register.fxml"));
            Parent root = loader.load();

            // Lấy stage hiện tại và thay đổi scene
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("REGISTER");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
