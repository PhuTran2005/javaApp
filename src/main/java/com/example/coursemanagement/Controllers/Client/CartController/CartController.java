package com.example.coursemanagement.Controllers.Client.CartController;

import com.example.coursemanagement.Utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class CartController implements Initializable {
    public void handleClear(ActionEvent event) {
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println(SessionManager.getInstance().getUser());

    }
}
