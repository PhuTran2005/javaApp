package com.example.coursemanagement.Controllers.Client;

import com.example.coursemanagement.Service.CartService;
import com.example.coursemanagement.Service.PaymentService;
import com.example.coursemanagement.Utils.Alerts;
import com.example.coursemanagement.Models.Model;
import com.example.coursemanagement.Utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientMenuController implements Initializable {
    private final PaymentService paymentService = new PaymentService();
    private final CartService cartService = new CartService();


    @FXML
    public Button myCourse_btn;
    @FXML
    public Button courses_btn;
    @FXML

    public Button cart_btn;
    private Alerts alerts = new Alerts();
    @FXML

    public Button logout_btn;
    @FXML

    public Button profile_btn;
    @FXML
    public Button payment_btn;

    @FXML
    private AnchorPane mainContent;
    @FXML

    public Button report_btn;
    private static ClientMenuController instance;

    public ClientMenuController() {
        instance = this; // Lưu instance hiện tại
    }

    public static ClientMenuController getInstance() {
        return instance;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addListeners();
    }

    @FXML
    private void addListeners() {
        courses_btn.setOnAction(event -> onCoursesMenu("Courses"));
        myCourse_btn.setOnAction(event -> onMyCourseMenu("MyCourse"));
        cart_btn.setOnAction(event -> onCart("Cart"));
        profile_btn.setOnAction(event -> onProfileMenu("Profile"));
        payment_btn.setOnAction(event -> onPaymentMenu("Payment"));
        report_btn.setOnAction(event -> onReportMenu("Report"));
        logout_btn.setOnAction(event -> onLogout());
        refreshCartSize();

    }

    public void refreshCartSize() {
        if (cart_btn != null) {
            int cartSize = SessionManager.getInstance().getCartSize(); // Lấy số lượng sản phẩm từ DB
            if (cartSize > 0) {
                cart_btn.setText("Cart (" + cartSize + ")");
            }
            System.out.println(cartSize);
        }
    }

    @FXML
    private void onCoursesMenu(String path) {
        Model.getInstance().getViewFactory().getClientSelectedMenuItemProperty().set(path);
    }

    @FXML
    private void onMyCourseMenu(String path) {
        Model.getInstance().getViewFactory().getClientSelectedMenuItemProperty().set(path);
    }

    @FXML
    private void onCart(String path) {
        Model.getInstance().getViewFactory().getClientSelectedMenuItemProperty().set(path);
    }

    @FXML
    private void onProfileMenu(String path) {
        Model.getInstance().getViewFactory().getClientSelectedMenuItemProperty().set(path);
    }

    @FXML
    private void onPaymentMenu(String path) {
        Parent view = Model.getInstance().getViewFactory().getPaymentView();
        mainContent.getChildren().setAll(view); // mainContent là AnchorPane trong ClientMenuController
    }

    @FXML
    private void onReportMenu(String path) {
        Model.getInstance().getViewFactory().getClientSelectedMenuItemProperty().set(path);
    }

    @FXML
    private void onLogout() {
        if (alerts.showConfirmationWarmingAlert("Bạn có chắc chắn muốn đăng xuất không?")) {
            Stage stage = (Stage) logout_btn.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showLoginWindow();
        }
    }

    private void showPaymentWindow() {
        try {
            URL fxml = getClass().getResource("/Fxml/Client/Payment.fxml");
            System.out.println("Loading Payment.fxml from: " + fxml);
            Parent root = new FXMLLoader(fxml).load();

            Stage stage = new Stage();
            stage.setTitle("Thanh toán");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showPayment() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/Payment.fxml"));
            Parent paymentUI = loader.load();

            PaymentController controller = loader.getController();

            // Lay thong tin so du, ma QR (ví dụ PaymentService sinh ra)
            double balance = paymentService.getBalanceForCurrentUser();
            String base64QR = paymentService.generatePaymentQR();

            // Day du lieu vao PaymentController
            controller.setBalance(balance);
            controller.setQRCode(base64QR);

            mainContent.getChildren().clear();
            mainContent.getChildren().add(paymentUI);
            AnchorPane.setTopAnchor(paymentUI, 0.0);
            AnchorPane.setBottomAnchor(paymentUI, 0.0);
            AnchorPane.setLeftAnchor(paymentUI, 0.0);
            AnchorPane.setRightAnchor(paymentUI, 0.0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showPaymentWindo() {
        try {
            URL fxml = getClass().getResource("/Fxml/Client/Payment.fxml");
            System.out.println("Loading Payment.fxml from: " + fxml);
            Parent root = new FXMLLoader(fxml).load();

            Stage stage = new Stage();
            stage.setTitle("Thanh toán");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showPaymen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/Payment.fxml"));
            Parent paymentUI = loader.load();

            PaymentController controller = loader.getController();

            // Lay thong tin so du, ma QR (ví dụ PaymentService sinh ra)
            double balance = paymentService.getBalanceForCurrentUser();
            String base64QR = paymentService.generatePaymentQR();

            // Day du lieu vao PaymentController
            controller.setBalance(balance);
            controller.setQRCode(base64QR);

            mainContent.getChildren().clear();
            mainContent.getChildren().add(paymentUI);
            AnchorPane.setTopAnchor(paymentUI, 0.0);
            AnchorPane.setBottomAnchor(paymentUI, 0.0);
            AnchorPane.setLeftAnchor(paymentUI, 0.0);
            AnchorPane.setRightAnchor(paymentUI, 0.0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showPaymentWind() {
        try {
            URL fxml = getClass().getResource("/Fxml/Client/Payment.fxml");
            System.out.println("Loading Payment.fxml from: " + fxml);
            Parent root = new FXMLLoader(fxml).load();

            Stage stage = new Stage();
            stage.setTitle("Thanh toán");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showPayme() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/Payment.fxml"));
            Parent paymentUI = loader.load();

            PaymentController controller = loader.getController();

            // Lay thong tin so du, ma QR (ví dụ PaymentService sinh ra)
            double balance = paymentService.getBalanceForCurrentUser();
            String base64QR = paymentService.generatePaymentQR();

            // Day du lieu vao PaymentController
            controller.setBalance(balance);
            controller.setQRCode(base64QR);

            mainContent.getChildren().clear();
            mainContent.getChildren().add(paymentUI);
            AnchorPane.setTopAnchor(paymentUI, 0.0);
            AnchorPane.setBottomAnchor(paymentUI, 0.0);
            AnchorPane.setLeftAnchor(paymentUI, 0.0);
            AnchorPane.setRightAnchor(paymentUI, 0.0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
