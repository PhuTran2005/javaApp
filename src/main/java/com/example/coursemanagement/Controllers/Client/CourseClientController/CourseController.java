package com.example.coursemanagement.Controllers.Client.CourseClientController;

import com.example.coursemanagement.Dto.CourseDetailDTO;
import com.example.coursemanagement.Models.PageResult;
import com.example.coursemanagement.Service.CourseService;
import com.example.coursemanagement.Utils.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class CourseController {

    private final CourseService courseService = new CourseService();
    private Alerts alerts = new Alerts();
    private Timeline searchDelay;

    // Các biến liên quan đến phân trang
    private int currentPage = 1;
    private int pageSize = 4; // Số lượng khóa học trên một trang
    private int totalPages = 1;
    private String currentSearchQuery = "";
    private Integer currentCategoryFilter = null;

    @FXML
    public ScrollPane listCourse;
    @FXML
    public FlowPane courseContainer;
    @FXML
    public TextField searchField;
    @FXML
    public HBox paginationContainer; // Container chứa các nút phân trang

    // Khởi tạo
    public void initialize() {
        loadPaginatedCourses(1);
        setupSearchFieldListener();
        setupPaginationControls();
    }

    // Thiết lập thanh tìm kiếm với delay
    private void setupSearchFieldListener() {
        searchDelay = new Timeline(new KeyFrame(Duration.millis(300), e -> handleSearch()));
        searchDelay.setCycleCount(1);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (searchDelay != null) {
                searchDelay.stop();
            }
            searchDelay.playFromStart();
        });
    }

    // Thiết lập điều khiển phân trang
    private void setupPaginationControls() {
        updatePaginationControls();
    }

    // Cập nhật hiển thị các nút phân trang
    private void updatePaginationControls() {
        if (paginationContainer != null) {
            paginationContainer.getChildren().clear();
            paginationContainer.setAlignment(Pos.CENTER);
            paginationContainer.setSpacing(5);
            paginationContainer.setPadding(new Insets(10, 0, 10, 0));

            // Nút First
            Button firstButton = createPageButton("<<", 1);
            firstButton.setDisable(currentPage == 1);

            // Nút Previous
            Button prevButton = createPageButton("<", currentPage - 1);
            prevButton.setDisable(currentPage == 1);

            paginationContainer.getChildren().addAll(firstButton, prevButton);

            // Các nút số trang
            int startPage = Math.max(1, currentPage - 2);
            int endPage = Math.min(totalPages, startPage + 4);
            if (endPage - startPage < 4) {
                startPage = Math.max(1, endPage - 4);
            }

            for (int i = startPage; i <= endPage; i++) {
                Button pageButton = createPageButton(String.valueOf(i), i);
                if (i == currentPage) {
                    pageButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                }
                paginationContainer.getChildren().add(pageButton);
            }

            // Nút Next
            Button nextButton = createPageButton(">", currentPage + 1);
            nextButton.setDisable(currentPage == totalPages);

            // Nút Last
            Button lastButton = createPageButton(">>", totalPages);
            lastButton.setDisable(currentPage == totalPages);

            paginationContainer.getChildren().addAll(nextButton, lastButton);

            // Hiển thị thông tin trang
            Label pageInfoLabel = new Label("Trang " + currentPage + " / " + totalPages);
            pageInfoLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
            pageInfoLabel.setPadding(new Insets(0, 0, 0, 10));

            paginationContainer.getChildren().add(pageInfoLabel);
        }
    }

    // Tạo nút cho phân trang
    private Button createPageButton(String text, int pageNumber) {
        Button button = new Button(text);
        button.setCursor(Cursor.HAND);

        // Styling cho nút
        button.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #333333;");
        button.setPrefWidth(30);
        button.setPrefHeight(30);

        // Hiệu ứng hover
        button.setOnMouseEntered(e -> {
            if (currentPage != pageNumber) {
                button.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333333;");
            }
        });

        button.setOnMouseExited(e -> {
            if (currentPage != pageNumber) {
                button.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #333333;");
            }
        });

        // Hiệu ứng khi click
        button.setOnAction(e -> {
            goToPage(pageNumber);
        });

        // Hiệu ứng bóng đổ
        DropShadow shadow = new DropShadow();
        shadow.setRadius(2.0);
        shadow.setColor(Color.GRAY);
        button.setEffect(shadow);

        return button;
    }

    // Chuyển đến trang được chỉ định
    public void goToPage(int pageNumber) {
        if (pageNumber < 1 || pageNumber > totalPages) {
            return;
        }

        currentPage = pageNumber;

        if (currentSearchQuery != null && !currentSearchQuery.isEmpty()) {
            loadPaginatedCoursesBySearch(currentSearchQuery, currentPage);
        } else if (currentCategoryFilter != null) {
            loadPaginatedCoursesByCategory(currentCategoryFilter, currentPage);
        } else {
            loadPaginatedCourses(currentPage);
        }
    }

    // Tải danh sách khóa học có phân trang
    public void loadPaginatedCourses(int pageNumber) {
        if (courseContainer != null) {
            refreshCourseList();

            PageResult<CourseDetailDTO> pageResult = courseService.getPaginatedCourseList(pageNumber, pageSize);
            List<CourseDetailDTO> courses = pageResult.getItems();

            if (courses.isEmpty()) {
                alerts.showErrorAlert("Danh sách rỗng!");
                return;
            } else if (courses.isEmpty()) {
                // Nếu trang này không có dữ liệu, quay lại trang trước
                goToPage(pageNumber - 1);
                return;
            }

            // Cập nhật thông tin phân trang
            currentPage = pageNumber;
            totalPages = pageResult.getTotalPages();
            currentSearchQuery = "";
            currentCategoryFilter = null;

            // Hiển thị danh sách khóa học
            displayCourses(courses);

            // Cập nhật điều khiển phân trang
            updatePaginationControls();
        }
    }

    // Tải danh sách khóa học theo từ khóa tìm kiếm có phân trang
    public void loadPaginatedCoursesBySearch(String query, int pageNumber) {
        if (courseContainer != null) {
            refreshCourseList();

            PageResult<CourseDetailDTO> pageResult = courseService.searchPaginatedCourses(query, pageNumber, pageSize);
            List<CourseDetailDTO> courses = pageResult.getItems();

            if (courses.isEmpty() && pageNumber == 1) {
                return;
            } else if (courses.isEmpty()) {
                goToPage(pageNumber - 1);
                return;
            }

            // Cập nhật thông tin phân trang
            currentPage = pageNumber;
            totalPages = pageResult.getTotalPages();
            currentSearchQuery = query;
            currentCategoryFilter = null;

            // Hiển thị danh sách khóa học
            displayCourses(courses);

            // Cập nhật điều khiển phân trang
            updatePaginationControls();
        }
    }

    // Tải danh sách khóa học theo danh mục có phân trang
    public void loadPaginatedCoursesByCategory(int categoryId, int pageNumber) {
        if (courseContainer != null) {
            refreshCourseList();

            PageResult<CourseDetailDTO> pageResult = courseService.filterPaginatedCoursesByCategory(categoryId, pageNumber, pageSize);
            List<CourseDetailDTO> courses = pageResult.getItems();

            if (courses.isEmpty() && pageNumber == 1) {
                alerts.showErrorAlert("Không có khóa học trong danh mục này!");
                return;
            } else if (courses.isEmpty()) {
                goToPage(pageNumber - 1);
                return;
            }

            // Cập nhật thông tin phân trang
            currentPage = pageNumber;
            totalPages = pageResult.getTotalPages();
            currentSearchQuery = "";
            currentCategoryFilter = categoryId;

            // Hiển thị danh sách khóa học
            displayCourses(courses);

            // Cập nhật điều khiển phân trang
            updatePaginationControls();
        }
    }

    // Hiển thị danh sách khóa học
    private void displayCourses(List<CourseDetailDTO> courses) {
        for (CourseDetailDTO course : courses) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/HelpFxml/CourseBoxExtra.fxml"));
                AnchorPane courseBox = loader.load();
                CourseBoxExtraController controller = loader.getController();
                controller.setCourseController(this);
                controller.setData(course);
                courseContainer.getChildren().add(courseBox);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Tương thích với code cũ
    public void loadCoursesList() {
        loadPaginatedCourses(1);
    }

    // Tương thích với code cũ
    public void loadCoursesListBySearch(String query) {
        loadPaginatedCoursesBySearch(query, 1);
    }

    // Tương thích với code cũ
    public void loadCoursesListByFilter(List<CourseDetailDTO> courses) {
        if (courses.isEmpty()) {
            alerts.showErrorAlert("Danh sách rỗng!");
            return;
        }

        refreshCourseList();
        displayCourses(courses);

        // Reset thông tin phân trang vì đây là trường hợp đặc biệt
        currentPage = 1;
        totalPages = 1;
        currentSearchQuery = "";
        currentCategoryFilter = null;
        updatePaginationControls();
    }

    // Xử lý khi lọc khóa học
    public void applyFilterByCategory(int categoryId) {
        loadPaginatedCoursesByCategory(categoryId, 1);
    }

    // Làm mới danh sách khóa học
    public void refreshCourseList() {
        if (courseContainer != null) {
            courseContainer.getChildren().clear();
        }
    }

    // Xử lý khi nhấn nút lọc
    @FXML
    public void handleFillter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/HelpFxml/FilterCourse.fxml"));
            Parent root = loader.load();

            FilterCourseController filterCourseController = loader.getController();
            filterCourseController.setCourseController(this);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Lọc Khóa Học");
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Sau khi đóng popup, kiểm tra nếu đã thêm
            if (filterCourseController.isCourseAdded()) {
                refreshCourseList();
                loadPaginatedCourses(1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Xử lý khi tìm kiếm
    @FXML
    public void handleSearch() {
        String query = searchField.getText().trim();
        if (!query.isEmpty()) {
            loadPaginatedCoursesBySearch(query, 1);
        } else {
            loadPaginatedCourses(1);
        }
    }

    // Xử lý khi xóa tìm kiếm
    @FXML
    public void handleClear() {
        searchField.setText("");
        loadPaginatedCourses(currentPage);
    }
}