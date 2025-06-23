package com.example.coursemanagement.Controllers;

import com.example.coursemanagement.Models.Category;
import com.example.coursemanagement.Models.Role;
import com.example.coursemanagement.Models.User;
import com.example.coursemanagement.Repository.UserRepository;
import com.example.coursemanagement.Utils.Alerts;
import com.example.coursemanagement.Models.Model;
import com.example.coursemanagement.Utils.GlobalVariable;
import com.example.coursemanagement.Utils.SessionManager;
import com.example.coursemanagement.Utils.ValidatorUtil;
import javafx.animation.ScaleTransition;
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
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    public AnchorPane registerForm;
    @FXML
    public ChoiceBox<Role> roleField;
    @FXML
    public TextField RGName;
    @FXML
    private Alerts alerts = new Alerts();
    @FXML
    public Button btnRegister;
    @FXML
    private TextField RGEmail;

    @FXML
    private PasswordField RGPassword;

    @FXML
    private PasswordField RGConfirmPassword;

    @FXML
    private Label messageLabel;
    private List<Role> roles = new ArrayList<>(
            List.of(
                    new Role(3, "Học viên"),
                    new Role(2, "Giáo viên")
            )
    );


    private final UserRepository userRepository = new UserRepository(); // Tạo repository
    private final LoginController loginController = new LoginController(); // Tạo repository

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initRole();
        pulseButton(btnRegister);
    }

    public void initRole() {
        roleField.getItems().addAll(roles);
        roleField.setConverter(new StringConverter<>() {

            @Override
            public String toString(Role c) {
                return c != null ? c.getRoleName() : "";
            }

            @Override
            public Role fromString(String string) {
                return roles.stream().filter(c -> c.getRoleName().equals(string)).findFirst().orElse(null);
            }
        });
        if (!roles.isEmpty()) roleField.setValue(roles.get(0));
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
    private void handleRegister() {
        String email = RGEmail.getText().trim();
        String password = RGPassword.getText().trim();
        String confirmPassword = RGConfirmPassword.getText().trim();
        String fullname = RGName.getText().trim();


        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || fullname.isEmpty()) {
            shake(registerForm);
            showMessage("Vui lòng nhập đầy đủ thông tin!", "RED", 400);
            return;
        }
        if (!ValidatorUtil.validateFullName(fullname)) {
            shake(registerForm);
            showMessage("Họ và tên không hợp lệ!", "RED", 400);
            return;
        }

        if (!ValidatorUtil.isValidEmail(email)) {
            shake(registerForm);

            showMessage("Email không hợp lệ!", "RED", 400);
            return;
        }


        if (!ValidatorUtil.isValidPassword(password, GlobalVariable.MIN_PASSWORD_LENGTH)) {
            shake(registerForm);

            showMessage("Mật khẩu phải có ít nhất " + GlobalVariable.MIN_PASSWORD_LENGTH + " ký tự!", "RED", 400);
            return;
        }


        if (!password.equals(confirmPassword)) {
            shake(registerForm);

            showMessage("Mật khẩu xác nhận không khớp!", "RED", 400);
            return;
        }
        if (userRepository.isExistEmail(email)) {
            shake(registerForm);

            showMessage("Email đã tồn tại!", "RED", 400);
            return;
        }
        if (userRepository.registerUser(email, password, roleField.getValue().getRoleId(), fullname)) {
            showMessage("Đăng ký thành công!", "GREEN", 400);
            alerts.showSuccessAlert("Đăng ký thành công!");
            if (alerts.showConfirmationSelectedAlert("Chuyển đến trang đăng nhập ngay?")) {

                Stage stage = (Stage) messageLabel.getScene().getWindow();
                Model.getInstance().getViewFactory().closeStage(stage);
                Model.getInstance().getViewFactory().showLoginWindow();

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
            if (RGName.isFocused()) {
                RGEmail.requestFocus();
            } else if (RGEmail.isFocused()) {
                RGPassword.requestFocus();
            } else if (RGPassword.isFocused()) {
                RGConfirmPassword.requestFocus();
            } else if (RGConfirmPassword.isFocused()) {
                btnRegister.requestFocus();
            }
        } else if (event.getCode() == KeyCode.UP) {
            if (RGEmail.isFocused()) {
                RGName.requestFocus();
            } else if (RGPassword.isFocused()) {
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
