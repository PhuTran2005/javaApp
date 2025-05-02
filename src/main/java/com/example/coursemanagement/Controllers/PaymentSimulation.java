package com.example.coursemanagement.Controllers;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PaymentSimulation {

    // Interface cho phương thức thanh toán
    public interface PaymentMethod {
        boolean processPayment(double amount, Map<String, String> paymentDetails);
    }

    // Lớp lưu trữ thông tin giao dịch
    public static class Transaction {
        private String transactionId;
        private double amount;
        private String paymentMethod;
        private LocalDateTime timestamp;
        private String status;
        private String courseId;
        private String userId;

        public Transaction(String transactionId, double amount, String paymentMethod,
                           LocalDateTime timestamp, String status, String courseId, String userId) {
            this.transactionId = transactionId;
            this.amount = amount;
            this.paymentMethod = paymentMethod;
            this.timestamp = timestamp;
            this.status = status;
            this.courseId = courseId;
            this.userId = userId;
        }

        // Getters và setters
        public String getTransactionId() { return transactionId; }
        public double getAmount() { return amount; }
        public String getPaymentMethod() { return paymentMethod; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public String getStatus() { return status; }
        public String getCourseId() { return courseId; }
        public String getUserId() { return userId; }

        @Override
        public String toString() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            return "Mã giao dịch: " + transactionId +
                    "\nSố tiền: " + amount + " VND" +
                    "\nPhương thức: " + paymentMethod +
                    "\nThời gian: " + timestamp.format(formatter) +
                    "\nTrạng thái: " + status +
                    "\nMã khóa học: " + courseId +
                    "\nMã người dùng: " + userId;
        }
    }

    // Lớp quản lý thanh toán
    public static class PaymentProcessor {
        private Map<String, PaymentMethod> paymentMethods;
        private Map<String, Transaction> transactions;

        public PaymentProcessor() {
            this.paymentMethods = new HashMap<>();
            this.transactions = new HashMap<>();

            // Đăng ký các phương thức thanh toán
            registerPaymentMethod("MOMO", new MomoPayment());
            registerPaymentMethod("ZALOPAY", new ZaloPayPayment());
            registerPaymentMethod("VNPAY", new VNPayPayment());
            registerPaymentMethod("CREDIT_CARD", new CreditCardPayment());
            registerPaymentMethod("BANK_TRANSFER", new BankTransferPayment());
        }

        public void registerPaymentMethod(String name, PaymentMethod method) {
            paymentMethods.put(name, method);
        }

        public Transaction processPayment(String methodName, double amount, Map<String, String> paymentDetails,
                                          String courseId, String userId) {
            PaymentMethod method = paymentMethods.get(methodName);
            if (method == null) {
                throw new IllegalArgumentException("Phương thức thanh toán không được hỗ trợ: " + methodName);
            }

            String transactionId = generateTransactionId();
            LocalDateTime timestamp = LocalDateTime.now();
            String status = "PENDING";

            // Tạo bản ghi giao dịch trước
            Transaction transaction = new Transaction(
                    transactionId, amount, methodName, timestamp, status, courseId, userId
            );

            // Lưu giao dịch
            transactions.put(transactionId, transaction);

            // Xử lý thanh toán
            boolean success = method.processPayment(amount, paymentDetails);

            // Cập nhật trạng thái giao dịch
            if (success) {
                transaction = new Transaction(
                        transactionId, amount, methodName, timestamp, "SUCCESS", courseId, userId
                );
            } else {
                transaction = new Transaction(
                        transactionId, amount, methodName, timestamp, "FAILED", courseId, userId
                );
            }

            // Cập nhật giao dịch trong bộ nhớ
            transactions.put(transactionId, transaction);

            return transaction;
        }

        private String generateTransactionId() {
            Random random = new Random();
            return "TXN" + System.currentTimeMillis() + random.nextInt(1000);
        }

        public Transaction getTransaction(String transactionId) {
            return transactions.get(transactionId);
        }
    }

    // Các lớp triển khai phương thức thanh toán
    public static class MomoPayment implements PaymentMethod {
        @Override
        public boolean processPayment(double amount, Map<String, String> paymentDetails) {
            // Mô phỏng xác minh thông tin MoMo
            String phoneNumber = paymentDetails.get("phoneNumber");
            String otp = paymentDetails.get("otp");

            if (phoneNumber == null || phoneNumber.length() != 10 || !phoneNumber.startsWith("0")) {
                return false;
            }

            if (otp == null || otp.length() != 6) {
                return false;
            }

            // Mô phỏng xử lý giao dịch (thành công 80% thời gian)
            return Math.random() < 0.8;
        }
    }

    public static class ZaloPayPayment implements PaymentMethod {
        @Override
        public boolean processPayment(double amount, Map<String, String> paymentDetails) {
            String accountId = paymentDetails.get("accountId");
            String password = paymentDetails.get("password");

            if (accountId == null || accountId.isEmpty()) {
                return false;
            }

            if (password == null || password.length() < 6) {
                return false;
            }

            // Mô phỏng xử lý giao dịch (thành công 85% thời gian)
            return Math.random() < 0.85;
        }
    }

    public static class VNPayPayment implements PaymentMethod {
        @Override
        public boolean processPayment(double amount, Map<String, String> paymentDetails) {
            String bankCode = paymentDetails.get("bankCode");
            String accountNumber = paymentDetails.get("accountNumber");

            if (bankCode == null || bankCode.length() != 3) {
                return false;
            }

            if (accountNumber == null || accountNumber.length() < 10) {
                return false;
            }

            // Mô phỏng xử lý giao dịch (thành công 90% thời gian)
            return Math.random() < 0.9;
        }
    }

    public static class CreditCardPayment implements PaymentMethod {
        @Override
        public boolean processPayment(double amount, Map<String, String> paymentDetails) {
            String cardNumber = paymentDetails.get("cardNumber");
            String expiryDate = paymentDetails.get("expiryDate");
            String cvv = paymentDetails.get("cvv");

            if (cardNumber == null || cardNumber.length() != 16) {
                return false;
            }

            if (expiryDate == null || expiryDate.length() != 5) { // MM/YY format
                return false;
            }

            if (cvv == null || cvv.length() != 3) {
                return false;
            }

            // Mô phỏng xử lý giao dịch (thành công 95% thời gian)
            return Math.random() < 0.95;
        }
    }

    public static class BankTransferPayment implements PaymentMethod {
        @Override
        public boolean processPayment(double amount, Map<String, String> paymentDetails) {
            String accountNumber = paymentDetails.get("accountNumber");
            String bankName = paymentDetails.get("bankName");

            if (accountNumber == null || accountNumber.length() < 10) {
                return false;
            }

            if (bankName == null || bankName.isEmpty()) {
                return false;
            }

            // Mô phỏng xử lý giao dịch (thành công 75% thời gian)
            return Math.random() < 0.75;
        }
    }

    // Giao diện thanh toán
    public static class PaymentUI extends Application {
        private PaymentProcessor paymentProcessor = new PaymentProcessor();
        private String courseId = "CS101";
        private String userId = "USER123";
        private double coursePrice = 1500000.0; // 1,500,000 VND

        @Override
        public void start(Stage primaryStage) {
            primaryStage.setTitle("Thanh toán khóa học");

            // Tạo giao diện thanh toán
            VBox root = new VBox(10);
            root.setPadding(new Insets(20));

            // Thông tin khóa học
            Label courseInfoLabel = new Label("Khóa học: Lập trình Java căn bản");
            Label priceLabel = new Label(String.format("Giá: %.0f VND", coursePrice));

            // Phương thức thanh toán
            Label paymentMethodLabel = new Label("Chọn phương thức thanh toán:");
            ComboBox<String> paymentMethodCombo = new ComboBox<>(
                    FXCollections.observableArrayList(
                            "MOMO", "ZALOPAY", "VNPAY", "CREDIT_CARD", "BANK_TRANSFER"
                    )
            );
            paymentMethodCombo.setValue("MOMO");

            // Container cho các form thanh toán
            VBox paymentFormContainer = new VBox(10);

            // Ban đầu hiển thị form MoMo
            GridPane momoForm = createMomoForm();
            paymentFormContainer.getChildren().add(momoForm);

            // Sự kiện thay đổi phương thức thanh toán
            paymentMethodCombo.setOnAction(e -> {
                paymentFormContainer.getChildren().clear();

                switch (paymentMethodCombo.getValue()) {
                    case "MOMO":
                        paymentFormContainer.getChildren().add(createMomoForm());
                        break;
                    case "ZALOPAY":
                        paymentFormContainer.getChildren().add(createZaloPayForm());
                        break;
                    case "VNPAY":
                        paymentFormContainer.getChildren().add(createVNPayForm());
                        break;
                    case "CREDIT_CARD":
                        paymentFormContainer.getChildren().add(createCreditCardForm());
                        break;
                    case "BANK_TRANSFER":
                        paymentFormContainer.getChildren().add(createBankTransferForm());
                        break;
                }
            });

            // Nút thanh toán
            Button payButton = new Button("Thanh toán");
            payButton.setOnAction(e -> {
                processSelectedPayment(paymentMethodCombo.getValue(), paymentFormContainer);
            });

            // Khu vực hiển thị kết quả giao dịch
            TextArea resultArea = new TextArea();
            resultArea.setEditable(false);
            resultArea.setPrefHeight(200);

            root.getChildren().addAll(
                    courseInfoLabel, priceLabel,
                    new Separator(),
                    paymentMethodLabel, paymentMethodCombo,
                    paymentFormContainer,
                    payButton,
                    new Separator(),
                    new Label("Kết quả giao dịch:"),
                    resultArea
            );

            Scene scene = new Scene(root, 500, 650);
            primaryStage.setScene(scene);
            primaryStage.show();

            // Function to process payment based on selected method
            payButton.setOnAction(e -> {
                Map<String, String> paymentDetails = new HashMap<>();

                // Lấy thông tin chi tiết thanh toán từ form
                switch (paymentMethodCombo.getValue()) {
                    case "MOMO":
                        TextField momoPhone = (TextField) findNodeByID(paymentFormContainer, "momoPhone");
                        TextField momoOtp = (TextField) findNodeByID(paymentFormContainer, "momoOtp");
                        paymentDetails.put("phoneNumber", momoPhone.getText());
                        paymentDetails.put("otp", momoOtp.getText());
                        break;
                    case "ZALOPAY":
                        TextField zaloId = (TextField) findNodeByID(paymentFormContainer, "zaloId");
                        PasswordField zaloPass = (PasswordField) findNodeByID(paymentFormContainer, "zaloPass");
                        paymentDetails.put("accountId", zaloId.getText());
                        paymentDetails.put("password", zaloPass.getText());
                        break;
                    case "VNPAY":
                        ComboBox<String> bankCombo = (ComboBox<String>) findNodeByID(paymentFormContainer, "bankCombo");
                        TextField vnpayAccount = (TextField) findNodeByID(paymentFormContainer, "vnpayAccount");
                        paymentDetails.put("bankCode", bankCombo.getValue().substring(0, 3));
                        paymentDetails.put("accountNumber", vnpayAccount.getText());
                        break;
                    case "CREDIT_CARD":
                        TextField cardNumber = (TextField) findNodeByID(paymentFormContainer, "cardNumber");
                        TextField expiryDate = (TextField) findNodeByID(paymentFormContainer, "expiryDate");
                        TextField cvv = (TextField) findNodeByID(paymentFormContainer, "cvv");
                        paymentDetails.put("cardNumber", cardNumber.getText());
                        paymentDetails.put("expiryDate", expiryDate.getText());
                        paymentDetails.put("cvv", cvv.getText());
                        break;
                    case "BANK_TRANSFER":
                        TextField bankName = (TextField) findNodeByID(paymentFormContainer, "bankName");
                        TextField accountNumber = (TextField) findNodeByID(paymentFormContainer, "accountNumber");
                        paymentDetails.put("bankName", bankName.getText());
                        paymentDetails.put("accountNumber", accountNumber.getText());
                        break;
                }

                // Xử lý thanh toán
                try {
                    Transaction transaction = paymentProcessor.processPayment(
                            paymentMethodCombo.getValue(),
                            coursePrice,
                            paymentDetails,
                            courseId,
                            userId
                    );

                    // Hiển thị kết quả
                    resultArea.setText(transaction.toString());

                    // Thông báo xác nhận
                    if ("SUCCESS".equals(transaction.getStatus())) {
                        showAlert(Alert.AlertType.INFORMATION, "Thanh toán thành công",
                                "Giao dịch đã được xử lý thành công!\nMã giao dịch: " + transaction.getTransactionId());
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Thanh toán thất bại",
                                "Giao dịch không thành công. Vui lòng thử lại hoặc chọn phương thức thanh toán khác.");
                    }

                } catch (Exception ex) {
                    resultArea.setText("Lỗi: " + ex.getMessage());
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Có lỗi xảy ra: " + ex.getMessage());
                }
            });
        }

        private Control findNodeByID(VBox container, String id) {
            for (javafx.scene.Node node : container.getChildren()) {
                if (node instanceof GridPane) {
                    GridPane grid = (GridPane) node;
                    for (javafx.scene.Node child : grid.getChildren()) {
                        if (id.equals(child.getId())) {
                            return (Control) child;
                        }
                    }
                }
            }
            return null;
        }

        private void showAlert(Alert.AlertType type, String title, String content) {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        }

        private void processSelectedPayment(String method, VBox formContainer) {
            // Implement this method
        }

        private GridPane createMomoForm() {
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(10));
            grid.setAlignment(Pos.CENTER_LEFT);

            Label phoneLabel = new Label("Số điện thoại:");
            TextField phoneField = new TextField();
            phoneField.setId("momoPhone");
            phoneField.setPromptText("Nhập số điện thoại MoMo");

            Label otpLabel = new Label("Mã OTP:");
            TextField otpField = new TextField();
            otpField.setId("momoOtp");
            otpField.setPromptText("Nhập mã OTP");

            Button requestOtpButton = new Button("Yêu cầu OTP");
            requestOtpButton.setOnAction(e -> {
                if (phoneField.getText().length() == 10 && phoneField.getText().startsWith("0")) {
                    showAlert(Alert.AlertType.INFORMATION, "OTP đã gửi",
                            "Mã OTP đã được gửi đến số điện thoại " + phoneField.getText());
                    otpField.setText(generateRandomOTP());
                } else {
                    showAlert(Alert.AlertType.ERROR, "Số điện thoại không hợp lệ",
                            "Vui lòng nhập số điện thoại hợp lệ (10 số, bắt đầu bằng số 0)");
                }
            });

            grid.add(phoneLabel, 0, 0);
            grid.add(phoneField, 1, 0);
            grid.add(otpLabel, 0, 1);
            grid.add(otpField, 1, 1);
            grid.add(requestOtpButton, 1, 2);

            return grid;
        }

        private String generateRandomOTP() {
            Random random = new Random();
            StringBuilder otp = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                otp.append(random.nextInt(10));
            }
            return otp.toString();
        }

        private GridPane createZaloPayForm() {
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(10));
            grid.setAlignment(Pos.CENTER_LEFT);

            Label idLabel = new Label("ZaloPay ID:");
            TextField idField = new TextField();
            idField.setId("zaloId");
            idField.setPromptText("Nhập ZaloPay ID");

            Label passLabel = new Label("Mật khẩu:");
            PasswordField passField = new PasswordField();
            passField.setId("zaloPass");
            passField.setPromptText("Nhập mật khẩu");

            grid.add(idLabel, 0, 0);
            grid.add(idField, 1, 0);
            grid.add(passLabel, 0, 1);
            grid.add(passField, 1, 1);

            return grid;
        }

        private GridPane createVNPayForm() {
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(10));
            grid.setAlignment(Pos.CENTER_LEFT);

            Label bankLabel = new Label("Ngân hàng:");
            ComboBox<String> bankCombo = new ComboBox<>(
                    FXCollections.observableArrayList(
                            "VCB - Vietcombank",
                            "TCB - Techcombank",
                            "VTB - VietinBank",
                            "ACB - Á Châu Bank",
                            "MBB - MB Bank"
                    )
            );
            bankCombo.setId("bankCombo");
            bankCombo.setValue("VCB - Vietcombank");

            Label accountLabel = new Label("Số tài khoản:");
            TextField accountField = new TextField();
            accountField.setId("vnpayAccount");
            accountField.setPromptText("Nhập số tài khoản");

            grid.add(bankLabel, 0, 0);
            grid.add(bankCombo, 1, 0);
            grid.add(accountLabel, 0, 1);
            grid.add(accountField, 1, 1);

            return grid;
        }

        private GridPane createCreditCardForm() {
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(10));
            grid.setAlignment(Pos.CENTER_LEFT);

            Label cardLabel = new Label("Số thẻ:");
            TextField cardField = new TextField();
            cardField.setId("cardNumber");
            cardField.setPromptText("XXXX XXXX XXXX XXXX");

            Label expiryLabel = new Label("Hạn thẻ:");
            TextField expiryField = new TextField();
            expiryField.setId("expiryDate");
            expiryField.setPromptText("MM/YY");

            Label cvvLabel = new Label("CVV:");
            TextField cvvField = new TextField();
            cvvField.setId("cvv");
            cvvField.setPromptText("XXX");

            grid.add(cardLabel, 0, 0);
            grid.add(cardField, 1, 0);
            grid.add(expiryLabel, 0, 1);
            grid.add(expiryField, 1, 1);
            grid.add(cvvLabel, 0, 2);
            grid.add(cvvField, 1, 2);

            return grid;
        }

        private GridPane createBankTransferForm() {
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(10));
            grid.setAlignment(Pos.CENTER_LEFT);

            Label bankLabel = new Label("Tên ngân hàng:");
            TextField bankField = new TextField();
            bankField.setId("bankName");
            bankField.setPromptText("Nhập tên ngân hàng");

            Label accountLabel = new Label("Số tài khoản:");
            TextField accountField = new TextField();
            accountField.setId("accountNumber");
            accountField.setPromptText("Nhập số tài khoản");

            Label infoLabel = new Label("Thông tin chuyển khoản:");
            TextArea infoArea = new TextArea(
                    "Chủ TK: COURSE MANAGEMENT\n" +
                            "Số TK: 0123456789\n" +
                            "Nội dung: [Mã khóa học] - [Tên học viên]"
            );
            infoArea.setEditable(false);
            infoArea.setPrefRowCount(4);

            grid.add(bankLabel, 0, 0);
            grid.add(bankField, 1, 0);
            grid.add(accountLabel, 0, 1);
            grid.add(accountField, 1, 1);
            grid.add(infoLabel, 0, 2);
            grid.add(infoArea, 1, 2);

            return grid;
        }

        public static void main(String[] args) {
            launch(args);
        }
    }

    // Main class to demonstrate
    public static void main(String[] args) {
        // Start the payment UI
        PaymentUI.main(args);
    }
}