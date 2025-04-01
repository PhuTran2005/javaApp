package com.example.coursemanagement.Controllers.Admin;

import com.example.coursemanagement.Helper.Alerts;
import com.example.coursemanagement.Models.Model;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminMenuController implements Initializable {
    @FXML
    public Button dashboard_btn;
    @FXML
    public Button courseManagement_btn;
    @FXML
    public Button transaction_btn;
    private Alerts alerts = new Alerts();
    @FXML

    public Button logout_btn;
    @FXML

    public Button accounts_btn;

    @FXML

    public Button report_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println(dashboard_btn);
        addListeners();
    }

    @FXML
    private void addListeners() {
        dashboard_btn.setOnAction(event -> onSelectedMenu("Dashboard"));
        transaction_btn.setOnAction(event -> onSelectedMenu("Transaction"));
        accounts_btn.setOnAction(event -> onSelectedMenu("Accounts"));
        courseManagement_btn.setOnAction(event -> onSelectedMenu("CourseManagement"));
        report_btn.setOnAction(event -> onSelectedMenu("Report"));
        logout_btn.setOnAction(event -> onLogout());
    }


    @FXML
    private void onSelectedMenu(String path) {
        System.out.println(path);
        Model.getInstance().getViewFactory().getAdminSelectedMenuItemProperty().set(path);
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
