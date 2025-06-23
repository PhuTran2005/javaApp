package com.example.coursemanagement.Controllers.Admin;

import com.example.coursemanagement.Models.Payment;
import com.example.coursemanagement.Repository.PaymentRepository;
import com.example.coursemanagement.Utils.Alerts;
import com.example.coursemanagement.Utils.ExcelExporter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class TransactionController implements Initializable {
    @FXML
    public  TableColumn<Payment, String> fullnameColumn;
    @FXML
    private TableView<Payment> transactionTableView;
    @FXML
    private TableColumn<Payment, Integer> paymentIdColumn;
    @FXML
    private TableColumn<Payment, BigDecimal> amountColumn;

    @FXML
    private TableColumn<Payment, String> methodColumn;

    @FXML
    private TableColumn<Payment, String> statusColumn;

    @FXML
    private TableColumn<Payment, LocalDateTime> dateColumn;

    @FXML
    private TableColumn<Payment, Void> actionsColumn;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> statusFilterComboBox;

    @FXML
    private Label currentPageLabel;

    @FXML
    private Label totalPagesLabel;

    @FXML
    private Label totalRecordsLabel;

    @FXML
    private ComboBox<Integer> pageSizeComboBox;

    // Pagination properties
    private int currentPage = 1;
    private int pageSize = 10;
    private int totalPages = 1;
    private int totalRecords = 0;
    private String currentSearchTerm = "";
    private String currentStatusFilter = "All";

    private final PaymentRepository paymentRepository = new PaymentRepository();
    private ObservableList<Payment> paymentData = FXCollections.observableArrayList();
    private final Alerts alerts = new Alerts(); // Tạo repository

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize table columns
        paymentIdColumn.setCellValueFactory(new PropertyValueFactory<>("paymentId"));
        fullnameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        methodColumn.setCellValueFactory(new PropertyValueFactory<>("method"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));

        // Format the amount column with currency
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        amountColumn.setCellFactory(column -> {
            return new TableCell<Payment, BigDecimal>() {
                @Override
                protected void updateItem(BigDecimal amount, boolean empty) {
                    super.updateItem(amount, empty);
                    if (empty || amount == null) {
                        setText(null);
                    } else {
                        setText(currencyFormat.format(amount));
                    }
                }
            };
        });

        // Format the date column
        dateColumn.setCellFactory(column -> {
            return new TableCell<Payment, LocalDateTime>() {
                private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

                @Override
                protected void updateItem(LocalDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(formatter.format(item));
                    }
                }
            };
        });

        // Style the status column based on status value
        statusColumn.setCellFactory(column -> {
            return new TableCell<Payment, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        switch (item) {
                            case "Success":
                                setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                                break;
                            case "Pending":
                                setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                                break;
                            case "Failed":
                                setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                                break;
                            default:
                                setStyle("");
                        }
                    }
                }
            };
        });

        // Setup actions column with buttons
        setupActionsColumn();

        // Load initial data
        loadData();
    }

    private void setupActionsColumn() {
        Callback<TableColumn<Payment, Void>, TableCell<Payment, Void>> cellFactory =
                new Callback<TableColumn<Payment, Void>, TableCell<Payment, Void>>() {
                    @Override
                    public TableCell<Payment, Void> call(TableColumn<Payment, Void> param) {
                        return new TableCell<Payment, Void>() {
                            private final Button viewBtn = new Button("View");
                            private final HBox pane = new HBox(5, viewBtn);

                            {
                                // Configure button actions
                                viewBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

                                pane.setPadding(new Insets(3));

                                viewBtn.setOnAction(event -> {
                                    Payment payment = getTableView().getItems().get(getIndex());
                                    showPaymentDetails(payment, payment.getPaymentId());
                                });

                            }

                            @Override
                            protected void updateItem(Void item, boolean empty) {
                                super.updateItem(item, empty);

                                if (empty) {
                                    setGraphic(null);
                                } else {
                                    setGraphic(pane);
                                }
                            }
                        };
                    }
                };

        actionsColumn.setCellFactory(cellFactory);
    }

    private void loadData() {
        // Get total count for pagination
        if (currentSearchTerm.isEmpty() && currentStatusFilter.equals("All")) {
            totalRecords = paymentRepository.getTotalPaymentsCount();
            List<Payment> payments = paymentRepository.getPayments(currentPage, pageSize);
            updateTableData(payments);
        } else if (!currentSearchTerm.isEmpty() && currentStatusFilter.equals("All")) {
            totalRecords = paymentRepository.getSearchResultsCount(currentSearchTerm);
            List<Payment> payments = paymentRepository.searchPayments(currentSearchTerm, currentPage, pageSize);
            updateTableData(payments);
        } else if (currentSearchTerm.isEmpty() && !currentStatusFilter.equals("All")) {
            totalRecords = paymentRepository.getFilterResultsCount(currentStatusFilter);
            List<Payment> payments = paymentRepository.filterPaymentsByStatus(currentStatusFilter, currentPage, pageSize);
            updateTableData(payments);
        } else {
            // Complex case: both search term and status filter are active
            // This would need a custom method in DAO to handle both filters
            totalRecords = paymentRepository.getFilterAndSearchResultsCount(currentSearchTerm, currentStatusFilter);
            List<Payment> payments = paymentRepository.filterAndSearchPayments(currentSearchTerm, currentStatusFilter, currentPage, pageSize);
            updateTableData(payments);
        }
    }

    private void updateTableData(List<Payment> payments) {
        // Calculate total pages
        totalPages = (int) Math.ceil((double) totalRecords / pageSize);
        if (totalPages == 0) totalPages = 1;

        // Update UI
        totalPagesLabel.setText(String.valueOf(totalPages));
        currentPageLabel.setText(String.valueOf(currentPage));
        totalRecordsLabel.setText(totalRecords + " records");

        // Update table data
        paymentData.clear();
        paymentData.addAll(payments);
        transactionTableView.setItems(paymentData);
    }

    @FXML
    private void handleSearchAction() {
        currentSearchTerm = searchField.getText().trim();
        currentPage = 1; // Reset to first page
        loadData();
    }

    @FXML
    private void handleStatusFilterChange() {
        currentStatusFilter = statusFilterComboBox.getValue();
        currentPage = 1; // Reset to first page
        loadData();
    }

    @FXML
    private void handleResetAction() {
        searchField.clear();
        statusFilterComboBox.setValue("All");
        currentSearchTerm = "";
        currentStatusFilter = "All";
        currentPage = 1; // Reset to first page
        loadData();
    }

    @FXML
    private void handlePreviousAction() {
        if (currentPage > 1) {
            currentPage--;
            loadData();
        }
    }

    @FXML
    private void handleNextAction() {
        if (currentPage < totalPages) {
            currentPage++;
            loadData();
        }
    }

    @FXML
    private void handlePageSizeChange() {
        pageSize = pageSizeComboBox.getValue();
        currentPage = 1; // Reset to first page when changing page size
        loadData();
    }

    @FXML

    private void showPaymentDetails(Payment payment, int paymentId) {
        payment = paymentRepository.getPaymentsDetail(paymentId);

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        String content = "Payment ID: " + payment.getPaymentId() + "\n" +
                "Email: " + payment.getEmail() + "\n" +
                "Fullname: " + payment.getFullName() + "\n" +
                "Amount: " + currencyFormat.format(payment.getAmount()) + "\n" +
                "Payment Method: " + payment.getMethod() + "\n" +
                "Status: " + payment.getStatus() + "\n" +
                "Payment Date: " + dateFormatter.format(payment.getPaymentDate());

        alerts.showSuccessAlert(content);
        // Log this view action
        System.out.println("Viewed payment details for Payment ID: " + payment.getPaymentId());
    }

    /**
     * Refresh data after a new transaction is added
     */
    public void refreshData() {
        currentPage = 1; // Reset to first page
        loadData();
    }

    /**
     * Export current displayed payments to Excel file
     */
    @FXML
    private void handleExportToExcelAction() {
        try {
            List<Payment> paymentsToExport;

            // Get all data based on current filters
            if (currentSearchTerm.isEmpty() && currentStatusFilter.equals("All")) {
                paymentsToExport = paymentRepository.getAllPayments();
            } else if (!currentSearchTerm.isEmpty() && currentStatusFilter.equals("All")) {
                paymentsToExport = paymentRepository.searchAllPayments(currentSearchTerm);
            } else if (currentSearchTerm.isEmpty() && (!currentStatusFilter.equals("All"))) {
                paymentsToExport = paymentRepository.filterAllPaymentsByStatus(currentStatusFilter);
            } else {
                paymentsToExport = paymentRepository.filterAndSearchAllPayments(currentSearchTerm, currentStatusFilter);
            }

            if (paymentsToExport.isEmpty()) {
                alerts.showConfirmationWarmingAlert(" Không có bản ghi thanh toán để xuất file");
                return;
            }

            // Create file chooser for saving the Excel file
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Excel File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
            fileChooser.setInitialFileName("Payment_Transactions_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx");

            // Show save dialog
            File file = fileChooser.showSaveDialog(transactionTableView.getScene().getWindow());

            if (file != null) {
                // Use the ExcelExporter utility to export the data
                boolean success = ExcelExporter.exportPaymentsToExcel(paymentsToExport, file.getAbsolutePath());

                if (success) {
                    alerts.showSuccessAlert("Xuất thành công file Excel thanh toán ");


                    // Log the export action
                    System.out.println("Exported " + paymentsToExport.size() + " payment records to: " + file.getAbsolutePath());
                } else {
                    alerts.showErrorAlert("Xuất Excel thanh toán thất bại");
                }
            }
        } catch (Exception e) {
            alerts.showErrorAlert("Xảy ra lỗi khi xuất file " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Print the current payments table
     */
    @FXML
    private void handlePrintAction() {
        // Printing functionality implementation...
    }

    /**
     * Helper method to show alerts
     */

}