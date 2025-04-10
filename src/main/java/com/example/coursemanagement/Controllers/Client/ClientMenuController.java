package com.example.coursemanagement.Controllers.Client;

import com.example.coursemanagement.Service.CartService;
import com.example.coursemanagement.Utils.Alerts;
import com.example.coursemanagement.Models.Model;
import com.example.coursemanagement.Utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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
    }

    @FXML
    private void addListeners() {
        courses_btn.setOnAction(event -> onCoursesMenu("Courses"));
        myCourse_btn.setOnAction(event -> onMyCourseMenu("MyCourse"));
        cart_btn.setOnAction(event -> onCart("Cart"));
        profile_btn.setOnAction(event -> onProfileMenu("Profile"));
        report_btn.setOnAction(event -> onReportMenu("Report"));
        logout_btn.setOnAction(event -> onLogout());
        refreshCartSize();

    }

    public void refreshCartSize() {
        if (cart_btn != null) {
            int cartSize = SessionManager.getInstance().getCartSize(); // Lấy số lượng sản phẩm từ DB
            if (cartSize > 0) {
                cart_btn.setText("Cart (" + cartSize + ")");
            }
            System.out.println(cartSize);
        }
    }

    @FXML
    private void onCoursesMenu(String path) {
        Model.getInstance().getViewFactory().getClientSelectedMenuItemProperty().set(path);
    }

    @FXML
    private void onMyCourseMenu(String path) {
        Model.getInstance().getViewFactory().getClientSelectedMenuItemProperty().set(path);
    }

    @FXML
    private void onCart(String path) {
        Model.getInstance().getViewFactory().getClientSelectedMenuItemProperty().set(path);
    }

    @FXML
    private void onProfileMenu(String path) {
        Model.getInstance().getViewFactory().getClientSelectedMenuItemProperty().set(path);

    }

    @FXML
    private void onReportMenu(String path) {
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


}
