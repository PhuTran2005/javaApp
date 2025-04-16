package com.example.coursemanagement.Controllers.Admin;

import com.example.coursemanagement.Models.Model;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminController implements Initializable {
    @FXML
    public BorderPane admin_parent;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Model.getInstance().getViewFactory().setAdminRoot((Parent) admin_parent);
        Model.getInstance().getViewFactory().getAdminSelectedMenuItemProperty().addListener((observableValue, oldVal, newVal) -> {
            admin_parent.setCenter(null); // Clear current view
            switch (newVal) {
                case "Accounts":
                    admin_parent.setCenter(Model.getInstance().getViewFactory().getAccountView());
                    break;
                case "CourseManagement":
                    admin_parent.setCenter(Model.getInstance().getViewFactory().getCourseManagementView());
                    break;
                case "Transaction":
                    admin_parent.setCenter(Model.getInstance().getViewFactory().getTransactionView());
                    break;
                case "deleteCourse":
                    admin_parent.setCenter(Model.getInstance().getViewFactory().getDeletedCourseView());
                    break;
                default:
                    // Load the dashboard view using FXMLLoader
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/Dashboard.fxml"));
                    Parent dashboardView = null;
                    try {
                        dashboardView = loader.load(); // Load the view
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // Set the dashboard view into the center of the BorderPane
                    admin_parent.setCenter(dashboardView);

                    // Access the controller and call loadDashboardData() after the view is set
                    if (dashboardView != null) {
                        DashboardController dashboardController = loader.getController();
                        dashboardController.loadDashboardData(); // Call method to load data
                    }
                    break;
            }
        });
    }


}
