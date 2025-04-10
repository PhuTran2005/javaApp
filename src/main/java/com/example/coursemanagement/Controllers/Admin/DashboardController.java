package com.example.coursemanagement.Controllers.Admin;

import com.example.coursemanagement.Service.StatisticsService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;


public class DashboardController {

    @FXML private PieChart pieChart;
    @FXML private BarChart<String, Number> barChart;
    @FXML private LineChart<String, Number> lineChart;
    @FXML
    private Label totalRevenueLabel;

    private void updateTotalRevenue() {
        Task<Double> task = new Task<>() {
            @Override
            protected Double call() throws Exception {
                // Lấy tổng doanh thu từ StatisticsService
                return StatisticsService.getTotalRevenue();
            }
        };

        // Khi task hoàn thành, cập nhật Label
        task.setOnSucceeded(event -> {
            double totalRevenue = task.getValue();
            totalRevenueLabel.setText("Tổng: " + totalRevenue + " VND");
        });

        // Thực thi task trong nền
        new Thread(task).start();
    }

    @FXML
    public void initialize() {
        // Hiển thị PieChart: Số lượng học viên đăng ký theo từng khóa
        pieChart.setData(StatisticsService.getStudentsPerCourse());

        barChart.getData().addAll(StatisticsService.getRevenuePerCourse());
        // Thêm hiệu ứng hover cho BarChart
        StatisticsService.addHoverEffectsToBarChart(barChart);

        Platform.runLater(() -> {
            barChart.lookup(".chart-plot-background").setStyle("-fx-background-color: transparent;");
        });
        CategoryAxis xAxis = (CategoryAxis) barChart.getXAxis();
        xAxis.setTickLabelRotation(360);

        // Hiển thị LineChart: Doanh thu toàn khóa theo thời gian
        lineChart.getData().addAll(StatisticsService.getTotalRevenueOverTime());
        // hover linechart
        StatisticsService.addHoverEffectsToLineChart(lineChart);

        // Đảm bảo thay đổi nền sau khi biểu đồ được tải
        Platform.runLater(() -> {
            lineChart.lookup(".chart-plot-background").setStyle("-fx-background-color: transparent;");
        });

        // Cập nhật tổng doanh thu khi màn hình được khởi tạo
        updateTotalRevenue();
    }

    // Phương thức được gọi khi chuyển đến màn hình Dashboard
    public void loadDashboardData() {
        // Gọi lại updateTotalRevenue để tải lại tổng doanh thu
        updateTotalRevenue();

        // Lấy lại dữ liệu mới nhất từ cơ sở dữ liệu cho các biểu đồ
        pieChart.setData(StatisticsService.getStudentsPerCourse());
        barChart.getData().setAll(StatisticsService.getRevenuePerCourse());
        lineChart.getData().setAll(StatisticsService.getTotalRevenueOverTime());
    }

}
