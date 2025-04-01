package com.example.coursemanagement.Controllers.Admin;

import com.example.coursemanagement.Models.Model;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminController implements Initializable {
    public BorderPane admin_parent;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Model.getInstance().getViewFactory().getAdminSelectedMenuItemProperty().addListener((observableValue, oldVal, newVal) -> {
            switch (newVal){
                case "Accounts" -> admin_parent.setCenter(Model.getInstance().getViewFactory().getAccountView());
                case "CourseManagement" -> admin_parent.setCenter(Model.getInstance().getViewFactory().getCourseManagementView());
                case "Transaction" -> admin_parent.setCenter(Model.getInstance().getViewFactory().getTransactionView());
                default -> admin_parent.setCenter(Model.getInstance().getViewFactory().getDashboardView());
            }

        });

    }
}
