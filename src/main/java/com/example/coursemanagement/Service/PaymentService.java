package com.example.coursemanagement.Service;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import com.example.coursemanagement.Controllers.Client.ClientMenuController;
import com.example.coursemanagement.Dto.CourseDetailDTO;
import com.example.coursemanagement.Utils.Alerts;
import com.example.coursemanagement.Utils.DatabaseConfig;
import com.example.coursemanagement.Utils.SessionManager;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.json.JSONArray;
import org.json.JSONObject;


public class PaymentService {
    private Alerts alerts = new Alerts();
    private static LogService logService = new LogService();

    // Thiết lập môi trường thử nghiệm
    private static PurchaseCourseService purchaseCourseService = new PurchaseCourseService();

    // Đường dẫn kết nối database
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=IT_Course_Management;encrypt=true;trustServerCertificate=true";
//    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=IT_Course_Management;integratedSecurity=true;encrypt=true;trustServerCertificate=true";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "1234567890";

    // Thời gian đợi mô phỏng thanh toán (mili giây)
    private static final int PAYMENT_SIMULATION_TIME = 5000; // 10 giây

    private static final String SEPAY_API_URL = "https://my.sepay.vn/userapi";
    private static final String SEPAY_API_KEY = "EYQJJ4VKTUFAFV55XH1FUWEGNT4KA7I6MRBJXDCCZZNIGLS9NM70AMP6KPRJQEYS";
    private static final String SEPAY_ACCOUNT_NUMBER = "1518893947588";
    private static final String SEPAY_BANK_CODE = "970437"; // bankCode của MB Bank

