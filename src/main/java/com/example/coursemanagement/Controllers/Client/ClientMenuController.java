package com.example.coursemanagement.Controllers.Client;

import com.example.coursemanagement.Helper.Alerts;
import com.example.coursemanagement.Models.Model;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientMenuController implements Initializable {
    @FXML
    public Button assignment_btn;
    @FXML
    public Button myCourse_btn;
    @FXML
    public Button courses_btn;
    private Alerts alerts = new Alerts();
    @FXML

    public Button logout_btn;
    @FXML

    public Button profile_btn;

    @FXML

    public Button report_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println(courses_btn);
        addListeners();
    }

    @FXML
    private void addListeners() {
        courses_btn.setOnAction(event -> onSelectedMenu("Courses"));
        myCourse_btn.setOnAction(event -> onSelectedMenu("MyCourse"));
        assignment_btn.setOnAction(event -> onSelectedMenu("Assignment"));
        profile_btn.setOnAction(event -> onSelectedMenu("Profile"));
        report_btn.setOnAction(event -> onSelectedMenu("Report"));
        logout_btn.setOnAction(event -> onLogout());
    }


    @FXML
    private void onSelectedMenu(String path) {
        System.out.println(path);
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
