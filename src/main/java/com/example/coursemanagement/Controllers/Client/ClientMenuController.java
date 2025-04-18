package com.example.coursemanagement.Controllers.Client;

import com.example.coursemanagement.Service.CartService;
import com.example.coursemanagement.Utils.Alerts;
import com.example.coursemanagement.Models.Model;
import com.example.coursemanagement.Utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientMenuController implements Initializable {
    private final CartService cartService = new CartService();
    @FXML
    public Button myCourse_btn;
    @FXML
    public Button courses_btn;
    @FXML
    public Button cart_btn;
    @FXML
    public Button student_btn;
    @FXML
    public Text welcomeText;
    @FXML
    public Button assignment_btn;
    private Alerts alerts = new Alerts();
    @FXML
    public Button logout_btn;
    @FXML
    public Button profile_btn;
    @FXML
    public Button report_btn;

    private static ClientMenuController instance;

    public ClientMenuController() {
        instance = this; // Lưu instance hiện tại
    }

    public static ClientMenuController getInstance() {
        return instance;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addListeners();
        if (SessionManager.getInstance().getUser().getRoleId() == 2) {
            if (welcomeText != null) {
                welcomeText.setText("Giáo viên: " + SessionManager.getInstance().getUser().getFullname());
            }
            loadInstructorUi();
        } else {
            if (welcomeText != null) {
                welcomeText.setText("Học viên: " + SessionManager.getInstance().getUser().getFullname());
            }
            loadStudentUi();
        }
    }

    //Thêm action cho các nút
    @FXML
    private void addListeners() {

        courses_btn.setOnAction(event -> onCoursesMenu("Courses"));
        myCourse_btn.setOnAction(event -> onMyCourseMenu("MyCourse"));
        cart_btn.setOnAction(event -> onCartMenu("Cart"));
        profile_btn.setOnAction(event -> onProfileMenu("Profile"));
        student_btn.setOnAction(event -> onStudentMenu("Student"));
        assignment_btn.setOnAction(event -> onAssignmentMenu("Assignment"));
        report_btn.setOnAction(event -> onReportMenu("Report"));
        logout_btn.setOnAction(event -> onLogout());
        if (courses_btn != null) {
            if (!courses_btn.getStyleClass().contains("active")) {
                courses_btn.getStyleClass().add("active");
            }
        }
        onCoursesMenu("Courses");
        refreshCartSize();
    }

    //load giao diện cho giáo viên
    public void loadInstructorUi() {
        toggleInstructorBoxes(true);
        toggleStudentBoxes(false);
    }

    public void loadStudentUi() {
        toggleInstructorBoxes(false);
        toggleStudentBoxes(true);
    }

    //handle on/off button
    private void toggleInstructorBoxes(boolean show) {
        if (assignment_btn != null) {
            assignment_btn.setVisible(show);
            assignment_btn.setManaged(show);
        }
        if (student_btn != null) {
            student_btn.setVisible(show);
            student_btn.setManaged(show);
        }

    }

    private void toggleStudentBoxes(boolean show) {
        if (myCourse_btn != null) {
            myCourse_btn.setVisible(show);
            myCourse_btn.setManaged(show);
        }
        if (cart_btn != null) {
            cart_btn.setVisible(show);
            cart_btn.setManaged(show);
        }

    }

    //load lại giỏ hàng
    public void refreshCartSize() {
        if (cart_btn != null) {
            int cartSize = SessionManager.getInstance().getCartSize();
            if (cartSize > 0) {
                cart_btn.setText("Cart (" + cartSize + ")");
            } else {
                cart_btn.setText("Cart");
            }
        }
    }

    @FXML
    private void onCoursesMenu(String path) {
        setActiveButton(courses_btn);
        Model.getInstance().getViewFactory().getClientSelectedMenuItemProperty().set(path);
    }

    @FXML
    private void onMyCourseMenu(String path) {
        setActiveButton(myCourse_btn);
        Model.getInstance().getViewFactory().getClientSelectedMenuItemProperty().set(path);
    }

    @FXML
    private void onStudentMenu(String path) {
        setActiveButton(student_btn);
        Model.getInstance().getViewFactory().getClientSelectedMenuItemProperty().set(path);
    }

    @FXML
    private void onAssignmentMenu(String path) {
        setActiveButton(assignment_btn);
        Model.getInstance().getViewFactory().getClientSelectedMenuItemProperty().set(path);
    }

    @FXML
    private void onCartMenu(String path) {
        setActiveButton(cart_btn);
        Model.getInstance().getViewFactory().getClientSelectedMenuItemProperty().set(path);
    }

    @FXML
    private void onProfileMenu(String path) {
        setActiveButton(profile_btn);
        Model.getInstance().getViewFactory().getClientSelectedMenuItemProperty().set(path);
    }

    @FXML
    private void onReportMenu(String path) {
        setActiveButton(report_btn);
        Model.getInstance().getViewFactory().getClientSelectedMenuItemProperty().set(path);
    }

    @FXML
    private void onLogout() {
        if (alerts.showConfirmationWarmingAlert("Bạn có chắc chắn muốn đăng xuất không?")) {
            Stage stage = (Stage) logout_btn.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showLoginWindow();
        }
    }

    private void setActiveButton(Button activeButton) {
        Button[] buttons = {courses_btn, myCourse_btn, cart_btn, profile_btn, report_btn, assignment_btn, student_btn};
        for (Button btn : buttons) {
            btn.getStyleClass().remove("active");
        }
        if (!activeButton.getStyleClass().contains("active")) {
            activeButton.getStyleClass().add("active");
        }
    }
}
