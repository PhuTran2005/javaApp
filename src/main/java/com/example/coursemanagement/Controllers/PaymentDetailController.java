package com.example.coursemanagement.Controllers;

import com.example.coursemanagement.Dto.CourseDetailDTO;
import com.example.coursemanagement.Service.PaymentService;
import com.example.coursemanagement.Service.PurchaseCourseService;
import com.example.coursemanagement.Utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;


import java.awt.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PaymentDetailController implements Initializable {
    @FXML
    public Label total;
    private List<CourseDetailDTO> courses;
    private boolean isCart = false;

    public List<CourseDetailDTO> getList() {
        return courses;
    }

    public void setList(List<CourseDetailDTO> courses) {
        this.courses = courses;
        renderCourses(); // thêm dòng này
    }

    public double getTotalPrice() {
        return TotalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        TotalPrice = totalPrice;
    }

    private double TotalPrice=0;
    // Tạo hàm riêng để hiển thị
    private void renderCourses() {
        if(total != null){
            total.setText(getTotalPrice()+" VNĐ");
        }
        if (courses != null && courseListContainer != null) {
            courseListContainer.getChildren().clear(); // Xóa cái cũ nếu có
            for (CourseDetailDTO course : courses) {

                HBox courseItem = createCourseItem(course);
                courseListContainer.getChildren().add(courseItem);
            }
        }
    }

    @FXML
    private VBox courseListContainer;  // Contain các khóa học

    // Hàm giả lập lấy khóa học từ cơ sở dữ liệu


    // Hàm hiển thị danh sách các khóa học

    // Tạo một HBox cho mỗi khóa học, bao gồm tên và giá
    private HBox createCourseItem(CourseDetailDTO course) {
        ImageView courseThumbnail = new ImageView();
        courseThumbnail.setFitHeight(60);
        courseThumbnail.setFitWidth(80);
        String thumbnailPath = "/" + course.getCourse().getCourseThumbnail();
        try {
            javafx.scene.image.Image image = new javafx.scene.image.Image(getClass().getResource(thumbnailPath).toExternalForm());
            courseThumbnail.setImage(image);
        } catch (Exception e) {
            System.err.println("Không tìm thấy ảnh: " + thumbnailPath + ", dùng ảnh mặc định.");
            try {
                javafx.scene.image.Image defaultImage = new Image(getClass().getResource("/Images/logo.png").toExternalForm());
                courseThumbnail.setImage(defaultImage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        courseThumbnail.getStyleClass().add("course-image");

        Label nameLabel = new Label(course.getCourse().getCourseName());
        nameLabel.getStyleClass().add("course-name");

        Label priceLabel = new Label(course.getCourse().getCoursePrice() + " VND");
        priceLabel.getStyleClass().add("course-price");

        Label quantityLabel = new Label("x1");
        quantityLabel.getStyleClass().add("quantity-label");

        VBox infoBox = new VBox(5, nameLabel, priceLabel, quantityLabel);

        HBox courseItem = new HBox(15, courseThumbnail, infoBox);
        courseItem.getStyleClass().add("course-item");

        return courseItem;
    }


    public boolean isCart() {
        return isCart;
    }

    public void setCart(boolean cart) {
        isCart = cart;
    }

    // Xử lý sự kiện khi người dùng ấn nút Mua khóa học
    @FXML
    private void handlePurchase() {
        System.out.println("Đang mua khóa học: ");
        PaymentService.processPaymentForCourse(SessionManager.getInstance().getUser().getUserId(),(int) getTotalPrice(),getList(),isCart);
        handleCancel();
        // Bạn có thể thêm logic thanh toán ở đây (hoặc gọi phương thức thanh toán)
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println(courses);
        if (courses != null) {
            // Lấy danh sách khóa học
            for (CourseDetailDTO course : courses) {
                // Tạo mỗi khóa học một HBox chứa thông tin
                System.out.println("dg tao");
                HBox courseItem = createCourseItem(course);
                courseListContainer.getChildren().add(courseItem);  // Thêm vào container
            }
        }
    }

    @FXML
    public void handleCancel() {
        Stage stage = (Stage) courseListContainer.getScene().getWindow();
        stage.close();
    }
}


