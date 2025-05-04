package com.example.coursemanagement.Controllers.Client;

import com.example.coursemanagement.Models.User;
import com.example.coursemanagement.Repository.UserRepository;
import com.example.coursemanagement.Service.UserService;
import com.example.coursemanagement.Utils.Alerts;
import com.example.coursemanagement.Utils.GlobalVariable;
import com.example.coursemanagement.Utils.SessionManager;
import com.example.coursemanagement.Utils.ValidatorUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class ProfileController {
    @FXML
    public TextField currPasswordField;
    @FXML
    public TextField newPasswordField;
    @FXML
    public TextField confirmNewPasswordField;
    @FXML
    public Button cancelButton;
    @FXML
    private TextField userEmailField, usernameField, userPhoneNumberField, createDateField;
    @FXML
    private Button saveButton;


    private final Alerts alerts = new Alerts();
    private final UserService userService = new UserService();
    private User myProfile;
    private Consumer<User> sessionListener; // Biến lưu Listener để có thể xóa
    private final UserRepository userRepository = new UserRepository(); // Tạo repository

    @FXML
    public void initialize() {
        updateCurrentProfile(); // Gọi lần đầu khi giao diện mở

        // Lắng nghe khi user thay đổi
        sessionListener = this::onUserChanged;
        SessionManager.getInstance().addListener(sessionListener);
    }

    // Khi tài khoản thay đổi, cập nhật giao diện
    private void onUserChanged(User newUser) {
        if (newUser == null) return;
        System.out.println("User changed to: " + newUser.getUserEmail()); // Debug kiểm tra

        myProfile = newUser;
        loadUserData();
    }

    public void updateCurrentProfile() {
        myProfile = SessionManager.getInstance().getUser();
        if (myProfile != null) {
            loadUserData();
        }
    }

    private void loadUserData() {
        if (usernameField == null || userPhoneNumberField == null || userEmailField == null || createDateField == null) {
            return; // Đợi FXML load xong mới cập nhật
        }

        if (myProfile == null) return;
        System.out.println("Load" + myProfile);
        User userFromDB = userRepository.getUserByEmail(myProfile.getUserEmail());
        if (userFromDB != null) {
            usernameField.setText((userFromDB.getUsername() == null || userFromDB.getUsername().trim().isEmpty()) ? "Chưa xác định" : userFromDB.getUsername());
            userPhoneNumberField.setText((userFromDB.getUserPhoneNumber() == null || userFromDB.getUserPhoneNumber().trim().isEmpty()) ? "Chưa xác định" : userFromDB.getUserPhoneNumber());
            userEmailField.setText(userFromDB.getUserEmail());
            createDateField.setText(userFromDB.getCreateDate().substring(0, 10));
        }
    }

    @FXML
    private void handleSave() {
        if (!ValidatorUtil.isValidPhoneNumber(userPhoneNumberField.getText())) {
            alerts.showErrorAlert("Số điện thoại không hợp lệ!");
            return;
        }

        myProfile.setUserPhoneNumber(userPhoneNumberField.getText());
        myProfile.setUsername(usernameField.getText());

        if (userService.modifierUser(myProfile)) {
            alerts.showSuccessAlert("Profile updated successfully!");
            loadUserData();
        } else {
            alerts.showErrorAlert("Failed to update profile.");
        }
    }

    // Phương thức này sẽ được gọi khi Controller bị hủy
    public void cleanup() {
        SessionManager.getInstance().removeListener(sessionListener);
    }

    @FXML

    public void handleCancel() {
        if (cancelButton != null) {
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();
        }
    }

    @FXML

    public void handleChange() {
        if (currPasswordField.getText().isEmpty() || newPasswordField.getText().isEmpty() || confirmNewPasswordField.getText().isEmpty()) {
            alerts.showErrorAlert("Vui lòng nhập đầy đủ thông tin!.");
            return;
        }

        if (!userService.isValidPassword(myProfile.getUserEmail(), currPasswordField.getText())) {
            alerts.showErrorAlert("Mật khẩu hiện tại không chính xác.");
            return;
        }

        if (!ValidatorUtil.isValidPassword(newPasswordField.getText(), GlobalVariable.MIN_PASSWORD_LENGTH)) {
            alerts.showErrorAlert("Mật khẩu phải có ít nhất " + GlobalVariable.MIN_PASSWORD_LENGTH + " ký tự!");
            return;
        }
        if (!newPasswordField.getText().equals(confirmNewPasswordField.getText())) {
            alerts.showErrorAlert("Mật khẩu xác nhận không khớp!");
            return;
        }
        if (userRepository.updatePasswordUser(myProfile.getUserEmail(), newPasswordField.getText())) {

            alerts.showSuccessAlert("Thay đổi mật khẩu thành công");
            handleCancel();
        } else {
            alerts.showErrorAlert("Thay đổi mật khẩu không thành công");
        }

    }

    @FXML
    public void handleChangePassword() {

        try {
            // Tải FXML cho cửa sổ Thêm Khóa Học
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/HelpFxml/ChangePassword.fxml"));
            Parent root = loader.load();



            // Tạo và hiển thị cửa sổ modal
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Đổi mật khẩu");
            stage.setResizable(false);

            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
