package com.example.coursemanagement.Controllers.Client;

import com.example.coursemanagement.Models.User;
import com.example.coursemanagement.Respository.UserRespository;
import com.example.coursemanagement.Service.UserService;
import com.example.coursemanagement.Utils.Alerts;
import com.example.coursemanagement.Utils.SessionManager;
import com.example.coursemanagement.Utils.ValidatorUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.util.function.Consumer;

public class ProfileController {
    @FXML
    private TextField userEmailField, usernameField, userPhoneNumberField, createDateField;
    @FXML
    private Button saveButton;

    private final Alerts alerts = new Alerts();
    private final UserService userService = new UserService();
    private User myProfile;
    private Consumer<User> sessionListener; // Biến lưu Listener để có thể xóa

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
        User userFromDB = UserRespository.getUserByEmail(myProfile.getUserEmail());
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
}
