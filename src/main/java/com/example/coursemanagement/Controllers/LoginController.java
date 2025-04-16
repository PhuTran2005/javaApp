package com.example.coursemanagement.Controllers;

import com.example.coursemanagement.Repository.UserRepository;
import com.example.coursemanagement.Service.CartService;
import com.example.coursemanagement.Utils.Alerts;
import com.example.coursemanagement.Models.Model;
import com.example.coursemanagement.Models.User;
import com.example.coursemanagement.Utils.SessionManager;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.ScaleTransition;
import javafx.scene.control.Button;
import javafx.util.Duration;
import javafx.animation.RotateTransition;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class LoginController implements Initializable {
    @FXML
    public TextField LGEmailname;
    public ImageView logo13;
    public AnchorPane loginForm;
    private Alerts alerts = new Alerts();

    @FXML

    public PasswordField LGPassword;

    @FXML
    private Button btnLogin;
    @FXML
    private Label messageLabel;
    private final UserRepository userRepository = new UserRepository(); // Tạo repository
    private final CartService cartService = new CartService(); // Tạo repository

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        pulseButton(btnLogin);
    }

    // Cho ImageView logo xoay khi vừa load
    public void shake(Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(50), node);
        tt.setFromX(0);
        tt.setByX(10);
        tt.setCycleCount(6);
        tt.setAutoReverse(true);
        tt.play();
    }


    private void showMessage(String message, String color, double width) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: " + color + ";");
        messageLabel.setAlignment(Pos.CENTER);
        messageLabel.setPrefWidth(width);
        messageLabel.setVisible(true);
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
    private void handleLogin() {
        String email = LGEmailname.getText().trim();
        String password = LGPassword.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            shake(loginForm);
            showMessage("Vui lòng nhập đầy đủ thông tin!", "RED", 400);
            return;
        }
        User response = userRepository.loginUser(email, password);
        loginProcess(response);
    }

    public void loginProcess(User response) {
        if (response != null) {
            showMessage("Đăng nhập thành công!", "GREEN", 400);
            SessionManager.getInstance().setUser(response); // Cập nhật user mới
            if (response.getRoleId() == 3) {
                SessionManager.getInstance().setCartSize();
            }
            alerts.showSuccessAlert("Đăng nhập thành công!");
            Stage stage = (Stage) messageLabel.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            openDashboard(response.getRoleId());
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
            if (LGEmailname.isFocused()) {
                LGPassword.requestFocus();
            } else if (LGPassword.isFocused()) {
                btnLogin.requestFocus();
            }
        } else if (event.getCode() == KeyCode.UP) {
            if (LGPassword.isFocused()) {
                LGEmailname.requestFocus();
            } else if (btnLogin.isFocused()) {
                LGPassword.requestFocus();
            }
        }
    }


    public void openDashboard(int roleId) {
        try {
            if (roleId == 1) {
                Model.getInstance().getViewFactory().showAdminWindow();
            } else {
                Model.getInstance().getViewFactory().showClientWindow();
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
