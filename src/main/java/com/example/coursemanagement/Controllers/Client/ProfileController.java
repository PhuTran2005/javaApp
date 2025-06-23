package com.example.coursemanagement.Controllers.Client;

import com.example.coursemanagement.Models.Instructor;
import com.example.coursemanagement.Models.Student;
import com.example.coursemanagement.Models.User;
import com.example.coursemanagement.Repository.InstructorRepository;
import com.example.coursemanagement.Repository.StudentRepository;
import com.example.coursemanagement.Repository.UserRepository;
import com.example.coursemanagement.Service.LogService;
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
import javafx.scene.layout.HBox;
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
    public TextField GpaField;
    @FXML
    public TextField enrollmentYearField;
    @FXML
    public TextField classField;
    @FXML
    public TextField yearsExperimentField;
    @FXML
    public TextField degreeField;
    @FXML
    public TextField specialtyField;
    @FXML
    public HBox studentBox3;
    @FXML
    public HBox studentBox2;

    @FXML
    public HBox studentBox1;
    @FXML
    public HBox instructorBox3;
    @FXML
    public HBox instructorBox2;
    @FXML
    public HBox instructorBox1;
    @FXML
    private TextField userEmailField, usernameField, userPhoneNumberField, createDateField;
    @FXML
    private Button saveButton;

    private final Alerts alerts = new Alerts();
    private final UserService userService = new UserService();
    private User myProfile;
    private Consumer<User> sessionListener; // Biến lưu Listener để có thể xóa
    private final UserRepository userRepository = new UserRepository(); // Tạo repository
    private final InstructorRepository instructorRepository = new InstructorRepository(); // Tạo repository
    private final StudentRepository studentRepository = new StudentRepository(); // Tạo repository
    private static LogService logService = new LogService();

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
        loadUserData(myProfile.getRoleId());
    }

    public void updateCurrentProfile() {
        myProfile = SessionManager.getInstance().getUser();
        if (myProfile != null) {
            loadUserData(myProfile.getRoleId());
        }
    }

    //hiện profile chi tiết instructor
    public void loadInstructorUi() {
        toggleInstructorBoxes(true);
        toggleStudentBoxes(false);
    }

    public void loadStudentUi() {
        toggleInstructorBoxes(false);
        toggleStudentBoxes(true);
    }

    private void toggleInstructorBoxes(boolean show) {
        if (instructorBox1 != null) {
            instructorBox1.setVisible(show);
            instructorBox1.setManaged(show);
        }
        if (instructorBox2 != null) {
            instructorBox2.setVisible(show);
            instructorBox2.setManaged(show);
        }
        if (instructorBox3 != null) {
            instructorBox3.setVisible(show);
            instructorBox3.setManaged(show);
        }
    }

    private void toggleStudentBoxes(boolean show) {
        if (studentBox1 != null) {
            studentBox1.setVisible(show);
            studentBox1.setManaged(show);
        }
        if (studentBox2 != null) {
            studentBox2.setVisible(show);
            studentBox2.setManaged(show);
        }
        if (studentBox3 != null) {
            studentBox3.setVisible(show);
            studentBox3.setManaged(show);
        }
    }

    //hiện profile chi tiết Student
    private void loadUserData(int roleId) {
        if (usernameField == null || userPhoneNumberField == null || userEmailField == null) return;
        if (myProfile == null) return;
        System.out.println("Load" + myProfile);

        if (roleId == 2) {
            loadInstructorUi();
            Instructor instructor = instructorRepository.getInstructorByEmail(myProfile.getUserEmail());
            if (instructor != null) {
                usernameField.setText((instructor.getFullname() == null || instructor.getFullname().trim().isEmpty()) ? "Chưa xác định" : instructor.getFullname());
                specialtyField.setText((instructor.getExpertise() == null || instructor.getExpertise().trim().isEmpty()) ? "Chưa xác định" : instructor.getExpertise());
                degreeField.setText((instructor.getDegree() == null || instructor.getDegree().trim().isEmpty()) ? "Chưa xác định" : instructor.getDegree());
                yearsExperimentField.setText((instructor.getYears_of_experience() < 0) ? "Chưa xác định" : instructor.getYears_of_experience() + "");
                userPhoneNumberField.setText((instructor.getUserPhoneNumber() == null || instructor.getUserPhoneNumber().trim().isEmpty()) ? "Chưa xác định" : instructor.getUserPhoneNumber());
                userEmailField.setText(instructor.getUserEmail());
                createDateField.setText(instructor.getCreateDate().substring(0, 10));

            }
        } else {
            loadStudentUi();
            Student student = studentRepository.getStudentByEmail(myProfile.getUserEmail());
            if (student != null) {
                usernameField.setText((student.getFullname() == null || student.getFullname().trim().isEmpty()) ? "Chưa xác định" : student.getFullname());
                classField.setText((student.getClasses() == null || student.getClasses().trim().isEmpty()) ? "Chưa xác định" : student.getClasses());
                enrollmentYearField.setText((student.getEnrollment_year() < 0) ? "Chưa xác định" : student.getEnrollment_year() + "");
                GpaField.setText((student.getGpa() < 0) ? "Chưa xác định" : student.getGpa() + "");
                userPhoneNumberField.setText((student.getUserPhoneNumber() == null || student.getUserPhoneNumber().trim().isEmpty()) ? "Chưa xác định" : student.getUserPhoneNumber());
                userEmailField.setText(student.getUserEmail());
                createDateField.setText(student.getCreateDate().substring(0, 10));
            }
        }
    }

    @FXML
    private void handleSave() {
        if (!ValidatorUtil.isValidPhoneNumber(userPhoneNumberField.getText())) {
            alerts.showErrorAlert("Số điện thoại không hợp lệ!");
            return;
        }

        int roleId = myProfile.getRoleId();
        myProfile.setUserPhoneNumber(userPhoneNumberField.getText());
        myProfile.setFullname(usernameField.getText());

        boolean isUpdated = false;

        if (roleId == 2) { // Instructor
            Instructor instructor = instructorRepository.getInstructorByEmail(myProfile.getUserEmail());
            if (instructor != null) {
                instructor.setFullname(usernameField.getText());
                instructor.setUserPhoneNumber(userPhoneNumberField.getText());
                instructor.setExpertise(specialtyField.getText());
                instructor.setDegree(degreeField.getText());
                try {
                    instructor.setYears_of_experience(Integer.parseInt(yearsExperimentField.getText()));
                } catch (NumberFormatException e) {
                    alerts.showErrorAlert("Năm kinh nghiệm phải nhập số nguyên");
                }

                isUpdated = instructorRepository.updateInforInstructor(instructor);
            }
        } else { // Student
            Student student = studentRepository.getStudentByEmail(myProfile.getUserEmail());
            if (student != null) {
                student.setFullname(usernameField.getText());
                student.setUserPhoneNumber(userPhoneNumberField.getText());
                student.setClasses(classField.getText());
                try {
                    student.setEnrollment_year(Integer.parseInt(enrollmentYearField.getText()));
                } catch (NumberFormatException e) {
                    alerts.showErrorAlert("Năm tốt nghiệp phải nhập số nguyên");
                }

                try {
                    student.setGpa(Float.parseFloat(GpaField.getText()));
                } catch (NumberFormatException e) {
                    alerts.showErrorAlert("Gpa phải nhập số");
                }
                isUpdated = studentRepository.updateInforStudent(student);
            }
        }

        if (isUpdated) {
            alerts.showSuccessAlert("Cập nhật thành công!");
            logService.createLog(SessionManager.getInstance().getUser().getUserId(), "Cập nhật thông tin cá nhân");
            loadUserData(roleId);
        } else {
            alerts.showErrorAlert("Cập nhật thất bại!");
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
            logService.createLog(SessionManager.getInstance().getUser().getUserId(), "Đã thay đổi mật khẩu");
            handleCancel();
        } else {
            alerts.showErrorAlert("Thay đổi mật khẩu không thành công");
        }

    }

    @FXML
    public void handleChangePassword() {
        System.out.println("open");
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
