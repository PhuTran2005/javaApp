package com.example.coursemanagement.Controllers.Admin;

import com.example.coursemanagement.Models.Log;
import com.example.coursemanagement.Repository.LogRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class LogController implements Initializable {

    @FXML
    private TableView<Log> logTableView;

    @FXML
    private TableColumn<Log, Integer> logIdColumn;

    @FXML
    private TableColumn<Log, Integer> userIdColumn;

    @FXML
    private TableColumn<Log, String> actionColumn;

    @FXML
    private TableColumn<Log, LocalDateTime> timeColumn;

    @FXML
    private TextField searchField;

    @FXML
    private Label currentPageLabel;

    @FXML
    private Label totalPagesLabel;

    @FXML
    private ComboBox<Integer> pageSizeComboBox;

    // Pagination properties
    private int currentPage = 1;
    private int pageSize = 10;
    private int totalPages = 1;
    private String currentSearchTerm = "";

    private final LogRepository logDAO = new LogRepository();
    private ObservableList<Log> logData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize table columns
        logIdColumn.setCellValueFactory(new PropertyValueFactory<>("logId"));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        actionColumn.setCellValueFactory(new PropertyValueFactory<>("action"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("actionTime"));

        // Format the date time column
        timeColumn.setCellFactory(column -> {
            return new TableCell<Log, LocalDateTime>() {
                private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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

        // Setup page size combo box
        pageSizeComboBox.setItems(FXCollections.observableArrayList(10, 25, 50, 100));
        pageSizeComboBox.setValue(pageSize);

        // Load initial data
        loadData();
    }

    private void loadData() {
        // Get total count for pagination
        int totalCount;
        List<Log> logs;

        if (currentSearchTerm.isEmpty()) {
            totalCount = logDAO.getTotalLogsCount();
            logs = logDAO.getLogs(currentPage, pageSize);
        } else {
            totalCount = logDAO.getSearchResultsCount(currentSearchTerm);
            logs = logDAO.searchLogs(currentSearchTerm, currentPage, pageSize);
        }

        // Calculate total pages
        totalPages = (int) Math.ceil((double) totalCount / pageSize);
        if (totalPages == 0) totalPages = 1;

        // Update UI
        totalPagesLabel.setText(String.valueOf(totalPages));
        currentPageLabel.setText(String.valueOf(currentPage));

        // Update table data
        logData.clear();
        logData.addAll(logs);
        logTableView.setItems(logData);
    }

    @FXML
    private void handleSearchAction() {
        currentSearchTerm = searchField.getText().trim();
        currentPage = 1; // Reset to first page
        loadData();
    }

    @FXML
    private void handleResetAction() {
        searchField.clear();
        currentSearchTerm = "";
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

    // Method to create a new log entry
    public void createLog(Integer userId, String action) {
        boolean success = logDAO.insertLog(userId, action);
        if (success) {
            // Refresh the table if currently viewing the first page
            if (currentPage == 1 && currentSearchTerm.isEmpty()) {
                loadData();
            }
        }
    }
}