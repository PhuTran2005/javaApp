package com.example.coursemanagement.Utils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class FileUtils {

    public static void openFile(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                System.err.println("File không tồn tại: " + filePath);
                return;
            }

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            } else {
                System.err.println("Desktop không hỗ trợ mở file.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi mở file: " + filePath);
        }
    }
}
