package com.example.coursemanagement.Controllers.Client;

import com.example.coursemanagement.Models.Model;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientController implements Initializable {
//    @FXML
//    private AnchorPane mainContent;
    public BorderPane client_parent;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Model.getInstance().getViewFactory().getClientSelectedMenuItemProperty().addListener((observableValue, oldVal, newVal) -> {
            switch (newVal){
                case "Profile" -> client_parent.setCenter(Model.getInstance().getViewFactory().getProfileView());
                case "MyCourse" -> client_parent.setCenter(Model.getInstance().getViewFactory().getMyCoursesView());
                case "Cart" -> client_parent.setCenter(Model.getInstance().getViewFactory().getCartView());
                case "Payment" -> client_parent.setCenter(Model.getInstance().getViewFactory().getPaymentView());
                default -> client_parent.setCenter(Model.getInstance().getViewFactory().getCoursesView());
            }

        });
    }
//    @FXML
//    private void showPayment() {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/Payment.fxml"));
//            Parent paymentUI = loader.load();
//
//            mainContent.getChildren().clear();
//            mainContent.getChildren().add(paymentUI);
//            AnchorPane.setTopAnchor(paymentUI, 0.0);
//            AnchorPane.setBottomAnchor(paymentUI, 0.0);
//            AnchorPane.setLeftAnchor(paymentUI, 0.0);
//            AnchorPane.setRightAnchor(paymentUI, 0.0);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
