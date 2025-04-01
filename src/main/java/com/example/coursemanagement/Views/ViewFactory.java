package com.example.coursemanagement.Views;

import com.example.coursemanagement.Controllers.Admin.AdminController;
import com.example.coursemanagement.Controllers.Client.ClientController;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ViewFactory {
    private final StringProperty clientSelectedMenuItem;
    private final StringProperty adminSelectedMenuItem;
    private AnchorPane dashboardView;
    private AnchorPane courseManagementView;
    private AnchorPane coursesView;
    private AnchorPane myCourseView;
    private AnchorPane assignmentView;
    private AnchorPane transactionView;
    private AnchorPane accountView;
    private AnchorPane profileView;


    public StringProperty getClientSelectedMenuItemProperty() {
        return clientSelectedMenuItem;
    }

    public StringProperty getAdminSelectedMenuItemProperty() {
        return adminSelectedMenuItem;
    }

    public ViewFactory() {
        this.clientSelectedMenuItem = new SimpleStringProperty("");
        this.adminSelectedMenuItem = new SimpleStringProperty("");
    }

    ;

    public AnchorPane getDashboardView() {
        if (dashboardView == null) {
            try {
                dashboardView = new FXMLLoader(getClass().getResource("/Fxml/Admin/Dashboard.fxml")).load();
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
        return dashboardView;

    }
    public AnchorPane getCourseManagementView() {
        if (courseManagementView == null) {
            try {
                courseManagementView = new FXMLLoader(getClass().getResource("/Fxml/Admin/CourseManagement.fxml")).load();
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
        return courseManagementView;

    }
    public AnchorPane getCoursesView() {
        if (coursesView == null) {
            try {
                coursesView = new FXMLLoader(getClass().getResource("/Fxml/Client/Courses.fxml")).load();
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
        return coursesView;

    }
    public AnchorPane getMyCoursesView() {
        if (myCourseView == null) {
            try {
                myCourseView = new FXMLLoader(getClass().getResource("/Fxml/Client/MyCourse.fxml")).load();
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
        return myCourseView;

    }
    public AnchorPane getAssignmentView() {
        if (assignmentView == null) {
            try {
                assignmentView = new FXMLLoader(getClass().getResource("/Fxml/Client/Assignment.fxml")).load();
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
        return assignmentView;

    }

    public AnchorPane getTransactionView() {
        if (transactionView == null) {
            try {
                transactionView = new FXMLLoader(getClass().getResource("/Fxml/Admin/Transaction.fxml")).load();
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
        return transactionView;

    }

    public AnchorPane getProfileView() {
        if (profileView == null) {
            try {
                profileView = new FXMLLoader(getClass().getResource("/Fxml/Client/Profile.fxml")).load();
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
        return profileView;

    }

    public AnchorPane getAccountView() {
        if (accountView == null) {
            try {
                accountView = new FXMLLoader(getClass().getResource("/Fxml/Admin/Accounts.fxml")).load();
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
        return accountView;

    }

    private void createStage(FXMLLoader loader, String wdName) {
        Scene scene = null;
        try {
            scene = new Scene(loader.load());
        } catch (Exception e) {
            e.printStackTrace();

        }
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle(wdName);
        stage.show();
    }

    public void showLoginWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Login.fxml"));
        createStage(loader, "LOGIN");
    }

    public void showClientWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/Client.fxml"));
        ClientController clientController = new ClientController();
        loader.setController(clientController);
        createStage(loader, "USER");
    }
    public void showAdminWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/Admin.fxml"));
        AdminController adminController = new AdminController();
        loader.setController(adminController);
        createStage(loader, "ADMIN");
    }

    public void closeStage(Stage stage) {
        stage.close();
    }


}
