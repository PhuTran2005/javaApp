package com.example.coursemanagement.Views;

import com.example.coursemanagement.Controllers.Admin.AdminController;
import com.example.coursemanagement.Controllers.Admin.DashboardController;
import com.example.coursemanagement.Controllers.Client.ClientController;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewFactory {
    private final StringProperty clientSelectedMenuItem;
    private final StringProperty adminSelectedMenuItem;
    private Parent dashboardView;
    private Parent courseManagementView;
    private Parent coursesView;
    private Parent studentView;
    private Parent myCourseView;
    private Parent transactionView;
    private Parent accountView;
    private Parent profileView;
    private Parent deletedCourseView;
    private Parent adminRoot;
    private Parent clientRoot;

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

    public void setAdminRoot(Parent adminRoot) {
        this.adminRoot = adminRoot;
    }

    public Parent getAdminRoot() {
        return this.adminRoot;
    }

    public void setAdminCenterContent(Parent content) {
        ((BorderPane) adminRoot).setCenter(content);
    }
    public void setClientRoot(Parent clientRoot) {
        this.clientRoot = clientRoot;
    }

    public Parent getClientRoot() {
        return this.clientRoot;
    }

    public void setClientCenterContent(Parent content) {
        ((BorderPane) clientRoot).setCenter(content);
    }

    public Parent getDashboardView() {
        if (dashboardView == null) {
            try {
                dashboardView = new FXMLLoader(getClass().getResource("/Fxml/Admin/CourseManagement.fxml")).load();
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
        return dashboardView;
    }


    public Parent getCourseManagementView() {
        if (courseManagementView == null) {
            try {
                courseManagementView = new FXMLLoader(getClass().getResource("/Fxml/Admin/CourseManagement.fxml")).load();
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
        return courseManagementView;

    }

    public Parent getCoursesView() {
        if (coursesView == null) {
            try {
                coursesView = new FXMLLoader(getClass().getResource("/Fxml/Client/Courses.fxml")).load();
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
        return coursesView;

    }

    public Parent getMyCoursesView() {
        if (myCourseView == null) {
            try {
                myCourseView = new FXMLLoader(getClass().getResource("/Fxml/Client/MyCourse.fxml")).load();
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
        return myCourseView;

    }

    public Parent getCartView() {
        try {
            return new FXMLLoader(getClass().getResource("/Fxml/Client/Cart.fxml")).load();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Parent getStudentView() {
        if (studentView == null) {
            try {
                studentView = new FXMLLoader(getClass().getResource("/Fxml/Client/Student.fxml")).load();
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
        return studentView;

    }

    public Parent getAssignmentView() {
        try {
            return new FXMLLoader(getClass().getResource("/Fxml/Client/Assignment/AssignmentMain.fxml")).load();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Parent getTransactionView() {
        if (transactionView == null) {
            try {
                transactionView = new FXMLLoader(getClass().getResource("/Fxml/Admin/Transaction.fxml")).load();
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
        return transactionView;

    }

    public Parent getDeletedCourseView() {
        if (deletedCourseView == null) {
            try {
                deletedCourseView = new FXMLLoader(getClass().getResource("/Fxml/HelpFxml/CourseDeletedManagement.fxml")).load();
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
        return deletedCourseView;

    }

    public Parent getProfileView() {
        if (profileView == null) {
            try {
                profileView = new FXMLLoader(getClass().getResource("/Fxml/Client/Profile.fxml")).load();
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
        return profileView;

    }

    public Parent getAccountView() {
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
        stage.setResizable(false);
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
