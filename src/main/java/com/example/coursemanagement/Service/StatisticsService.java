package com.example.coursemanagement.Service;

import com.example.coursemanagement.Utils.DatabaseConfig;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Tooltip;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StatisticsService {

    public static ObservableList<PieChart.Data> getStudentsPerCourse() {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();

        String query = "SELECT c.course_name AS course_name, COUNT(e.enrollments_id) AS student_count " +
                "FROM Courses c " +
                "LEFT JOIN Enrollments e ON c.course_id = e.course_id " +
                "GROUP BY c.course_name";

        int totalStudents = 0; // Tổng số học viên

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String courseName = rs.getString("course_name");
                int studentCount = rs.getInt("student_count");
                totalStudents += studentCount;
                data.add(new PieChart.Data(courseName, studentCount));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        int finalTotalStudents = totalStudents; // Biến cố định để dùng trong lambda

        // Chạy trên UI Thread để cập nhật Tooltip
        Platform.runLater(() -> {
            for (PieChart.Data slice : data) {
                int studentCount = (int) slice.getPieValue();
                double percentage = finalTotalStudents > 0 ? (studentCount * 100.0 / finalTotalStudents) : 0;

                // 1️⃣ Cài đặt Tooltip khi hover vào PieChart
                Tooltip tooltip = new Tooltip(slice.getName() + ": " + studentCount + "/" + finalTotalStudents
                        + " học viên (" + String.format("%.1f", percentage) + "%)");
                Tooltip.install(slice.getNode(), tooltip);

                // 2️⃣ Hiển thị số liệu trực tiếp trên PieChart
                slice.getNode().setOnMouseEntered(event ->
                        slice.getNode().setStyle("-fx-scale-x: 1.1; -fx-scale-y: 1.1; -fx-font-size: 14px; -fx-fill: white;")
                );
                slice.getNode().setOnMouseExited(event ->
                        slice.getNode().setStyle("-fx-scale-x: 1; -fx-scale-y: 1;")
                );

                // 3️⃣ Cập nhật tooltip khi giá trị thay đổi
                slice.pieValueProperty().addListener((obs, oldValue, newValue) -> {
                    double newPercentage = finalTotalStudents > 0 ? (newValue.doubleValue() * 100.0 / finalTotalStudents) : 0;
                    tooltip.setText(slice.getName() + ": " + newValue.intValue() + "/" + finalTotalStudents
                            + " học viên (" + String.format("%.1f", newPercentage) + "%)");
                });
            }
        });
        return data;
    }


    // 2️⃣ BarChart: Lấy doanh thu của top 10 khóa học có doanh thu cao nhất
    public static ObservableList<XYChart.Series<String, Number>> getRevenuePerCourse() {
        ObservableList<XYChart.Series<String, Number>> dataList = FXCollections.observableArrayList();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Doanh thu");

        String query = "SELECT TOP 10 c.course_name AS course_name, SUM(p.amount) AS revenue " +
                "FROM Courses c " +
                "LEFT JOIN Enrollments e ON c.course_id = e.course_id " +
                "LEFT JOIN Payments p ON e.enrollments_id = p.enrollments_id " +
                "WHERE p.status = 'Success' " +
                "GROUP BY c.course_name " +
                "ORDER BY revenue DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String courseName = rs.getString("course_name");
                double revenue = rs.getDouble("revenue");
                series.getData().add(new XYChart.Data<>(courseName, revenue));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        dataList.add(series);
        return dataList;
    }

    // 3️⃣ Xử lý sự kiện hover cho BarChart
    public static void addHoverEffectsToBarChart(BarChart<String, Number> barChart) {
        Platform.runLater(() -> {
            for (XYChart.Series<String, Number> series : barChart.getData()) {
                for (XYChart.Data<String, Number> data : series.getData()) {
                    // Lấy doanh thu từ cột
                    double revenue = data.getYValue().doubleValue();
                    String courseName = data.getXValue();  // Tên khóa học

                    // 1️⃣ Cài đặt Tooltip khi hover vào cột
                    Tooltip tooltip = new Tooltip(courseName + ": " + String.format("%.2f", revenue) + " VNĐ");
                    Tooltip.install(data.getNode(), tooltip);

                    // 2️⃣ Thêm sự kiện hover vào cột
                    data.getNode().setOnMouseEntered(event -> {
                        // Tăng kích thước khi hover và đổi màu
                        data.getNode().setStyle("-fx-scale-x: 1.2; -fx-scale-y: 1.2; -fx-font-size: 14px; -fx-fill: red;");
                        // Hiển thị tooltip khi hover
                        tooltip.show(data.getNode(), event.getScreenX(), event.getScreenY() + 15); // Thêm một chút offset cho tooltip
                    });

                    data.getNode().setOnMouseExited(event -> {
                        // Khôi phục lại trạng thái cũ của cột khi thoát khỏi hover
                        data.getNode().setStyle("-fx-scale-x: 1; -fx-scale-y: 1;");
                        // Ẩn tooltip khi không hover nữa
                        tooltip.hide();
                    });
                }
            }
        });
    }

    // 3️⃣ LineChart: Lấy doanh thu toàn khóa theo thời gian
    public static ObservableList<XYChart.Series<String, Number>> getTotalRevenueOverTime() {
        ObservableList<XYChart.Series<String, Number>> dataList = FXCollections.observableArrayList();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Doanh thu");

        String query = "SELECT FORMAT(p.payment_date, 'yyyy-MM-dd') AS month, SUM(p.amount) AS revenue " +
                "FROM Payments p " +
                "WHERE p.status = 'Success' " +
                "GROUP BY FORMAT(p.payment_date, 'yyyy-MM-dd') " +
                "ORDER BY month ASC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String month = rs.getString("month");
                double revenue = rs.getDouble("revenue");
                series.getData().add(new XYChart.Data<>(month, revenue));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dataList.add(series);
        return dataList;
    }

    // 3️⃣ LineChart: Xử lý sự kiện hover cho LineChart
    public static void addHoverEffectsToLineChart(LineChart<String, Number> lineChart) {
        Platform.runLater(() -> {
            for (XYChart.Series<String, Number> series : lineChart.getData()) {
                for (XYChart.Data<String, Number> data : series.getData()) {
                    double revenue = data.getYValue().doubleValue(); // Doanh thu của điểm
                    String date = data.getXValue();  // Thời gian (ngày)

                    // 1️⃣ Cài đặt Tooltip khi hover vào điểm
                    Tooltip tooltip = new Tooltip(date + ": " + String.format("%.2f", revenue) + " VNĐ");
                    Tooltip.install(data.getNode(), tooltip);

                    // 2️⃣ Thêm sự kiện hover vào điểm trong LineChart
                    data.getNode().setOnMouseEntered(event -> {
                        // Tăng kích thước khi hover và đổi màu
                        data.getNode().setStyle("-fx-scale-x: 1.5; -fx-scale-y: 1.5; -fx-fill: red;");
                        // Hiển thị tooltip khi hover
                        tooltip.show(data.getNode(), event.getScreenX(), event.getScreenY() + 15); // Thêm một chút offset cho tooltip
                    });

                    data.getNode().setOnMouseExited(event -> {
                        // Khôi phục lại trạng thái cũ của điểm khi thoát khỏi hover
                        data.getNode().setStyle("-fx-scale-x: 1; -fx-scale-y: 1;");
                        // Ẩn tooltip khi không hover nữa
                        tooltip.hide();
                    });
                }
            }
        });
    }

    // Thêm phương thức để lấy tổng doanh thu toàn khóa
    public static double getTotalRevenue() {
        double totalRevenue = 0.0;

        String query = "SELECT SUM(p.amount) AS revenue FROM Payments p WHERE p.status = 'Success'";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                totalRevenue = rs.getDouble("revenue");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalRevenue;
    }




}
