package com.example.coursemanagement.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.math.BigDecimal;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class PaymentService {

    private static final String SEPAY_API_URL = "https://my.sepay.vn/userapi";
    private static final String SEPAY_API_KEY = "EYQJJ4VKTUFAFV55XH1FUWEGNT4KA7I6MRBJXDCCZZNIGLS9NM70AMP6KPRJQEYS";
    private static final String SEPAY_ACCOUNT_NUMBER = "1518893947588";
    private static final String SEPAY_BANK_CODE = "MBBank";

    // Tạo mã QR, hiển thị lên màn hình, và lưu vào file
    public static void taoMaQR(int userId, String accountNumber, String bankCode, String memo, int amount) {
        try {
            // Tạo dữ liệu VietQR (dự phòng nếu API SePay thất bại)
            String qrData = generateVietQRData(accountNumber, bankCode, amount, memo);

            // Thử gọi API SePay
            String url = SEPAY_API_URL + "/qr/create";
            String body = String.format(
                    "{\"account_number\": \"%s\", \"bank_code\": \"%s\", \"amount\": %d, \"memo\": \"%s\", \"order_id\": \"ORDER_%s\"}",
                    accountNumber, bankCode, amount, memo, System.currentTimeMillis());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + SEPAY_API_KEY)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int statusCode = response.statusCode();
            System.out.println("🖼️ Tạo mã QR với nội dung \"" + memo + "\":");
            System.out.println("Status code: " + statusCode);
            System.out.println("Response body: " + response.body());

            if (statusCode != 200) {
                System.out.println("API SePay trả về lỗi (Status code: " + statusCode + "). Sử dụng dữ liệu VietQR...");
            } else {
                JSONObject jsonResponse = new JSONObject(response.body());
                qrData = jsonResponse.getString("qr_data");
            }

            // Hiển thị mã QR lên màn hình và lưu vào file
            String fileName = "qrcode_" + memo + ".png";
            BufferedImage qrImage = generateQRCodeImage(qrData, 200, 200, fileName);
            displayQRCode(qrImage, memo);

        } catch (IOException e) {
            System.err.println("Lỗi kết nối hoặc đọc phản hồi (SePay - QR): " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("Yêu cầu bị gián đoạn (SePay - QR): " + e.getMessage());
            e.printStackTrace();
        } catch (WriterException e) {
            System.err.println("Lỗi khi tạo hình ảnh QR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Tạo chuỗi VietQR theo chuẩn chính thức
    private static String generateVietQRData(String accountNumber, String bankCode, int amount, String memo) {
        StringBuilder qrData = new StringBuilder();
        qrData.append("000201");
        qrData.append("010212");

        String beneficiaryInfo = String.format(
                "3800%02d0100%02dA000000727%02d%02d%s%02d%s%02d%s",
                38,
                bankCode.length(),
                bankCode.length() + 26,
                bankCode.length(),
                bankCode,
                accountNumber.length(),
                accountNumber,
                memo.length(),
                memo
        );
        qrData.append(beneficiaryInfo);

        qrData.append("5303704");

        String amountStr = String.format("%d", amount);
        qrData.append(String.format("54%02d%s", amountStr.length(), amountStr));

        String purpose = String.format("08%02d%s", memo.length(), memo);
        qrData.append(purpose);

        qrData.append("6304");

        String crc = calculateCRC16(qrData.toString());
        qrData.append(crc);

        return qrData.toString();
    }

    // Tính CRC16 theo chuẩn VietQR
    private static String calculateCRC16(String data) {
        int crc = 0xFFFF;
        int polynomial = 0x1021;

        for (int i = 0; i < data.length(); i++) {
            crc ^= (data.charAt(i) << 8);
            for (int j = 0; j < 8; j++) {
                if ((crc & 0x8000) != 0) {
                    crc = (crc << 1) ^ polynomial;
                } else {
                    crc <<= 1;
                }
                crc &= 0xFFFF;
            }
        }

        return String.format("%04X", crc);
    }

    // Tạo hình ảnh QR và lưu vào file, trả về BufferedImage
    private static BufferedImage generateQRCodeImage(String text, int width, int height, String fileName) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        // Đường dẫn tuyệt đối đến thư mục lưu mã QR
        String qrDirectory = "D:\\java\\btl\\btl\\javaApp\\qr";
        Path directoryPath = FileSystems.getDefault().getPath(qrDirectory);

        // Tạo thư mục nếu chưa tồn tại
        Files.createDirectories(directoryPath);

        // Đường dẫn đầy đủ cho file mã QR
        Path filePath = directoryPath.resolve(fileName);

        System.out.println("Đường dẫn lưu mã QR: " + filePath.toAbsolutePath());
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", filePath);

        // Trả về BufferedImage để hiển thị
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    // Hiển thị mã QR lên màn hình
    private static void displayQRCode(BufferedImage qrImage, String memo) {
        JFrame frame = new JFrame("Mã QR - " + memo);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 300);

        JLabel label = new JLabel(new ImageIcon(qrImage));
        frame.add(label);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null); // Căn giữa màn hình
    }

    // Kiểm tra lịch sử giao dịch và cập nhật số dư nếu tìm thấy
    private static void kiemTraLichSuGiaoDich(String tuKhoaNoiDung, int userId, int amount) {
        try {
            String url = SEPAY_API_URL + "/transactions/list?account_number=" + SEPAY_ACCOUNT_NUMBER + "&limit=20";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + SEPAY_API_KEY)
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int statusCode = response.statusCode();
            System.out.println("🧾 Giao dịch có nội dung chứa \"" + tuKhoaNoiDung + "\":");
            System.out.println("Status code: " + statusCode);
            System.out.println("Response body: " + response.body());

            if (statusCode != 200) {
                throw new IOException("Lỗi khi lấy lịch sử giao dịch. Status code: " + statusCode + ", Response: " + response.body());
            }

            JSONObject jsonResponse = new JSONObject(response.body());
            JSONArray transactions = jsonResponse.getJSONArray("transactions");
            boolean transactionFound = false;
            for (int i = 0; i < transactions.length(); i++) {
                JSONObject transaction = transactions.getJSONObject(i);
                String description = transaction.getString("transaction_content");
                if (description.contains(tuKhoaNoiDung)) {
                    System.out.println("Tìm thấy giao dịch: " + transaction.toString());
                    transactionFound = true;

                    // Cập nhật account_balance trong database
                    Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=IT_Course_Management", "username", "password");
                    String sql = "UPDATE Users SET account_balance = account_balance + ? WHERE userId = ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setBigDecimal(1, new BigDecimal(amount));
                    stmt.setInt(2, userId);
                    stmt.executeUpdate();
                    conn.close();

                    System.out.println("Cộng " + amount + " VND vào account_balance của userId " + userId);
                    break; // Thoát vòng lặp sau khi tìm thấy giao dịch
                }
            }
            if (!transactionFound) {
                System.out.println("Không tìm thấy giao dịch với nội dung \"" + tuKhoaNoiDung + "\". Không cập nhật số dư.");
            }

        } catch (IOException e) {
            System.err.println("Lỗi kết nối hoặc đọc phản hồi (SePay - Transactions): " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("Yêu cầu bị gián đoạn (SePay - Transactions): " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật số dư: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Test thử
    public static void main(String[] args) {
        // Tạo và hiển thị mã QR
        taoMaQR(1, SEPAY_ACCOUNT_NUMBER, SEPAY_BANK_CODE, "abc", 10000);

        // Kiểm tra giao dịch và cập nhật số dư
        kiemTraLichSuGiaoDich("abc", 1, 10000);
    }
}