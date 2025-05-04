package com.example.coursemanagement.Views;

import com.example.coursemanagement.Controllers.Admin.AdminController;
import com.example.coursemanagement.Controllers.Client.ClientController;
import com.example.coursemanagement.Controllers.Client.ClientMenuController;
import com.example.coursemanagement.Controllers.Client.PaymentController;
import com.example.coursemanagement.Service.PaymentService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewFactory {
    private final StringProperty clientSelectedMenuItem;
    private final StringProperty adminSelectedMenuItem;

    private AnchorPane dashboardView;
    private AnchorPane courseManagementView;
    private AnchorPane coursesView;
    private AnchorPane myCourseView;
    private AnchorPane transactionView;
    private AnchorPane accountView;
    private AnchorPane profileView;
    private Parent paymentView; // Đã sửa từ AnchorPane thành Parent



    public StringProperty getClientSelectedMenuItemProperty() {
        return clientSelectedMenuItem;
    }

    public StringProperty getAdminSelectedMenuItemProperty() {
        return adminSelectedMenuItem;
    }

    public ViewFactory() {
        this.clientSelectedMenuItem = new SimpleStringProperty("");
        this.adminSelectedMenuItem = new SimpleStringProperty("");
    }

    public AnchorPane getDashboardView() {
        if (dashboardView == null) {
            try {
                dashboardView = new FXMLLoader(getClass().getResource("/Fxml/Admin/Dashboard.fxml")).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dashboardView;
    }

    public AnchorPane getCourseManagementView() {
        if (courseManagementView == null) {
            try {
                courseManagementView = new FXMLLoader(getClass().getResource("/Fxml/Admin/CourseManagement.fxml")).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return courseManagementView;
    }

    public AnchorPane getCoursesView() {
        if (coursesView == null) {
            try {
                coursesView = new FXMLLoader(getClass().getResource("/Fxml/Client/Courses.fxml")).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return coursesView;
    }

    public AnchorPane getMyCoursesView() {
        if (myCourseView == null) {
            try {
                myCourseView = new FXMLLoader(getClass().getResource("/Fxml/Client/MyCourse.fxml")).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return myCourseView;
    }

    public AnchorPane getCartView() {
        try {
            return new FXMLLoader(getClass().getResource("/Fxml/Client/Cart.fxml")).load();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public AnchorPane getTransactionView() {
        if (transactionView == null) {
            try {
                transactionView = new FXMLLoader(getClass().getResource("/Fxml/Admin/Transaction.fxml")).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return transactionView;
    }

    public AnchorPane getProfileView() {
        if (profileView == null) {
            try {
                profileView = new FXMLLoader(getClass().getResource("/Fxml/Client/Profile.fxml")).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return profileView;
    }

    public AnchorPane getAccountView() {
        if (accountView == null) {
            try {
                accountView = new FXMLLoader(getClass().getResource("/Fxml/Admin/Accounts.fxml")).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return accountView;
    }

    public Parent getPaymentView() {
        if (paymentView == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/Payment.fxml"));
                paymentView = loader.load();

                // Gọi controller để truyền dữ liệu
                PaymentController controller = loader.getController();
                controller.setBalance(new PaymentService().getBalanceForCurrentUser());
                controller.setQRCode(new PaymentService().generatePaymentQR());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return paymentView;
    }

    private void createStage(FXMLLoader loader, String wdName) {
        try {
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle(wdName);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showLoginWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Login.fxml"));
        createStage(loader, "LOGIN");
    }

    public void showClientWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/Client.fxml"));
        ClientController clientController = new ClientController();
        createStage(loader, "USER");
    }

    public void showAdminWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/Admin.fxml"));
        AdminController adminController = new AdminController();
//        loader.setController(adminController);
        createStage(loader, "ADMIN");
    }

    public void closeStage(Stage stage) {
        stage.close();
    }
}
