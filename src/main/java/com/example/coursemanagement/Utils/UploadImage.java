package com.example.coursemanagement.Utils;

import javafx.scene.image.Image;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class UploadImage {
    // lưu ảnh đã chọn vào folder .
    public static String saveImageToFolder(File file,String newFileName) {
        try {
            // Tạo thư mục 'images' cùng cấp với thư mục dự án
            String folderName = "src/main/resources/Images";
            String absolutePath = System.getProperty("user.dir") + File.separator + folderName;

            File dir = new File(absolutePath);
            if (!dir.exists()) {
                dir.mkdirs(); // Tạo thư mục nếu chưa tồn tại
            }

            // Tạo tên file mới để tránh trùng lặp
            File destFile = new File(dir, newFileName);

            // Sao chép file vào thư mục
            Files.copy(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // Trả về đường dẫn tương đối (hoặc tuyệt đối nếu bạn muốn load ngay)
            return "Images" + "/" + newFileName; // => ví dụ: images/abc123_myimage.png
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    // khởi tạo một đối tượng image bằng link online hoặc đường dẫn ảnh trong tệp
    public static Image loadImage(String path) {
        try {
            if (path.startsWith("http://") || path.startsWith("https://")) {
                // Ảnh từ internet
                return new Image(path, true);
            }
            else {
                // Ảnh từ hệ thống tập tin (thư mục ngoài, ví dụ: images/)
                File file = new File(path);
                if (file.exists()) {
                    return new Image(file.toURI().toString());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}