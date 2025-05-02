package com.example.coursemanagement.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
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
import javax.swing.Timer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.example.coursemanagement.Controllers.Client.CartController.CartController;
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

public class PaymentService {
    private Alerts alerts = new Alerts();
    private static LogService logService = new LogService();

    // Thiết lập môi trường thử nghiệm
    private static final String SEPAY_ACCOUNT_NUMBER = "1518893947588";
    private static final String SEPAY_BANK_CODE = "MBBank";
    private static PurchaseCourseService purchaseCourseService = new PurchaseCourseService();

    // Đường dẫn kết nối database
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=IT_Course_Management;encrypt=true;trustServerCertificate=true";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "18082005";

    // Thời gian đợi mô phỏng thanh toán (mili giây)
    private static final int PAYMENT_SIMULATION_TIME = 5000; // 10 giây

    /**
     * Khởi tạo quá trình thanh toán đầy đủ và trả về CompletableFuture
     * để người gọi có thể xử lý kết quả thanh toán
     */
    public static CompletableFuture<Boolean> startPaymentProcess(int userId, double amount) {
        CompletableFuture<Boolean> paymentResult = new CompletableFuture<>();

        // Tạo mã giao dịch duy nhất
        String transactionCode = "PAY" + System.currentTimeMillis() + "_" + userId;

        // Tạo nội dung giao dịch
        String memo = "COURSE_" + transactionCode;

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
     * Hiển thị dialog thanh toán với QR code và mô phỏng thanh toán sau 10 giây
     */
    private static void showPaymentDialog(int userId, String accountNumber, String bankCode,
                                          String memo, double amount, String userName, String transactionCode,
                                          CompletableFuture<Boolean> paymentResult) {

        try {
            // Tạo dữ liệu QR
            String qrData = generateVietQRData(accountNumber, bankCode, amount, memo);

            // Tạo hình ảnh QR
            int qrSize = 250;
            BufferedImage qrImage = generateQRCodeImage(qrData, qrSize, qrSize,
                    "qrcode" + ".png");

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
            boolean close = false;
            // Timer để mô phỏng thanh toán thành công sau 1 giây
            Timer timer = new Timer(PAYMENT_SIMULATION_TIME, new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    ((Timer) e.getSource()).stop(); // Dừng timer
                    // Cập nhật giao diện
                    statusLabel.setText("Thanh toán thành công");
                    statusLabel.setForeground(new Color(40, 167, 69));
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(100);


                    // Hoàn thành CompletableFuture
                    paymentResult.complete(true);

                    // Hiển thị thông báo thành công và đóng dialog
                    SwingUtilities.invokeLater(() -> {
                        Alerts alerts1 = new Alerts();
                        JOptionPane.showMessageDialog(paymentDialog,
                                "Thanh toán thành công! Số tiền " + formatCurrency(amount) + " VNĐ đã được ghi nhận.",
                                "Thanh toán thành công",
                                JOptionPane.INFORMATION_MESSAGE);
                        paymentDialog.dispose();
                    });
                }
            });
            timer.setRepeats(false); // Chỉ chạy 1 lần
            timer.start();
            // Xử lý nút hủy
            cancelButton.addActionListener(e -> {
                timer.stop();
                paymentResult.complete(false);
                paymentDialog.dispose();
            });

            // Xử lý khi đóng dialog
            paymentDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    timer.stop();
                    paymentResult.complete(false);
                }
            });

            // Hiển thị dialog và bắt đầu timer
            SwingUtilities.invokeLater(() -> {
                paymentDialog.setVisible(true);
            });

        } catch (WriterException | IOException e) {
            System.err.println("Lỗi khi tạo mã QR: " + e.getMessage());
            e.printStackTrace();
            paymentResult.completeExceptionally(e);
        }
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
    private static String formatCurrency(double amount) {
        return String.format("%,.0f", amount).replace(",", ".");
    }


    /**
     * Tạo chuỗi VietQR theo chuẩn chính thức
     */
    public static String generateVietQRData(String accountNumber, String bankCode, double amount, String memo) {
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

        String amountStr = String.format("%.0f", amount);
        qrData.append(String.format("54%02d%s", amountStr.length(), amountStr));

        String purpose = String.format("08%02d%s", memo.length(), memo);
        qrData.append(purpose);

        qrData.append("6304");

        String crc = calculateCRC16(qrData.toString());
        qrData.append(crc);

        return qrData.toString();
    }

    /**
     * Tính CRC16 theo chuẩn VietQR
     */
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

    /**
     * Tạo hình ảnh QR và lưu vào file, trả về BufferedImage
     */
    private static BufferedImage generateQRCodeImage(String text, int width, int height, String fileName) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        // Đường dẫn tuyệt đối đến thư mục lưu mã QR
        String qrDirectory = "A:\\document\\BTL\\Java\\javaApp\\qr";
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

    /**
     * Lấy kết nối database
     */
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static void insertPayment(int orderId, double amount) throws SQLException {
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
     * @param userId   ID của người dùng
     * @param courseId ID của khóa học
     * @return true nếu thanh toán thành công, false nếu thất bại
     */
    public static boolean processPaymentForCourse(int userId, double amount, List<CourseDetailDTO> list, boolean isCart) {
        try {
            // Lấy thông tin khóa học từ database


            // Thực hiện thanh toán và đợi kết quả
            CompletableFuture<Boolean> paymentFuture = PaymentService.startPaymentProcess(userId, amount);

            // Đợi kết quả thanh toán (blocking call)
            boolean paymentSuccess = paymentFuture.get(); // lưu ý: đợi kết quả

            if (paymentSuccess) {
                logService.createLog(SessionManager.getInstance().getUser().getUserId(), "Học viên " + SessionManager.getInstance().getUser().getFullname() + " đã mua khóa học");
                // Xử lý khi thanh toán thành công
                // Xử lý logic thanh toán thành công
//                handleSuccessfulPayment(userId, amount, courseInfo);
                purchaseCourseService.setTotal(amount);
                int orderId = purchaseCourseService.purchaseCoursesFromCart(userId, list);
                if (orderId != -1) {
                    insertPayment(orderId, amount);
                    if (isCart) {

                        CartService cartService = new CartService();
                        cartService.deleteAllCartItem(SessionManager.getInstance().getUser().getUserId());
                        SessionManager.getInstance().setCartSize();
                        ClientMenuController.getInstance().refreshCartSize();
                    }
                    System.out.println("add thanh cong");
                } else {
                    System.out.println("add that bai");
                }
            }

            return paymentSuccess;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Phương thức main để test chức năng
     */
    public static void main(String[] args) {
        List<CourseDetailDTO> list = new ArrayList<>();
//        PaymentService.processPaymentForCourse(3, 100000, list);

    }
}