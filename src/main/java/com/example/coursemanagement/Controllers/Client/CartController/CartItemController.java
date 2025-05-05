package com.example.coursemanagement.Controllers.Client.CartController;


import com.example.coursemanagement.Controllers.Client.ClientMenuController;
import com.example.coursemanagement.Dto.CourseDetailDTO;
import com.example.coursemanagement.Models.Cart;
import com.example.coursemanagement.Service.CartService;
import com.example.coursemanagement.Service.CourseService;
import com.example.coursemanagement.Utils.Alerts;
import com.example.coursemanagement.Utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class CartItemController {
    @FXML
    public ImageView cartImg;
    @FXML

    public Label cartName;
    @FXML

    public Label cartInstructor;
    @FXML

    public Label cartPrice;
    private Cart cart;
    private CourseDetailDTO course;
    private final Alerts alerts = new Alerts(); // Tạo repository
    private final CourseService courseService = new CourseService(); // Tạo repository
    private final CartService cartService = new CartService(); // Tạo repository
    private CartController cartController;

    public void setCartController(CartController cartController) {
        this.cartController = cartController;
    }

    //load data
    public void setData(Cart cart) {
        this.cart = cart;
        this.course = courseService.getCourseById(this.cart.getCourseId());
        cartName.setText(course.getCourse().getCourseName());
        cartInstructor.setText("GV: " + course.getInstructor().getFullname());
        cartPrice.setText("Giá: " + course.getCourse().getCoursePrice() + " VND");

        // Load ảnh
        String thumbnailPath = "/" + course.getCourse().getCourseThumbnail();
        try {
            Image image = new Image(getClass().getResource(thumbnailPath).toExternalForm());
            cartImg.setImage(image);
        } catch (Exception e) {
            System.err.println("Không tìm thấy ảnh: " + thumbnailPath + ", dùng ảnh mặc định.");
            try {
                Image defaultImage = new Image(getClass().getResource("/Images/logo.png").toExternalForm());
                cartImg.setImage(defaultImage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    @FXML
    public void handleDelete() {
        if(alerts.showConfirmationSelectedAlert("Bạn có muốn xóa khóa học này ra khỏi Cart không")){
            cartService.deleteCartItem(cart.getCartId());
            SessionManager.getInstance().setCartSize();
            ClientMenuController.getInstance().refreshCartSize();
            cartController.refreshCourseList();
        }
    }
}
