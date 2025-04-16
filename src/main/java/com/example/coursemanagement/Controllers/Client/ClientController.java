package com.example.coursemanagement.Controllers.Client;

import com.example.coursemanagement.Models.Model;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientController implements Initializable {
    public BorderPane client_parent;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Model.getInstance().getViewFactory().setClientRoot((Parent) client_parent);

        Model.getInstance().getViewFactory().getClientSelectedMenuItemProperty().addListener((observableValue, oldVal, newVal) -> {
            switch (newVal) {
                case "Profile" -> client_parent.setCenter(Model.getInstance().getViewFactory().getProfileView());
                case "MyCourse" -> client_parent.setCenter(Model.getInstance().getViewFactory().getMyCoursesView());
                case "Cart" -> client_parent.setCenter(Model.getInstance().getViewFactory().getCartView());
                case "Student" -> client_parent.setCenter(Model.getInstance().getViewFactory().getStudentView());
                case "Assignment" -> client_parent.setCenter(Model.getInstance().getViewFactory().getAssignmentView());
                default -> client_parent.setCenter(Model.getInstance().getViewFactory().getCoursesView());
            }

        });

    }
}
