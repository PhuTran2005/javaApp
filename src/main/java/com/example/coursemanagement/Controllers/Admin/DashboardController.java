package com.example.coursemanagement.Controllers.Admin;

import com.example.coursemanagement.Service.StatisticsService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.layout.HBox;

public class DashboardController {

    @FXML private PieChart pieChart;
    @FXML private BarChart<String, Number> barChart;
    @FXML private LineChart<String, Number> lineChart;

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
    }
}
