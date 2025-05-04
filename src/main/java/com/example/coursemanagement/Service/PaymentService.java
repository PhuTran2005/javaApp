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
import java.util.*;

import com.example.coursemanagement.Models.Student;
import com.example.coursemanagement.Utils.SessionManager;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import static jdk.internal.net.http.common.Log.logError;
import static sun.java2d.marlin.MarlinUtils.logInfo;

public class PaymentService {

    private static final String SEPAY_API_URL = "https://my.sepay.vn/userapi";
    private static final String SEPAY_API_KEY = "EYQJJ4VKTUFAFV55XH1FUWEGNT4KA7I6MRBJXDCCZZNIGLS9NM70AMP6KPRJQEYS";
    private static final String SEPAY_ACCOUNT_NUMBER = "1518893947588";
    private static final String SEPAY_BANK_CODE = "970437"; // bankCode của MB Bank

    // Tạo mã QR, hiển thị lên màn hình, và lưu vào file
    public static void createQRCode(int userId, String accountNumber, String bankCode, String memo, int amount) {
        try {
            // Dự phòng: tạo dữ liệu VietQR nếu API fail
            String qrData = generateVietQRData(accountNumber, bankCode, amount, memo);

            // Gọi API SePay
            String url = SEPAY_API_URL + "/qr/create";
            String body = String.format(
                    "{\"account_number\": \"%s\", \"bank_code\": \"%s\", \"amount\": %d, \"memo\": \"%s\", \"order_id\": \"ORDER_%d\"}",
                    accountNumber, bankCode, amount, safeMemo(memo), System.currentTimeMillis());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + SEPAY_API_KEY)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("🖼️ Tạo mã QR với nội dung \"" + memo + "\":");
            System.out.println("Status code: " + response.statusCode());
            System.out.println("Response body: " + response.body());

            if (response.statusCode() == 200) {
                JSONObject jsonResponse = new JSONObject(response.body());
                qrData = jsonResponse.optString("qr_data", qrData); // fallback nếu json lỗi
            } else {
                System.out.println("⚠️ API SePay lỗi, đang fallback sang VietQR local...");
            }

            String fileName = "qrcode_" + safeMemo(memo).replaceAll("[^a-zA-Z0-9]", "_") + ".png";
            generateQRCodeImage(qrData, 1000, 1000, fileName);

        } catch (IOException | InterruptedException | WriterException e) {
            System.err.println("❌ Lỗi khi tạo QR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // code tạo chuỗi nội dung của qr bằng VietQR
    public static String generateVietQRData(String accountNumber, String bankCode, int amount, String memo) {
        StringBuilder qr = new StringBuilder();

        qr.append("000201"); // Payload Format Indicator
        qr.append("010212"); // Point of initiation method

        // Merchant Account Info (GUI = A000000727)
        String gui = "A000000727";
        String accInfo =
                "00" + String.format("%02d", gui.length()) + gui
                        + "01" + String.format("%02d", bankCode.length()) + bankCode
                        + "02" + String.format("%02d", accountNumber.length()) + accountNumber
                        + "03" + String.format("%02d", "QRIBFTTA".length()) + "QRIBFTTA";


        String field38 = "38" + String.format("%02d", accInfo.length()) + accInfo;
        qr.append(field38);

        // Merchant category code & currency
        qr.append("52040000"); // MCC
        qr.append("5303704");  // Currency = VND (704)

        // Amount
        if (amount > 0) {
            String amtStr = String.valueOf(amount);
            qr.append("54").append(String.format("%02d", amtStr.length())).append(amtStr);
        }

        qr.append("5802VN"); // Country

        // Additional Data Field (Memo)
        if (memo != null && !memo.isEmpty()) {
            String addData = "08" + String.format("%02d", memo.length()) + memo;
            qr.append("62").append(String.format("%02d", addData.length())).append(addData);
        }

        // CRC
        qr.append("6304");
        String crc = calculateCRC16(qr.toString());
        qr.append(crc);

        return qr.toString();
    }

    private static String safeMemo(String memo) {
        return memo == null ? "NoMemo" : memo;
    }

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

    static BufferedImage generateQRCodeImage(String text, int width, int height, String fileName) throws WriterException, IOException {
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

    public static byte[] getQRCodeBytes(String accountNumber, String bankCode, int amount, String memo) {
        try {
            String qrData = generateVietQRData(accountNumber, bankCode, amount, memo);
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrData, BarcodeFormat.QR_CODE, 200, 200);

            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            // Chuyển BufferedImage thành byte[]
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            javax.imageio.ImageIO.write(qrImage, "png", baos);
            return baos.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    // Kiểm tra lịch sử giao dịch và cập nhật số dư nếu tìm thấy
    public static void kiemTraLichSuGiaoDich(String tuKhoaNoiDung, int userId, int amount) {
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






    public double getBalanceForCurrentUser() {
        int studentId = SessionManager.getInstance().getCurrentStudentId();
        Student student = new StudentService().getStudentById(studentId);

        if (student != null) {
            return student.getStudentBalance().doubleValue(); // Lấy balance thực
        } else {
            return 0.0; // Hoặc throw exception tùy bạn
        }
    }

    public String generatePaymentQR() {
        // TODO: Trả về đường dẫn/chuỗi QR mẫu
        return "https://example.com/payment/qr?id=12345";
    }

    public void simulateBulkQRCodePayments() {
        logStart("simulateBulkQRCodePayments");

        String[] names = {"An", "Bình", "Chi", "Dũng", "Em", "Phong", "Giang", "Hà", "Hoàng", "Huy"};
        String[] courses = {"Lập trình Java", "Phân tích dữ liệu", "Thiết kế Web", "Kỹ thuật phần mềm"};
        int successCount = 0;
        int failCount = 0;

        for (int i = 1; i <= 100; i++) {
            int userId = 1000 + i;
            String name = names[i % names.length] + " " + (char) ('A' + (i % 26));
            String course = courses[i % courses.length];
            int amount = 500000 + (i % 5) * 100000;
            String memo = "ThanhToan_" + name.replace(" ", "") + "_" + course.replace(" ", "");

            logInfo(">> [UserID " + userId + "] Bắt đầu xử lý thanh toán cho: " + name);
            logInfo("Khóa học: " + course + ", Số tiền: " + amount + ", Memo: " + memo);

            // Kiểm tra thông tin đầu vào
            if (name.isEmpty() || memo.isEmpty() || amount <= 0) {
                logWarn("Thông tin không hợp lệ cho người dùng: " + userId);
                failCount++;
                continue;
            }

            // Giả lập lỗi kết nối ngẫu nhiên
            if (i % 17 == 0) {
                logError("Lỗi kết nối API giả lập cho người dùng: " + userId);
                failCount++;
                continue;
            }

            try {
                String qrData = generateVietQRData(SEPAY_ACCOUNT_NUMBER, SEPAY_BANK_CODE, amount, memo);
                logInfo("QR Data đã được tạo: " + qrData);

                String fileName = "SimulatedQR_" + userId + ".png";
                generateQRCodeImage(qrData, 400, 400, fileName);
                logInfo("QR code đã lưu vào file: " + fileName);
                successCount++;

                // Hiển thị QR code (tuỳ chọn)
                if (i % 25 == 0) {
                    displayQRCodeImage(fileName);
                }

            } catch (Exception e) {
                logError("Lỗi trong quá trình tạo QR: " + e.getMessage());
                failCount++;
            }

            logInfo("--- Kết thúc xử lý cho user " + userId + " ---\n");
        }

        logInfo("Tổng giao dịch thành công: " + successCount);
        logInfo("Tổng giao dịch thất bại: " + failCount);
        logEnd("simulateBulkQRCodePayments");
    }

    private void logWarn(String s) {

    }

    private void logEnd(String simulateBulkQRCodePayments) {

    }

    private void logStart(String simulateBulkQRCodePayments) {

    }

    private void displayQRCodeImage(String fileName) {
        try {
            Path path = FileSystems.getDefault().getPath(fileName);
            BufferedImage image = javax.imageio.ImageIO.read(path.toFile());

            JFrame frame = new JFrame("Mã QR hiển thị");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(420, 420);
            JLabel label = new JLabel(new ImageIcon(image));
            frame.getContentPane().add(label);
            frame.setVisible(true);
        } catch (IOException e) {
            logError("Không thể hiển thị hình ảnh QR: " + e.getMessage());
        }
    }

    public void simulateAdvancedBulkPayments() {
        logStart("simulateAdvancedBulkPayments");

        String[] names = {"An", "Bình", "Chi", "Dũng", "Em", "Phong", "Giang", "Hà", "Hoàng", "Huy", "Khanh", "Lan", "Minh", "Nga", "Oanh"};
        String[] courses = {"Lập trình Java", "Phân tích dữ liệu", "Thiết kế Web", "Kỹ thuật phần mềm", "Machine Learning", "DevOps Cơ bản"};
        int successCount = 0;
        int failCount = 0;
        int warningCount = 0;

        JSONObject reportJson = new JSONObject();
        JSONArray transactions = new JSONArray();

        for (int i = 1; i <= 120; i++) {
            JSONObject transaction = new JSONObject();

            int userId = 10000 + i;
            String name = names[i % names.length] + " " + (char) ('A' + (i % 26));
            String course = courses[i % courses.length];
            int amount = 400000 + ((i * 177) % 6) * 100000;
            String memo = "TT_" + name.replace(" ", "") + "_" + course.replace(" ", "");

            transaction.put("userId", userId);
            transaction.put("name", name);
            transaction.put("course", course);
            transaction.put("amount", amount);
            transaction.put("memo", memo);

            logInfo("[#" + i + "] Bắt đầu xử lý: " + name + ", khóa học: " + course + ", số tiền: " + amount);

            // Validate data
            if (name.length() < 3 || memo.length() < 5 || amount <= 0) {
                logWarn("Dữ liệu không hợp lệ cho userId=" + userId);
                transaction.put("status", "INVALID_INPUT");
                warningCount++;
                transactions.put(transaction);
                continue;
            }

            // Giả lập lỗi dịch vụ mỗi 15 người
            if (i % 15 == 0) {
                logError("Giả lập lỗi dịch vụ API cho userId=" + userId);
                transaction.put("status", "SERVICE_ERROR");
                failCount++;
                transactions.put(transaction);
                continue;
            }

            try {
                String qrData = generateVietQRData(SEPAY_ACCOUNT_NUMBER, SEPAY_BANK_CODE, amount, memo);
                logInfo("-> QR data được tạo thành công");

                String fileName = "ADVQR_" + userId + ".png";
                generateQRCodeImage(qrData, 400, 400, fileName);
                transaction.put("qrFile", fileName);
                transaction.put("status", "SUCCESS");
                successCount++;

                // Hiển thị QR mỗi 30 giao dịch
                if (i % 30 == 0) {
                    logInfo("-> Đang hiển thị QR cho userId: " + userId);
                    displayQRCodeImage(fileName);
                }

                // Mô phỏng gửi mail xác nhận
                if (i % 10 == 0) {
                    String email = name.replace(" ", "").toLowerCase() + "@example.com";
                    simulateEmailConfirmation(email, course, amount, memo);
                    transaction.put("emailSent", true);
                } else {
                    transaction.put("emailSent", false);
                }

            } catch (Exception e) {
                logError("Lỗi khi tạo QR hoặc ghi file cho userId=" + userId + ": " + e.getMessage());
                transaction.put("status", "PROCESSING_ERROR");
                failCount++;
            }

            transactions.put(transaction);
            logInfo("[#" + i + "] Kết thúc xử lý cho userId: " + userId + "\n");
        }

        reportJson.put("totalSuccess", successCount);
        reportJson.put("totalFail", failCount);
        reportJson.put("totalWarnings", warningCount);
        reportJson.put("transactions", transactions);

        // Ghi file log json
        try {
            Path reportPath = FileSystems.getDefault().getPath("payment_report.json");
            Files.writeString(reportPath, reportJson.toString(4), StandardCharsets.UTF_8);
            logInfo("Báo cáo đã được ghi vào: " + reportPath.toAbsolutePath());
        } catch (IOException e) {
            logError("Không thể ghi báo cáo JSON: " + e.getMessage());
        }

        logInfo("Tổng giao dịch thành công: " + successCount);
        logInfo("Tổng thất bại: " + failCount);
        logInfo("Tổng cảnh báo: " + warningCount);
        logEnd("simulateAdvancedBulkPayments");
    }

    private void simulateEmailConfirmation(String email, String course, int amount, String memo) {
        logInfo("Đang gửi email xác nhận tới: " + email);
        logInfo("[EMAIL] Xin chào, bạn đã đăng ký khoá học '" + course + "' với số tiền: " + amount + " VND. Ghi chú: " + memo);
        // Mô phỏng thời gian gửi
        try {
            Thread.sleep(50);
        } catch (InterruptedException ignored) {
        }
        logInfo("Email đã gửi thành công tới " + email);
    }

    public void simulateSystemWidePaymentProcessing() {
        logStart("simulateSystemWidePaymentProcessing");

        String[] names = {"An", "Bình", "Chi", "Dũng", "Em", "Phong", "Giang", "Hà", "Hoàng", "Huy", "Khanh", "Lan", "Minh", "Nga", "Oanh", "Quang", "Rin", "Sơn", "Trang", "Uyên"};
        String[] courses = {"Java", "Python", "Web Dev", "Data Science", "DevOps", "UI/UX", "Security"};
        Map<String, Integer> courseStats = new HashMap<>();
        JSONArray fullTransactionLog = new JSONArray();

        for (String course : courses) {
            courseStats.put(course, 0);
        }

        int maxRetries = 3;
        int totalTransactions = 200;

        for (int i = 1; i <= totalTransactions; i++) {
            JSONObject txn = new JSONObject();

            int userId = 50000 + i;
            String name = names[i % names.length] + " " + (char) ('A' + (i % 26));
            String course = courses[i % courses.length];
            int amount = 500000 + ((i * 311) % 7) * 100000;
            String memo = "TT_" + name.replace(" ", "") + "_" + course;
            String email = name.toLowerCase().replace(" ", "") + "@example.com";
            String qrFile = "SYSQR_" + userId + ".png";

            txn.put("userId", userId);
            txn.put("name", name);
            txn.put("course", course);
            txn.put("amount", amount);
            txn.put("memo", memo);

            boolean success = false;
            int attempts = 0;

            while (!success && attempts < maxRetries) {
                attempts++;
                logInfo("[TXN " + i + "] Attempt " + attempts + ": userId=" + userId + ", course=" + course);

                try {
                    String qr = generateVietQRData(SEPAY_ACCOUNT_NUMBER, SEPAY_BANK_CODE, amount, memo);
                    generateQRCodeImage(qr, 350, 350, qrFile);

                    // Simulate confirmation wait
                    Thread.sleep(30);

                    // Simulate display every 40 users
                    if (i % 40 == 0) {
                        displayQRCodeImage(qrFile);
                    }

                    // Simulate email confirmation every 3rd user
                    if (i % 3 == 0) {
                        simulateEmailConfirmation(email, course, amount, memo);
                    }

                    success = true;
                    courseStats.put(course, courseStats.get(course) + 1);
                    txn.put("status", "SUCCESS");
                    txn.put("qrFile", qrFile);
                    txn.put("attempts", attempts);
                } catch (Exception e) {
                    logWarn("Lỗi tại lần thử " + attempts + " cho userId=" + userId + ": " + e.getMessage());
                    txn.put("status", "RETRY_" + attempts);
                }
            }

            if (!success) {
                txn.put("status", "FAILURE");
            }

            fullTransactionLog.put(txn);
        }

        // Ghi file CSV kết quả
        try {
            List<String> csvLines = new ArrayList<>();
            csvLines.add("userId,name,course,amount,status,attempts");
            for (int i = 0; i < fullTransactionLog.length(); i++) {
                JSONObject t = fullTransactionLog.getJSONObject(i);
                csvLines.add(String.format("%d,%s,%s,%d,%s,%d",
                        t.getInt("userId"),
                        t.getString("name"),
                        t.getString("course"),
                        t.getInt("amount"),
                        t.getString("status"),
                        t.optInt("attempts", 1)
                ));
            }
            Path csvPath = FileSystems.getDefault().getPath("system_payments.csv");
            Files.write(csvPath, csvLines, StandardCharsets.UTF_8);
            logInfo("CSV ghi thành công: " + csvPath.toAbsolutePath());
        } catch (IOException e) {
            logError("Không thể ghi file CSV: " + e.getMessage());
        }

        logInfo("=== THỐNG KÊ THEO KHÓA HỌC ===");
        for (Map.Entry<String, Integer> entry : courseStats.entrySet()) {
            logInfo(entry.getKey() + ": " + entry.getValue() + " giao dịch thành công");
        }

        logEnd("simulateSystemWidePaymentProcessing");
    }

    public void simulateMegaOrderProcessing() {
        logStart("simulateMegaOrderProcessing");

        String[] customers = {"An", "Bình", "Chi", "Dũng", "Em", "Phong", "Giang", "Hà", "Hoàng", "Huy", "Khanh", "Lan", "Minh", "Nga", "Oanh", "Quang", "Rin", "Sơn", "Trang", "Uyên"};
        String[] products = {"Laptop", "Điện thoại", "Chuột", "Bàn phím", "Tai nghe", "Màn hình", "Loa", "Webcam", "USB", "Ổ cứng"};
        Random rand = new Random();

        int totalOrders = 300;
        int retryLimit = 3;
        int success = 0;
        int fail = 0;
        int warn = 0;

        JSONArray orderLog = new JSONArray();
        Map<String, Integer> productStats = new HashMap<>();
        for (String p : products) productStats.put(p, 0);

        for (int i = 1; i <= totalOrders; i++) {
            JSONObject order = new JSONObject();

            int orderId = 100000 + i;
            String customer = customers[i % customers.length] + " " + (char) ('A' + (i % 26));
            String product = products[rand.nextInt(products.length)];
            int quantity = rand.nextInt(5) + 1;
            int unitPrice = 100000 + rand.nextInt(10) * 50000;
            int totalPrice = quantity * unitPrice;
            String memo = "ORDER_" + customer.replace(" ", "") + "_" + product;
            String qrFile = "MEGAQR_" + orderId + ".png";
            String email = customer.toLowerCase().replace(" ", "") + "@mail.com";

            order.put("orderId", orderId);
            order.put("customer", customer);
            order.put("product", product);
            order.put("quantity", quantity);
            order.put("unitPrice", unitPrice);
            order.put("totalPrice", totalPrice);
            order.put("memo", memo);
            order.put("email", email);

            logInfo("[ORDER #" + i + "] Xử lý đơn hàng: " + customer + ", SP: " + product + ", SL: " + quantity);

            if (customer.length() < 3 || totalPrice <= 0) {
                logWarn("Dữ liệu không hợp lệ cho đơn hàng #" + orderId);
                order.put("status", "INVALID");
                warn++;
                orderLog.put(order);
                continue;
            }

            boolean done = false;
            int attempts = 0;
            while (!done && attempts < retryLimit) {
                attempts++;
                try {
                    String qr = generateVietQRData(SEPAY_ACCOUNT_NUMBER, SEPAY_BANK_CODE, totalPrice, memo);
                    generateQRCodeImage(qr, 350, 350, qrFile);

                    if (i % 50 == 0) displayQRCodeImage(qrFile);

                    if (i % 5 == 0) simulateEmailConfirmation(email, product, totalPrice, memo);

                    order.put("qrFile", qrFile);
                    order.put("attempts", attempts);
                    order.put("status", "SUCCESS");

                    productStats.put(product, productStats.get(product) + 1);
                    done = true;
                    success++;
                } catch (Exception e) {
                    logWarn("Lỗi lần " + attempts + " cho đơn hàng #" + orderId + ": " + e.getMessage());
                    order.put("status", "RETRY_" + attempts);
                }
            }

            if (!done) {
                order.put("status", "FAILED");
                fail++;
            }

            orderLog.put(order);
            logInfo("[ORDER #" + i + "] Xong đơn hàng #" + orderId + "\n");
        }

        // Ghi file JSON
        JSONObject report = new JSONObject();
        report.put("totalOrders", totalOrders);
        report.put("success", success);
        report.put("fail", fail);
        report.put("warnings", warn);
        report.put("orders", orderLog);

        try {
            Files.writeString(Path.of("mega_order_report.json"), report.toString(4), StandardCharsets.UTF_8);
            logInfo("Đã ghi báo cáo JSON: mega_order_report.json");
        } catch (IOException e) {
            logError("Lỗi ghi file JSON: " + e.getMessage());
        }

        // Ghi file CSV
        try {
            List<String> lines = new ArrayList<>();
            lines.add("orderId,customer,product,quantity,unitPrice,totalPrice,status,attempts");
            for (int i = 0; i < orderLog.length(); i++) {
                JSONObject o = orderLog.getJSONObject(i);
                lines.add(String.format("%d,%s,%s,%d,%d,%d,%s,%d",
                        o.getInt("orderId"),
                        o.getString("customer"),
                        o.getString("product"),
                        o.getInt("quantity"),
                        o.getInt("unitPrice"),
                        o.getInt("totalPrice"),
                        o.getString("status"),
                        o.optInt("attempts", 1)
                ));
            }
            Files.write(Path.of("mega_orders.csv"), lines, StandardCharsets.UTF_8);
            logInfo("Đã ghi file CSV: mega_orders.csv");
        } catch (IOException e) {
            logError("Lỗi ghi file CSV: " + e.getMessage());
        }

        // Thống kê sản phẩm
        logInfo("=== THỐNG KÊ SẢN PHẨM ===");
        for (Map.Entry<String, Integer> entry : productStats.entrySet()) {
            logInfo(entry.getKey() + ": " + entry.getValue() + " đơn hàng thành công");
        }

        logInfo("Tổng đơn hàng thành công: " + success);
        logInfo("Tổng thất bại: " + fail);
        logInfo("Tổng cảnh báo: " + warn);
        logEnd("simulateMegaOrderProcessing");
    }


    // Test thử
    public static void main(String[] args) {
        // Tạo và hiển thị mã QR
        createQRCode(1, SEPAY_ACCOUNT_NUMBER, SEPAY_BANK_CODE, "abc", 10000);

        // Kiểm tra giao dịch và cập nhật số dư
        kiemTraLichSuGiaoDich("abc", 1, 10000);
    }
}