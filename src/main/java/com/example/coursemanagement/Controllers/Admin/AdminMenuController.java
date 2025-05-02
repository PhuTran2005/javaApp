package com.example.coursemanagement.Controllers.Admin;

import com.example.coursemanagement.Controllers.Admin.CourseController.CourseManagementController;
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
        addListeners();
    }

    @FXML
    private void addListeners() {
        dashboard_btn.setOnAction(event -> onDashboardButtonClicked());
        transaction_btn.setOnAction(event -> onTransactionMenu("Transaction"));
        accounts_btn.setOnAction(event -> onAccountsButtonClicked());
        courseManagement_btn.setOnAction(event -> onCourseButtonClicker());
        report_btn.setOnAction(event -> onSelectedMenu("Report"));
        logout_btn.setOnAction(event -> onLogout());
        if (dashboard_btn != null) {
            if (!dashboard_btn.getStyleClass().contains("active")) {
                dashboard_btn.getStyleClass().add("active");
            }
        }
    }

    @FXML
    private void onDashboardButtonClicked (){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/Dashboard.fxml"));
            BorderPane borderPane = (BorderPane) dashboard_btn.getScene().getRoot();
            borderPane.setCenter(loader.load());

            DashboardController dashboardController =loader.getController();
            dashboardController.loadDashboardData();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onCourseButtonClicker(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/CourseManagement.fxml"));
            BorderPane borderPane = (BorderPane) courseManagement_btn.getScene().getRoot();
            borderPane.setCenter(loader.load());

            CourseManagementController courseManagementController = loader.getController();
            courseManagementController.refreshCourseList();
        }catch (IOException e){
            e.printStackTrace();
        }
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
        Model.getInstance().getViewFactory().getAdminSelectedMenuItemProperty().set(path);
    }

    @FXML
    private void onDashboardMenu(String path) {
        setActiveButton(dashboard_btn);

        Model.getInstance().getViewFactory().getAdminSelectedMenuItemProperty().set(path);
    }

    @FXML
    private void onTransactionMenu(String path) {
        setActiveButton(transaction_btn);

        Model.getInstance().getViewFactory().getAdminSelectedMenuItemProperty().set(path);
    }

    @FXML
    private void onAccountsMenu(String path) {
        setActiveButton(accounts_btn);

        Model.getInstance().getViewFactory().getAdminSelectedMenuItemProperty().set(path);
    }

    @FXML
    private void onCourseManagementMenu(String path) {
        Model.getInstance().getViewFactory().getAdminSelectedMenuItemProperty().set(path);
        setActiveButton(courseManagement_btn);
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
        Button[] buttons = {dashboard_btn, courseManagement_btn, transaction_btn, accounts_btn, report_btn};
        for (Button btn : buttons) {
            btn.getStyleClass().remove("active");
        }
        if (!activeButton.getStyleClass().contains("active")) {
            activeButton.getStyleClass().add("active");
        }
    }
}
