package com.example.coursemanagement.Controllers.Client.CartController;

import com.example.coursemanagement.Controllers.Admin.CourseController.CourseBoxController;
import com.example.coursemanagement.Controllers.Client.ClientMenuController;
import com.example.coursemanagement.Controllers.PaymentDetailController;
import com.example.coursemanagement.Dto.CourseDetailDTO;
import com.example.coursemanagement.Models.Cart;
import com.example.coursemanagement.Service.CartService;
import com.example.coursemanagement.Service.CourseService;
import com.example.coursemanagement.Utils.Alerts;
import com.example.coursemanagement.Utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class CartController implements Initializable {
    private final CourseService courseService = new CourseService(); // Tạo repository
    private final CartService cartService = new CartService(); // Tạo repository
    private final Alerts alerts = new Alerts(); // Tạo repository

    @FXML
    public FlowPane courseContainer;
    @FXML
    public Text cartTotal;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println(SessionManager.getInstance().getUser());
        refreshCourseList();

    }

    //Load data
    public void loadCartsList() {
        if (courseContainer != null) {
            List<Cart> carts = cartService.getAllCartOfUser(SessionManager.getInstance().getUser().getUserId());
            for (Cart cart : carts) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/HelpFxml/CartItem.fxml"));
                    AnchorPane cartItem = loader.load();
                    CartItemController controller = loader.getController();
                    controller.setCartController(this);
                    controller.setData(cart);
                    courseContainer.getChildren().add(cartItem);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (cartTotal != null) {
            cartTotal.setText(cartService.getTotalCart(SessionManager.getInstance().getUser().getUserId()) + " VNĐ");
        }
    }

    public void refreshCourseList() {
        if (courseContainer != null) {
            courseContainer.getChildren().clear(); // Xóa hết các course đang hiển thị
            loadCartsList();
        }        // Tải lại từ CSDL
    }

    @FXML

    public void handleClear() {
        if (alerts.showConfirmationSelectedAlert("Bạn có muốn xóa tất cả khóa học này ra khỏi Cart không")) {
            cartService.deleteAllCartItem(SessionManager.getInstance().getUser().getUserId());
            SessionManager.getInstance().setCartSize();
            ClientMenuController.getInstance().refreshCartSize();
            refreshCourseList();
        }
    }

    @FXML

    public void handlePay() {
        System.out.println("payment");
        List<Cart> carts = cartService.getAllCartOfUser(SessionManager.getInstance().getUser().getUserId());
        List<CourseDetailDTO> courses = new ArrayList<>();
        for (Cart i : carts
        ) {
            courses.add(courseService.getCourseById(i.getCourseId()));
        }
        if (courses.isEmpty()) {
            alerts.showErrorAlert("Cart hiện không có sản phẩm");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/HelpFxml/PaymentDetail.fxml"));
            Parent root = loader.load();

            PaymentDetailController paymentDetailController = loader.getController();
            paymentDetailController.setCart(true);
            paymentDetailController.setTotalPrice((int) cartService.getTotalCart(SessionManager.getInstance().getUser().getUserId()));
            paymentDetailController.setList(courses);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Thanh toán");
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
        refreshCourseList();
    }
}
