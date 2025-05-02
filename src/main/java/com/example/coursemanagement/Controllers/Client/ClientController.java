package com.example.coursemanagement.Controllers.Client;

import com.example.coursemanagement.Models.Model;
import com.example.coursemanagement.Utils.SessionManager;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientController implements Initializable {
    public BorderPane client_parent;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Model.getInstance().getViewFactory().setClientRoot((Parent) client_parent);
        if (SessionManager.getInstance().getUser().getRoleId() == 2) {
            client_parent.setCenter(Model.getInstance().getViewFactory().getCourseManagementView());
        } else {
            client_parent.setCenter(Model.getInstance().getViewFactory().getCoursesView());
        }

        Model.getInstance().getViewFactory().getClientSelectedMenuItemProperty().addListener((observableValue, oldVal, newVal) -> {
            switch (newVal) {
                case "Profile": {
                    client_parent.setCenter(Model.getInstance().getViewFactory().getProfileView());
                    break;
                }
                case "MyCourse": {
                    client_parent.setCenter(Model.getInstance().getViewFactory().getMyCoursesView());
                    break;
                }
                case "Cart": {
                    client_parent.setCenter(Model.getInstance().getViewFactory().getCartView());
                    break;
                }
                case "Student": {
                    client_parent.setCenter(Model.getInstance().getViewFactory().getStudentView());
                    break;
                }
                case "Assignment": {
                    client_parent.setCenter(Model.getInstance().getViewFactory().getAssignmentView());
                    break;
                }
                case "InstructorCourse": {
                    FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/Fxml/Admin/CourseManagement.fxml"));
                    Parent view = null;
                    try {
                        view = loader1.load();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    client_parent.setCenter(view);
                    break;
                }
                case "Courses": {
                    client_parent.setCenter(Model.getInstance().getViewFactory().getCoursesView());
                    break;
                }

            }

        });

    }
}
