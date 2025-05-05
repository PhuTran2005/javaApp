package com.example.coursemanagement.Controllers.Admin;

import com.example.coursemanagement.Service.StatisticsService;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private PieChart pieChart;
    @FXML
    private BarChart<String, Number> barChart;
    @FXML
    private LineChart<String, Number> lineChart;
    @FXML
    private Label totalRevenueLabel;

    // Cập nhật tổng doanh thu từ StatisticsService
    private void updateTotalRevenue() {
        Task<Double> task = new Task<>() {
            @Override
            protected Double call() throws Exception {
                return StatisticsService.getTotalRevenue();
            }
        };

        task.setOnSucceeded(event -> {
            double totalRevenue = task.getValue();
            totalRevenueLabel.setText("Tổng: " + totalRevenue + " VND");
        });

        // Thực thi task trong nền
        new Thread(task).start();
    }

    // Phương thức này được gọi khi chuyển đến màn hình Dashboard
    public void loadDashboardData() {

        // Áp dụng hiệu ứng fade cho các biểu đồ
        applyFadeTransition(pieChart);
        applyFadeTransition(barChart);
        applyFadeTransition(lineChart);
        applyTranslateTransition(pieChart);
        applyTranslateTransition(barChart);
        applyTranslateTransition(lineChart);
        // Cập nhật dữ liệu mới cho các biểu đồ
        pieChart.setData(StatisticsService.getStudentsPerCourse());
        barChart.getData().setAll(StatisticsService.getRevenuePerCourse());
        lineChart.getData().setAll(StatisticsService.getTotalRevenueOverTime());

        // Cập nhật lại tổng doanh thu
        updateTotalRevenue();
    }

    // Áp dụng hiệu ứng FadeTransition cho PieChart
    private void applyFadeTransition(PieChart chart) {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5), chart);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
    }

    // Áp dụng hiệu ứng FadeTransition cho BarChart
    private void applyFadeTransition(BarChart<String, Number> chart) {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5), chart);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
    }

    // Áp dụng hiệu ứng FadeTransition cho LineChart
    private void applyFadeTransition(LineChart<String, Number> chart) {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5), chart);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
    }

    // Áp dụng hiệu ứng di chuyển cho PieChart (TranslateTransition)
    private void applyTranslateTransition(PieChart chart) {
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), chart);
        translateTransition.setFromX(-100);  // Di chuyển từ trái qua phải
        translateTransition.setToX(0);      // Dừng tại vị trí ban đầu
        translateTransition.play();
    }

    // Áp dụng hiệu ứng di chuyển cho BarChart (TranslateTransition)
    private void applyTranslateTransition(BarChart<String, Number> chart) {
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), chart);
        translateTransition.setFromX(-100);
        translateTransition.setToX(0);
        translateTransition.play();
    }

    // Áp dụng hiệu ứng di chuyển cho LineChart (TranslateTransition)
    private void applyTranslateTransition(LineChart<String, Number> chart) {
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), chart);
        translateTransition.setFromX(-100);
        translateTransition.setToX(0);
        translateTransition.play();
    }
    // Phương thức initialize, sẽ được gọi khi controller được khởi tạo

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Hiển thị PieChart: Số lượng học viên đăng ký theo từng khóa
        pieChart.setData(StatisticsService.getStudentsPerCourse());

        // Hiển thị BarChart: Doanh thu theo từng khóa học
        barChart.getData().addAll(StatisticsService.getRevenuePerCourse());
        StatisticsService.addHoverEffectsToBarChart(barChart);

        Platform.runLater(() -> {
            barChart.lookup(".chart-plot-background").setStyle("-fx-background-color: transparent;");
        });

        CategoryAxis xAxis = (CategoryAxis) barChart.getXAxis();
        xAxis.setTickLabelRotation(360);

        // Hiển thị LineChart: Doanh thu toàn khóa theo thời gian
        lineChart.getData().addAll(StatisticsService.getTotalRevenueOverTime());
        StatisticsService.addHoverEffectsToLineChart(lineChart);

        Platform.runLater(() -> {
            lineChart.lookup(".chart-plot-background").setStyle("-fx-background-color: transparent;");
        });

        // Cập nhật tổng doanh thu ngay khi màn hình được khởi tạo
        updateTotalRevenue();
        System.out.println("Laod data");
    }
}
