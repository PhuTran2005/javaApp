package com.example.coursemanagement.Controllers.Client;

import com.example.coursemanagement.Service.CartService;
import com.example.coursemanagement.Utils.Alerts;
import com.example.coursemanagement.Models.Model;
import com.example.coursemanagement.Utils.SessionManager;
import com.example.coursemanagement.Utils.UIHelper;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
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
    @FXML
    public ImageView avartarView;
    @FXML

    public Button instructorCourse_btn;
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

        Image avatarImage = UIHelper.generateAvatar(UIHelper.getLastWord(SessionManager.getInstance().getUser().getFullname()), 99);
        if (avartarView != null) {
            System.out.println("Load anh");
            avartarView.setImage(avatarImage);
            avartarView.setFitWidth(100);
            avartarView.setFitHeight(100);
            avartarView.setPreserveRatio(false); // giữ đúng tỉ lệ 120x120

            Circle clip = new Circle();
            clip.centerXProperty().bind(avartarView.fitWidthProperty().divide(2));
            clip.centerYProperty().bind(avartarView.fitHeightProperty().divide(2));
            clip.radiusProperty().bind(Bindings.min(
                    avartarView.fitWidthProperty(),
                    avartarView.fitHeightProperty()
            ).divide(2));

            avartarView.setClip(clip);

        }
        if (SessionManager.getInstance().getUser().getRoleId() == 2) {
            if (welcomeText != null) {
                welcomeText.setText("Giáo viên");
            }
            loadInstructorUi();
        } else {
            if (welcomeText != null) {
                welcomeText.setText("Học viên");
            }
            loadStudentUi();
        }
    }

    //Thêm action cho các nút
    @FXML
    private void addListeners() {

        instructorCourse_btn.setOnAction(event -> onInstructorCourse("InstructorCourse"));
        courses_btn.setOnAction(event -> onCoursesMenu("Courses"));
        myCourse_btn.setOnAction(event -> onMyCourseMenu("MyCourse"));
        cart_btn.setOnAction(event -> onCartMenu("Cart"));
        profile_btn.setOnAction(event -> onProfileMenu("Profile"));
        student_btn.setOnAction(event -> onStudentMenu("Student"));
        assignment_btn.setOnAction(event -> onAssignmentMenu("Assignment"));
        report_btn.setOnAction(event -> onReportMenu("Report"));
        logout_btn.setOnAction(event -> onLogout());
        if (SessionManager.getInstance().getUser().getRoleId() == 2) {
            if (instructorCourse_btn != null) {
                if (!instructorCourse_btn.getStyleClass().contains("active")) {
                    instructorCourse_btn.getStyleClass().add("active");
                }
            }
            onInstructorCourse("InstructorCourse");
        } else {
            if (courses_btn != null) {
                if (!courses_btn.getStyleClass().contains("active")) {
                    courses_btn.getStyleClass().add("active");
                }
            }
            onCoursesMenu("Courses");
        }
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
            assignment_btn.setVisible(false);
            assignment_btn.setManaged(false);
        }
        if (student_btn != null) {
            student_btn.setVisible(show);
            student_btn.setManaged(show);
        }
        if (instructorCourse_btn != null) {
            instructorCourse_btn.setVisible(show);
            instructorCourse_btn.setManaged(show);
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
        if (courses_btn != null) {
            courses_btn.setVisible(show);
            courses_btn.setManaged(show);
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
    private void onInstructorCourse(String path) {
        setActiveButton(instructorCourse_btn);
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
        Button[] buttons = {courses_btn, myCourse_btn, cart_btn, profile_btn, report_btn, assignment_btn, student_btn, instructorCourse_btn};
        for (Button btn : buttons) {
            btn.getStyleClass().remove("active");
        }
        if (!activeButton.getStyleClass().contains("active")) {
            activeButton.getStyleClass().add("active");
        }
    }
}
