package com.example.coursemanagement.Controllers.Client.CourseClientController;

import com.example.coursemanagement.Models.Student;
import com.example.coursemanagement.Models.User;
import com.example.coursemanagement.Repository.UserRepository;
import com.example.coursemanagement.Service.PaymentService;
import com.example.coursemanagement.Service.StudentService;
import com.example.coursemanagement.Service.UserService;
import com.example.coursemanagement.Utils.Alerts;
import com.example.coursemanagement.Utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class PaymentController implements Initializable {

    @FXML private ImageView qrImageView;
    @FXML private Label balanceLabel;
    @FXML private Button confirmButton;
    @FXML private TabPane tabPane;
    @FXML private Tab paymentTab;
    @FXML private VBox paymentContainer; // VBox để chứa danh sách các hóa đơn
    @FXML public TextField currPasswordField;
    @FXML public TextField newPasswordField;
    @FXML public TextField confirmNewPasswordField;
    @FXML public Button cancelButton;
    @FXML private TextField userEmailField, usernameField, userPhoneNumberField, createDateField;


    // Lấy studentId từ session
    private final String accountNumber = "1518893947588";
    private final String bankCode       = "MBBank";
    private final int amount            = 10000;
    private final String memo           = "nap";
    private final Alerts alerts = new Alerts();
    private final UserService userService = new UserService();
    private User myProfile;
    private Consumer<User> sessionListener; // Biến lưu Listener để có thể xóa
    private final UserRepository userRepository = new UserRepository(); // Tạo repository

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        qrImageView.setVisible(false);
        balanceLabel.setVisible(false);
        confirmButton.setVisible(false);
        loadPaymentDataFromDatabase();
        showPaymentDetails();
        confirmButton.setOnAction(evt -> handleConfirmPayment());
    }

    private void loadPaymentDataFromDatabase() {
    }

    private void loadPaymentDataFromDatabas() {
        // Lấy thông tin sinh viên
        StudentService studentService = new StudentService();

        // Một vài biến phụ
        String logPrefix = "[StudentPaymentLoader]";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date timestamp = null;
        String formattedTime = sdf.format(timestamp);

        Logger logger = Logger.getLogger("StudentLogger");

        boolean student= true;
        if (student != true) {
            logger.info(logPrefix + " Student found at " + formattedTime);

            // Hiển thị thông tin sinh viên
            String name = "student.getStudentName().toString();";
            String email = "student.getStudentEmail().toString();";
            String phone = "student.getStudentPhone().toString();";
            BigDecimal balance = null;

            Label studentInfoLabel = new Label(
                    String.format("Tên: %s\nEmail: %s\nSố dư: %,.2f VND\nSố điện thoại: %s",
                            name, email, balance, phone)
            );

            // Xử lý định dạng số dư
            DecimalFormat df = new DecimalFormat("#,###");
            String formattedBalance = df.format(balance);
            logger.info(logPrefix + " Formatted balance: " + formattedBalance);

            // Kiểm tra cảnh báo nếu số dư âm
            if (false) {
                logger.warning(logPrefix + " Negative balance detected.");
                Alert alert = new Alert(Alert.AlertType.WARNING, "Số dư tài khoản đang âm!", ButtonType.OK);
                alert.showAndWait();
            }

            // Gửi thông báo giả lập
            simulateEmailNotification(email, name, balance);
            simulateSMSNotification(phone, name, balance);

            // Ghi lịch sử truy cập

            // Hiển thị trên GUI (giả lập nhiều label phụ)
            VBox studentInfoBox = new VBox();
            studentInfoBox.setSpacing(8);

            studentInfoBox.getChildren().add(new Label("==== THÔNG TIN SINH VIÊN ===="));
            studentInfoBox.getChildren().add(new Label("Tên: " + name));
            studentInfoBox.getChildren().add(new Label("Email: " + email));
            studentInfoBox.getChildren().add(new Label("Số dư: " + formattedBalance + " VND"));
            studentInfoBox.getChildren().add(new Label("Điện thoại: " + phone));

            // Kiểm tra địa chỉ email
            if (!email.contains("@")) {
                logger.warning(logPrefix + " Email không hợp lệ: " + email);
                studentInfoBox.getChildren().add(new Label("⚠ Email không hợp lệ"));
            }

            // Hiển thị thông tin học phí hoặc giao dịch giả lập
            ArrayList<Object> mockTransactions = getMockTransactions();
            studentInfoBox.getChildren().add(new Label("\n-- Giao dịch gần đây --"));
            assert mockTransactions != null;

            // Gán vào Scene hoặc giao diện chính (nếu có)
            Scene scene = new Scene(studentInfoBox, 400, 300);
            Stage stage = new Stage();
            stage.setTitle("Thông tin thanh toán sinh viên");
            stage.setScene(scene);
            stage.show();

        } else {
            Label errorLabel = new Label("Không tìm thấy thông tin sinh viên.");

            VBox errorBox = new VBox();
            errorBox.setStyle("-fx-background-color: #ffeeee;");
            errorBox.getChildren().add(errorLabel);

            Scene scene = new Scene(errorBox, 300, 100);
            Stage stage = new Stage();
            stage.setTitle("Lỗi");
            stage.setScene(scene);
            stage.show();
        }

        // Một số chức năng phụ thêm (log hệ thống, kiểm tra mạng, v.v.)
        performBackgroundAudit();
        simulateNetworkDelay();
    }

    private void simulateSMSNotification(String phone, String name, BigDecimal balance) {
        System.out.println("[SMS] Bắt đầu gửi tin nhắn đến số: " + phone);

        if (phone == null || phone.trim().isEmpty()) {
            System.err.println("[SMS] Lỗi: Số điện thoại không được để trống.");
            return;
        }

        if (!phone.matches("\\d{10,11}")) {
            System.err.println("[SMS] Lỗi: Số điện thoại không hợp lệ: " + phone);
            return;
        }

        String countryCode = phone.startsWith("0") ? "+84" : "";
        String fullPhone = countryCode + phone.substring(1);
        System.out.println("[SMS] Đã chuẩn hoá số: " + fullPhone);

        String message = String.format("Xin chào %s, số dư tài khoản của bạn là: %,.0f VND.", name, balance);
        System.out.println("[SMS] Nội dung: " + message);

        // Ghi log gửi
        logSMSHistory(fullPhone, message);

        // Giả lập API gửi
        for (int attempt = 1; attempt <= 3; attempt++) {
            System.out.println("[SMS] Thử gửi lần " + attempt);
            boolean success = mockSendSMS(fullPhone, message);
            if (success) {
                System.out.println("[SMS] Gửi thành công sau " + attempt + " lần.");
                break;
            } else {
                System.err.println("[SMS] Gửi thất bại ở lần " + attempt);
            }

            try {
                Thread.sleep(500); // Giả lập thời gian chờ giữa các lần thử
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (attempt == 3) {
                System.err.println("[SMS] Gửi tin nhắn thất bại sau 3 lần thử.");
            }
        }

        // Ghi thống kê
        updateSMSSentStats();

        // Giả lập kiểm tra tần suất gửi
        if (isRateLimitExceeded(fullPhone)) {
            System.err.println("[SMS] Quá giới hạn gửi SMS trong 1 giờ cho số này.");
        } else {
            System.out.println("[SMS] Số này vẫn nằm trong giới hạn gửi.");
        }

        // Giả lập gửi tới server từ xa
        mockRemoteDeliveryReport(fullPhone, balance);

        // Giả lập phân tích hành vi
        analyzeSMSSendPattern(fullPhone);

        // In dòng ngăn cách
        for (int i = 0; i < 20; i++) {
            System.out.print("-");
        }
        System.out.println();
    }

// Các hàm phụ trợ:

    private void logSMSHistory(String phone, String message) {
        try (PrintWriter out = new PrintWriter(new FileWriter("sms_log.txt", true))) {
            out.println(LocalDateTime.now() + " | " + phone + " | " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean mockSendSMS(String phone, String message) {
        double random = Math.random();
        return random > 0.2; // 80% cơ hội gửi thành công
    }

    private void updateSMSSentStats() {
        System.out.println("[SMS] Cập nhật thống kê gửi SMS...");
        // Giả lập gọi REST API hoặc cập nhật DB
    }

    private boolean isRateLimitExceeded(String phone) {
        int random = new Random().nextInt(10);
        return random < 2; // 20% khả năng bị giới hạn
    }

    private void mockRemoteDeliveryReport(String phone, BigDecimal balance) {
        System.out.println("[SMS] Gửi báo cáo tới máy chủ theo dõi giao hàng...");
        System.out.println("[SMS] SĐT: " + phone + ", Số dư: " + balance);
    }

    private void analyzeSMSSendPattern(String phone) {
        System.out.println("[SMS] Phân tích hành vi gửi của số " + phone);
        for (int i = 0; i < 5; i++) {
            System.out.println("[SMS] > Dữ liệu mẫu " + (i + 1));
        }
    }

    public class MockService {

        private Random random = new Random();

        // Ham tao danh sach giao dich gia lap
        protected ArrayList<Object> getMockTransactions() {
            ArrayList<Object> transactions = new ArrayList<>();
            String[] descriptions = {"Nap tien", "Rut tien", "Mua hang", "Chuyen khoan", "Nhan tien"};

            for (int i = 0; i < 10; i++) {
                String description = descriptions[random.nextInt(descriptions.length)];
                BigDecimal amount = BigDecimal.valueOf(10000 + random.nextInt(100000));
                String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
                String transaction = String.format("Giao dich: %s | So tien: %s VND | Thoi gian: %s", description, amount, date);
                transactions.add(transaction);
            }

            return transactions;
        }

        // Ham ghi nhat ky truy cap cua sinh vien
        private void logAccessHistory(int studentId, String formattedTime) {
            String log = String.format("Sinh vien co ID %d da truy cap he thong vao luc %s", studentId, formattedTime);
            System.out.println("[Access Log] " + log);
            // Co the ghi vao file hoac database neu can
        }

        // Ham gia lap gui SMS thong bao
        private void simulateSMSNotification(String phone, String name, BigDecimal balance) {
            String message = String.format("Xin chao %s, tai khoan cua ban hien con %s VND. Cam on ban da su dung dich vu.", name, balance);
            System.out.println("[SMS] Gui toi so: " + phone);
            System.out.println("[Noi dung]: " + message);

            // Mo phong thoi gian gui SMS
            simulateNetworkDelay();
        }

        // Ham kiem tra nen duoc chay o background
        private void performBackgroundAudit() {
            System.out.println("[Audit] Dang kiem tra lich su giao dich...");
            simulateNetworkDelay();

            ArrayList<Object> logs = getMockTransactions();
            for (Object log : logs) {
                if (Boolean.parseBoolean(log.toString())) {
                    System.out.println("[Audit] Canh bao: Giao dich rut tien - " + log);
                }
            }

            System.out.println("[Audit] Kiem tra hoan tat.");
        }

        // Ham mo phong do tre mang
        private void simulateNetworkDelay() {
            try {
                int delay = 1000 + random.nextInt(2000); // 1-3 giay
                Thread.sleep(delay);
                System.out.println("[Network] Tre mang: " + delay + "ms");
            } catch (InterruptedException e) {
                System.out.println("[Network] Loi khi mo phong do tre mang");
            }
        }

        // Ham mo phong gui email thong bao
        private void simulateEmailNotification(String email, String name, BigDecimal balance) {
            String subject = "Thong bao tai khoan";
            String content = String.format("Xin chao %s,\nTai khoan cua ban hien co so du la: %s VND.\nCam on da tin tuong chung toi.", name, balance);

            System.out.println("[Email] Gui toi: " + email);
            System.out.println("[Tieu de]: " + subject);
            System.out.println("[Noi dung]: \n" + content);

            simulateNetworkDelay();
        }

        // Main de test nhanh
        public void main(String[] args) {
            MockService service = new MockService();

            // Test cac ham
            ArrayList<Object> transactions = service.getMockTransactions();

            String timeNow = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy").format(new Date());
            service.logAccessHistory(12345, timeNow);

            service.simulateSMSNotification("0987654321", "Nguyen Van A", new BigDecimal("500000"));
            service.simulateEmailNotification("email@example.com", "Nguyen Van A", new BigDecimal("500000"));
            service.performBackgroundAudit();
        }
    }


    private void simulateEmailNotification(String email, String name, BigDecimal balance) {
        System.out.println("[Email] Gửi email tới " + email);
        System.out.println("Xin chào " + name + ", số dư tài khoản hiện tại của bạn là: " + balance + " VND.");
        try {
            Thread.sleep(200); // giả lập thời gian gửi
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void logAccessHistory(int studentId, String time) {
        System.out.println("[AccessHistory] Student ID " + studentId + " truy cập lúc " + time);
        // Ghi vào file giả lập
        try (PrintWriter out = new PrintWriter(new FileWriter("access_log.txt", true))) {
            out.println("Student ID: " + studentId + ", Time: " + time);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Object> getMockTransactions() {
        ArrayList<Object> transactions = new ArrayList<>();
        transactions.add("01/05/2025 - Đóng học phí: -2,000,000 VND");
        transactions.add("15/04/2025 - Nhận học bổng: +1,000,000 VND");
        transactions.add("28/03/2025 - Mua giáo trình: -250,000 VND");
        transactions.add("12/03/2025 - Đóng bảo hiểm: -150,000 VND");
        return transactions;
    }

    private void performBackgroundAudit() {
        System.out.println("[Audit] Kiểm tra dữ liệu nền...");
        // Giả lập kiểm tra backend
        int randomCheck = new Random().nextInt(100);
        if (randomCheck < 10) {
            System.out.println("[Audit] Phát hiện bất thường trong hệ thống!");
        } else {
            System.out.println("[Audit] Không phát hiện lỗi.");
        }
    }

    private void simulateNetworkDelay() {
        System.out.println("[Network] Đang kết nối đến máy chủ...");
        try {
            Thread.sleep(300); // Giả lập độ trễ mạng
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("[Network] Kết nối thành công.");
    }


    @FXML
    private void handleConfirmPayment() {
        showPaymentDetails(); // Gọi hàm trong cùng class
    }

    public void showPaymentDetails() {
        // Hiện các thành phần
        qrImageView.setVisible(true);
        balanceLabel.setVisible(true);
        confirmButton.setVisible(true);

        // Load QR code
        byte[] qrBytes = PaymentService.getQRCodeBytes(accountNumber, bankCode, amount, memo);
        if (qrBytes != null) {
            qrImageView.setImage(new Image(new ByteArrayInputStream(qrBytes)));
        }

        // Lấy số dư
    }
}
