package com.example.coursemanagement.Controllers.Admin;

import com.example.coursemanagement.Utils.Alerts;
import com.example.coursemanagement.Models.Model;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
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
//<<<<<<< HEAD
        dashboard_btn.setOnAction(event -> onDashboardMenu("Dashboard"));
        transaction_btn.setOnAction(event -> onTransactionMenu("Transaction"));
        accounts_btn.setOnAction(event -> onAccountsMenu("Accounts"));
        courseManagement_btn.setOnAction(event -> onCourseManagementMenu("CourseManagement"));
//=======
//        dashboard_btn.setOnAction(event -> onDashboardButtonClicked());
//        transaction_btn.setOnAction(event -> onSelectedMenu("Transaction"));
//        accounts_btn.setOnAction(event -> onAccountsButtonClicked());
//        courseManagement_btn.setOnAction(event -> onSelectedMenu("CourseManagement"));
//>>>>>>> b5cc3d00a08209e1f5655296b14a2de710cb1b40
        report_btn.setOnAction(event -> onSelectedMenu("Report"));
        logout_btn.setOnAction(event -> onLogout());
    }



    @FXML
    private void onAccountsButtonClicked() {
        System.out.println("Accounts button clicked");  // Debug để kiểm tra
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/Accounts.fxml"));
            BorderPane borderPane = (BorderPane) accounts_btn.getScene().getRoot();  // Lấy BorderPane chứa cả menu và nội dung

            borderPane.setCenter(loader.load());

            AccountsController accountsController = loader.getController();
            accountsController.loadStudentData();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onSelectedMenu(String path) {
        System.out.println(path);
        Model.getInstance().getViewFactory().getAdminSelectedMenuItemProperty().set(path);
    }
    @FXML
    private void onDashboardMenu(String path) {
        System.out.println(path);
        Model.getInstance().getViewFactory().getAdminSelectedMenuItemProperty().set(path);
    }
    @FXML
    private void onTransactionMenu(String path) {
        System.out.println(path);
        Model.getInstance().getViewFactory().getAdminSelectedMenuItemProperty().set(path);
    }
    @FXML
    private void onAccountsMenu(String path) {
        System.out.println(path);
        Model.getInstance().getViewFactory().getAdminSelectedMenuItemProperty().set(path);
    }
    @FXML
    private void onCourseManagementMenu(String path) {
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
