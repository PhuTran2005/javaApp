package com.example.coursemanagement.Controllers.Admin.CourseController;

import com.example.coursemanagement.Controllers.Component.LoadingComponentController;
import com.example.coursemanagement.Dto.CourseDetailDTO;
import com.example.coursemanagement.Models.Model;
import com.example.coursemanagement.Service.CourseService;
import com.example.coursemanagement.Utils.Alerts;
import com.example.coursemanagement.Utils.SessionManager;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CourseManagementController {
    @FXML private AnchorPane Box; // Container chính
    private LoadingComponentController loading;
    private final CourseService courseService = new CourseService();
    private final Alerts alerts = new Alerts();

    @FXML public ScrollPane listCourse;
    @FXML public FlowPane courseContainer;
    @FXML public Button recycle_btn;


    public void initialize() {
        setupLoadingComponent();
        refreshCourseList();
    }

    private void setupLoadingComponent() {
        loading = new LoadingComponentController();
        // Thêm loading component vào container chính
        LoadingComponentController.addToPane(Box, loading);

        // Cấu hình theme (tùy chọn)
        loading.applyTheme("default"); // "default", "dark", hoặc "glass"
        loading.setSpinnerColor("#18CFC4"); // Màu spinner
    }

    public void loadCoursesList() {
        if (courseContainer != null) {
            List<CourseDetailDTO> courses = new ArrayList<>();
            if (SessionManager.getInstance().getUser().getRoleId() == 2) {
                courses = courseService.getCourseListByInstructorId(
                        SessionManager.getInstance().getUser().getUserId()
                );
            } else {
                courses = courseService.getCourseList(0);
            }

            for (CourseDetailDTO course : courses) {
                try {
                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource("/Fxml/HelpFxml/CourseBox.fxml")
                    );
                    AnchorPane courseBox = loader.load();
                    CourseBoxController controller = loader.getController();
                    controller.setCourseManagementController(this);
                    controller.setIsDelete(false);
                    controller.setData(course);
                    courseContainer.getChildren().add(courseBox);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void refreshCourseList() {
        // Hiển thị loading với message tùy chỉnh
        loading.show("Đang tải danh sách khóa học...");

        // Tạo background task để load data
        Task<List<CourseDetailDTO>> loadTask = new Task<List<CourseDetailDTO>>() {
            @Override
            protected List<CourseDetailDTO> call() throws Exception {
                // Simulate loading time (có thể remove trong production)
                Thread.sleep(1000);

                // Update progress (tùy chọn)
                updateProgress(0.3, 1.0);
                loading.setProgress(0.3);
                loading.setLoadingMessage("Đang kết nối cơ sở dữ liệu...");

                Thread.sleep(500);
                updateProgress(0.6, 1.0);
                loading.setProgress(0.6);
                loading.setLoadingMessage("Đang tải dữ liệu khóa học...");

                // Load actual data
                List<CourseDetailDTO> courses = new ArrayList<>();
                if (SessionManager.getInstance().getUser().getRoleId() == 2) {
                    courses = courseService.getCourseListByInstructorId(
                            SessionManager.getInstance().getUser().getUserId()
                    );
                } else {
                    courses = courseService.getCourseList(0);
                }

                Thread.sleep(500);
                updateProgress(1.0, 1.0);
                loading.setProgress(1.0);
                loading.setLoadingMessage("Hoàn thành!");

                return courses;
            }

            @Override
            protected void succeeded() {
                List<CourseDetailDTO> courses = getValue();

                // Clear existing courses
                if (courseContainer != null) {
                    courseContainer.getChildren().clear();
                }

                // Add loaded courses to UI
                for (CourseDetailDTO course : courses) {
                    try {
                        FXMLLoader loader = new FXMLLoader(
                                getClass().getResource("/Fxml/HelpFxml/CourseBox.fxml")
                        );
                        AnchorPane courseBox = loader.load();
                        CourseBoxController controller = loader.getController();
                        controller.setCourseManagementController(CourseManagementController.this);
                        controller.setIsDelete(false);
                        controller.setData(course);
                        courseContainer.getChildren().add(courseBox);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // Ẩn loading với một chút delay để user thấy "Hoàn thành!"
                Task<Void> hideTask = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        Thread.sleep(300);
                        return null;
                    }

                    @Override
                    protected void succeeded() {
                        loading.hide();
                    }
                };
                new Thread(hideTask).start();
            }

            @Override
            protected void failed() {
                loading.hide();
                alerts.showErrorAlert("Không thể tải danh sách khóa học: " + getException().getMessage());
            }
        };

        // Chạy task trong background thread
        new Thread(loadTask).start();
    }

    public void handleAddCourse() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/Fxml/HelpFxml/AddCourse.fxml")
            );
            Parent root = loader.load();

            AddCourseController addCourseController = loader.getController();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Thêm Khóa Học");
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Sau khi đóng popup, kiểm tra nếu đã thêm
            if (addCourseController.isCourseAdded()) {
                refreshCourseList();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleRecycleCourse() throws IOException {
        if (SessionManager.getInstance().getUser().getRoleId() == 2) {
            alerts.showErrorAlert("Chức năng không khả thi");
            return;
        }

        // Hiển thị loading khi chuyển trang
        loading.show("Đang chuyển đến trang khôi phục...");

        Task<Void> navigateTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(500); // Simulate loading
                return null;
            }

            @Override
            protected void succeeded() {
                try {
                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource("/Fxml/HelpFxml/CourseDeletedManagement.fxml")
                    );
                    Parent view = loader.load();
                    Model.getInstance().getViewFactory().setAdminCenterContent(view);
                    loading.hide();
                } catch (IOException e) {
                    loading.hide();
                    alerts.showErrorAlert("Không thể tải trang khôi phục: " + e.getMessage());
                }
            }

            @Override
            protected void failed() {
                loading.hide();
                alerts.showErrorAlert("Lỗi khi chuyển trang: " + getException().getMessage());
            }
        };

        new Thread(navigateTask).start();
    }

    // Utility methods để control loading từ bên ngoài
    public void showLoading(String message) {
        if (loading != null) {
            loading.show(message);
        }
    }

    public void hideLoading() {
        if (loading != null) {
            loading.hide();
        }
    }

    public void setLoadingProgress(double progress, String message) {
        if (loading != null) {
            loading.setProgress(progress);
            loading.setLoadingMessage(message);
        }
    }
}