    // Tạo mã QR, hiển thị lên màn hình, và lưu vào file
    public static void createQRCode(String accountNumber, String bankCode, String memo, int amount, int qrSize) {
        try {
            // Xóa tất cả các file trong thư mục "qr"
            File qrDirectory = new File("qr");
            if (qrDirectory.exists() && qrDirectory.isDirectory()) {
                File[] files = qrDirectory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isFile()) {
                            boolean deleted = file.delete();
                            if (deleted) {
                                System.out.println("✅ Đã xóa file: " + file.getName());
                            } else {
                                System.out.println("❌ Không thể xóa file: " + file.getName());
                            }
                        }
                    }
                }
            }
            // Dự phòng: tạo dữ liệu VietQR nếu API fail
            String qrData = generateVietQRData(accountNumber, bankCode, amount, memo);
            String fileName = "qrcode_" + safeMemo(memo).replaceAll("[^a-zA-Z0-9]", "_") + ".png";
            generateQRCodeImage(qrData, qrSize, qrSize, fileName);

        } catch (IOException |  WriterException e) {
            System.err.println("❌ Lỗi khi tạo QR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // code tạo chuỗi nội dung của qr bằng VietQR
    public static String generateVietQRData(String accountNumber, String bankCode, int amount, String memo) {
        StringBuilder qr = new StringBuilder();

        qr.append("000201"); // Payload Format Indicator
        qr.append("010212"); // Point of initiation method

        // Merchant Account Info
//        String gui = "A000000727";
//        String shortBankCode = bankCode.substring(0, 4); // MB = "9704"
//        String merchantID = "0006" + shortBankCode + "2201" + accountNumber;
//
//        String accInfo =
//                "00" + String.format("%02d", gui.length()) + gui +
//                        "01" + String.format("%02d", merchantID.length()) + merchantID +
//                        "03" + String.format("%02d", "QRIBFTTA".length()) + "QRIBFTTA";
//
//        String field38 = "38" + String.format("%02d", accInfo.length()) + accInfo;
//        qr.append(field38);

        // Merchant Account Info (Strict VietQR format)
        String gui = "A000000727";
        String shortBankCode = bankCode.substring(0, 4); // 970437 → 9704
        String merchantVal = "0006" + shortBankCode + "220113" + accountNumber;

        String accInfo =
                "00" + String.format("%02d", gui.length()) + gui +
                        "01" + String.format("%02d", merchantVal.length()) + merchantVal +
                        "02" + String.format("%02d", "QRIBFTTA".length()) + "QRIBFTTA";

        String field38 = "38" + String.format("%02d", accInfo.length()) + accInfo;
        qr.append(field38);




        qr.append("52040000"); // Merchant Category Code (MCC)
        qr.append("5303704");  // Currency = VND (704)

        if (amount > 0) {
            String amtStr = String.valueOf(amount);
            qr.append("54").append(String.format("%02d", amtStr.length())).append(amtStr);
        }

        qr.append("5802VN"); // Country Code

        if (memo != null && !memo.isEmpty()) {
            String addData = "08" + String.format("%02d", memo.length()) + memo;
            qr.append("62").append(String.format("%02d", addData.length())).append(addData);
        }

        // CRC
        qr.append("6304");
        String dataForCRC = qr.toString();
        String crc = calculateCRC16(dataForCRC);
        qr.append(crc);



        return qr.toString();
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



    private static String safeMemo(String memo) {
        return memo == null ? "NoMemo" : memo;
    }


    static BufferedImage generateQRCodeImage(String text, int width, int height, String fileName) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        // Đường dẫn tuyệt đối đến thư mục lưu mã QR
        String qrDirectory = "qr";
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
        return null;
    }

        private void displayQRCodeImage(String fileName) {
        logInfo("🔍 Đang kiểm tra sự tồn tại của file QR: " + fileName);

        File qrFile = new File(fileName);
        if (!qrFile.exists()) {
            logWarn("⚠️ File QR không tồn tại: " + fileName + ". Bỏ qua hiển thị.");
            return;
        }

        long fileSize = qrFile.length();
        logInfo("📁 Dung lượng file: " + fileSize + " bytes");

        try {
            Thread.sleep(100); // Mô phỏng loading
            logInfo("🖼️ [HIỂN THỊ QR]: " + fileName);
            for (int i = 0; i < 5; i++) {
                System.out.print("█");
                Thread.sleep(50);
            }
            System.out.println(" ✅");
        } catch (InterruptedException e) {
            logError("Lỗi khi mô phỏng hiển thị ảnh QR: " + e.getMessage());
        }

        logInfo("✅ File QR đã được xử lý thành công.");
    }

    private void logError(String s) {
    }

    private void logInfo(String s) {
        
    }


    private void logWarn(String message) {    }

        /**
     * Khởi tạo quá trình thanh toán đầy đủ và trả về CompletableFuture
     * để người gọi có thể xử lý kết quả thanh toán
     */
    public static CompletableFuture<Boolean> startPaymentProcess(int userId, int amount) {
        CompletableFuture<Boolean> paymentResult = new CompletableFuture<>();

        // Tạo mã giao dịch duy nhất
        String transactionCode = "PAY" + System.currentTimeMillis();

        // Tạo nội dung giao dịch
        String memo =transactionCode;

        try {
            // Lấy thông tin người dùng từ DB
            Connection conn = getConnection();
            String sql = "SELECT full_name, email FROM Users WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            String userName = "";
            if (rs.next()) {
                userName = rs.getString("full_name");
            }
            conn.close();

            // Tạo và hiển thị mã QR trong dialog có thời gian chờ và kiểm tra giao dịch
            showPaymentDialog(userId, SEPAY_ACCOUNT_NUMBER, SEPAY_BANK_CODE, memo, amount,
                    userName, transactionCode, paymentResult);

        } catch (SQLException e) {
            System.err.println("Lỗi khi truy vấn thông tin người dùng: " + e.getMessage());
            e.printStackTrace();
            paymentResult.completeExceptionally(e);
        }

        return paymentResult;
    }

    /**
     * Xử lý khi thanh toán thành công
     */

    /**
     * Tạo một panel thông tin với label và giá trị
     */
    private static JPanel createInfoRow(String labelText, String valueText,
                                        Font labelFont, Font valueFont, int height) {
        JPanel row = new JPanel();
        row.setLayout(new BorderLayout());
        row.setBackground(Color.WHITE);
        row.setPreferredSize(new Dimension(400, height));

        JLabel label = new JLabel(labelText);
        label.setFont(labelFont);
        label.setPreferredSize(new Dimension(120, height));

        JLabel value = new JLabel(valueText);
        value.setFont(valueFont);

        row.add(label, BorderLayout.WEST);
        row.add(value, BorderLayout.CENTER);

        return row;
    }

    /**
     * Format số tiền dạng tiền tệ
     */
    private static String formatCurrency(int amount) {
        return String.format("%d", amount).replace(",", ".");
    }


    /**
     * Lấy kết nối database
     */
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static void insertPayment(int orderId, int amount) throws SQLException {
        String sql = "INSERT INTO Payments (order_id, amount, status,method) VALUES (?, ?, 'Success','Bank Transfer')";
        try (Connection conn = DatabaseConfig.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, orderId);
            stmt.setDouble(2, amount);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Phương thức tiện ích để xử lý thanh toán khóa học
     *
     * @param userId ID của người dùng
     *               //     * @param courseId ID của khóa học
     * @return true nếu thanh toán thành công, false nếu thất bại
     */
    public static boolean processPaymentForCourse(int userId, int amount, List<CourseDetailDTO> list, boolean isCart) {
        return false;
    }

    /**
     * Hiển thị dialog thanh toán với QR code và mô phỏng thanh toán sau 10 giây
     */
    private static void showPaymentDialog(int userId, String accountNumber, String bankCode,
                                          String memo, int amount, String userName, String transactionCode,
                                          CompletableFuture<Boolean> paymentResult) {
//        String memo="abcD";

        try {
            // Tạo hình ảnh QR
            int qrSize = 250;
            // Tạo dữ liệu QR
            createQRCode(accountNumber, bankCode, memo, amount, qrSize);


            BufferedImage qrImage =  ImageIO.read(new File("qr/qrcode_"+memo+".png"));


            // Tạo dialog hiển thị QR và thông tin thanh toán
            JDialog paymentDialog = new JDialog((JFrame) null, "Thanh toán khóa học", true);
            paymentDialog.setSize(500, 650);
            paymentDialog.setLayout(new BorderLayout());
            paymentDialog.setLocationRelativeTo(null);
            paymentDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

            // Panel chứa QR
            JPanel qrPanel = new JPanel(new BorderLayout());
            qrPanel.setBackground(Color.WHITE);
            qrPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

            // Tiêu đề
            JLabel titleLabel = new JLabel("Quét mã để thanh toán", JLabel.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
            qrPanel.add(titleLabel, BorderLayout.NORTH);

            // QR code
            JLabel qrLabel = new JLabel(new ImageIcon(qrImage));
            qrLabel.setHorizontalAlignment(JLabel.CENTER);
            qrPanel.add(qrLabel, BorderLayout.CENTER);

            // Thông tin thanh toán
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BorderLayout());
            infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
            infoPanel.setBackground(Color.WHITE);

            JPanel detailsPanel = new JPanel();
            detailsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            detailsPanel.setBackground(Color.WHITE);

            // Khởi tạo font
            Font labelFont = new Font("Arial", Font.BOLD, 14);
            Font valueFont = new Font("Arial", Font.PLAIN, 14);
            int height = 25;

            // Row 1: Mã giao dịch
            JPanel row1 = createInfoRow("Mã giao dịch:", transactionCode, labelFont, valueFont, height);

            // Row 2: Khóa học
//            JPanel row2 = createInfoRow("Khóa học:", courseInfo, labelFont, valueFont, height);

            // Row 3: Học viên
            JPanel row3 = createInfoRow("Học viên:", userName, labelFont, valueFont, height);

            // Row 4: Số tiền
            JPanel row4 = createInfoRow("Số tiền:", formatCurrency(amount) + " VNĐ", labelFont, valueFont, height);

            // Row 5: Ngân hàng
            JPanel row5 = createInfoRow("Ngân hàng:", bankCode, labelFont, valueFont, height);

            // Row 6: Số tài khoản
            JPanel row6 = createInfoRow("Số tài khoản:", accountNumber, labelFont, valueFont, height);

            // Row 7: Nội dung CK
            JPanel row7 = createInfoRow("Nội dung CK:", memo, labelFont, valueFont, height);

            // Thêm các row vào panel
            JPanel infoRows = new JPanel();
            infoRows.setLayout(new BorderLayout());
            infoRows.setBackground(Color.WHITE);

            JPanel topRows = new JPanel();
            topRows.setLayout(new BorderLayout());
            topRows.setBackground(Color.WHITE);
            topRows.add(row1, BorderLayout.NORTH);

            JPanel row1_2 = new JPanel();
            row1_2.setLayout(new BorderLayout());
            row1_2.setBackground(Color.WHITE);
//            row1_2.add(row2, BorderLayout.NORTH);
            row1_2.add(row3, BorderLayout.CENTER);

            topRows.add(row1_2, BorderLayout.CENTER);

            JPanel middleRows = new JPanel();
            middleRows.setLayout(new BorderLayout());
            middleRows.setBackground(Color.WHITE);
            middleRows.add(row4, BorderLayout.NORTH);

            JPanel row5_6 = new JPanel();
            row5_6.setLayout(new BorderLayout());
            row5_6.setBackground(Color.WHITE);
            row5_6.add(row5, BorderLayout.NORTH);
            row5_6.add(row6, BorderLayout.CENTER);

            middleRows.add(row5_6, BorderLayout.CENTER);

            infoRows.add(topRows, BorderLayout.NORTH);
            infoRows.add(middleRows, BorderLayout.CENTER);
            infoRows.add(row7, BorderLayout.SOUTH);

            infoPanel.add(infoRows, BorderLayout.NORTH);

            // Progress bar và trạng thái
            JPanel statusPanel = new JPanel();
            statusPanel.setLayout(new BorderLayout());
            statusPanel.setBackground(Color.WHITE);
            statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

            JLabel statusLabel = new JLabel("Đang chờ thanh toán...", JLabel.CENTER);
            statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
            statusLabel.setForeground(new Color(0, 102, 204));

            JProgressBar progressBar = new JProgressBar();
            progressBar.setIndeterminate(true);
            progressBar.setPreferredSize(new Dimension(400, 15));

            statusPanel.add(statusLabel, BorderLayout.NORTH);
            statusPanel.add(progressBar, BorderLayout.CENTER);

            infoPanel.add(statusPanel, BorderLayout.CENTER);

            // Nút hủy
            JPanel buttonPanel = new JPanel();
            buttonPanel.setBackground(Color.WHITE);

            JButton cancelButton = new JButton("Hủy thanh toán");
            cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
            cancelButton.setBackground(new Color(220, 53, 69));
            cancelButton.setForeground(Color.WHITE);
            buttonPanel.add(cancelButton);

            infoPanel.add(buttonPanel, BorderLayout.SOUTH);

            // Thêm các panel vào dialog
            paymentDialog.add(qrPanel, BorderLayout.NORTH);
            paymentDialog.add(infoPanel, BorderLayout.CENTER);
            Thread checkerThread = new Thread(() -> {
                boolean found = false;
                int maxRetry = 20; // chạy tối đa 20 lần = 100s
                int retry = 0;

                while (!found && retry < maxRetry) {
                    found = kiemTraLichSuGiaoDich(memo, amount);

                    if (found) {
                        SwingUtilities.invokeLater(() -> {
                            statusLabel.setText("Thanh toán thành công");
                            statusLabel.setForeground(new Color(40, 167, 69));
                            progressBar.setIndeterminate(false);
                            progressBar.setValue(100);

                            JOptionPane.showMessageDialog(paymentDialog,
                                    "Thanh toán thành công! Số tiền " + formatCurrency(amount) + " VNĐ đã được ghi nhận.",
                                    "Thanh toán thành công",
                                    JOptionPane.INFORMATION_MESSAGE);

                            paymentDialog.dispose();
                        });
                        paymentResult.complete(true);
                        break;
                    } else {
                        try {
                            Thread.sleep(5000); // chờ 5s
                        } catch (InterruptedException e) {
                            break;
                        }
                        retry++;
                    }
                }

                if (!found) {
                    paymentResult.complete(false); // Sau khi thử max lần vẫn fail
                }
            });
            checkerThread.start();

            // Nút huỷ
            cancelButton.addActionListener(e -> {
                checkerThread.interrupt(); // Dừng vòng lặp kiểm tra
                paymentResult.complete(false);
                paymentDialog.dispose();
            });

            // Khi đóng cửa sổ
            paymentDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    checkerThread.interrupt();
                    paymentResult.complete(false);
                }
            });

            // Hiển thị dialog
            SwingUtilities.invokeLater(() -> {
                paymentDialog.setVisible(true);
            });

        } catch (IOException e) {
            System.err.println("Lỗi khi tạo mã QR: " + e.getMessage());
            e.printStackTrace();
            paymentResult.completeExceptionally(e);
        }
    }

    // Kiểm tra lịch sử giao dịch và cập nhật số dư nếu tìm thấy
    public static boolean kiemTraLichSuGiaoDich(String tuKhoaNoiDung,  int amount) {
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

            if (statusCode != 200) {
                throw new IOException("Lỗi khi lấy lịch sử giao dịch. Status code: " + statusCode + ", Response: " + response.body());
            }

            JSONObject jsonResponse = new JSONObject(response.body());
            JSONArray transactions = jsonResponse.getJSONArray("transactions");
            for (int i = 0; i < transactions.length(); i++) {
                JSONObject transaction = transactions.getJSONObject(i);
                String description = transaction.getString("transaction_content");
                String amountInStr = transaction.getString("amount_in");
                int amountIn = (int) Double.parseDouble(amountInStr); // vì amount là int

                if (description.contains(tuKhoaNoiDung) && amountIn == amount) {
                    System.out.println("Tìm thấy giao dịch: " + transaction.toString());
                    return true;
                }

            }

        } catch (IOException e) {
            System.err.println("Lỗi kết nối hoặc đọc phản hồi (SePay - Transactions): " + e.getMessage());
            e.printStackTrace();

        } catch (InterruptedException e) {
            System.err.println("Yêu cầu bị gián đoạn (SePay - Transactions): " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Phương thức main để test chức năng
     */
    public static void main(String[] args) {
        kiemTraLichSuGiaoDich("COURSE_PAY1746408050140_5",2000);
//        PaymentService.processPaymentForCourse(3, 100000, list);

    }
}