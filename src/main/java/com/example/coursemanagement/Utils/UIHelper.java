package com.example.coursemanagement.Utils;

import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.Text;

public class UIHelper {


    public static Image generateAvatar(String name, int size) {
        // Lấy chữ cái đầu của từ cuối cùng
        String[] parts = name.trim().split("\\s+");
        String firstLetter = parts[parts.length - 1].substring(0, 1).toUpperCase();

        Canvas canvas = new Canvas(size, size);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Màu nền tròn
        gc.setFill(randomColor());
        gc.fillOval(0, 0, size, size);

        // Font và căn chỉnh
        Font font = Font.font("Arial", FontWeight.BOLD, size * 0.5);
        gc.setFont(font);
        gc.setFill(Color.WHITE);

        Text text = new Text(firstLetter);
        text.setFont(font);
        Bounds bounds = text.getLayoutBounds();

        double x = (size - bounds.getWidth()) / 2;
        double y = (size - bounds.getHeight()) / 2 + text.getBaselineOffset();

        gc.fillText(firstLetter, x, y);

        WritableImage image = new WritableImage(size, size);
        canvas.snapshot(null, image);
        return image;
    }


    public static String getLastWord(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) return "";
        String[] parts = fullName.trim().split("\\s+");
        return parts[parts.length - 1];
    }

    private static Color randomColor() {
        return Color.color(Math.random(), Math.random(), Math.random());
    }
}
