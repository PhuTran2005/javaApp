package com.example.coursemanagement.Controllers.Admin;

import com.example.coursemanagement.Models.Student;
import com.example.coursemanagement.Models.User;
import com.example.coursemanagement.Service.StudentService;
import com.example.coursemanagement.Utils.Alerts;
import com.example.coursemanagement.Utils.ExcelExporter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.coursemanagement.Utils.DatabaseConfig.getConnection;

public class AccountsController {

//    @FXML private Label studentTab;
//    @FXML private Label instructorTab;
    @FXML private TableView<User> studentTable;
    @FXML private TableColumn<User, Boolean> colSelect;
    @FXML private TableColumn<User, Integer> colId;
    @FXML private TableColumn<User, String> colName, colEmail, colPhone, colCourses;
    @FXML private TextField searchField;
    @FXML private ImageView searchImageView;
    @FXML private Button filterButton;
    @FXML private Button editButton;
    @FXML private ImageView exportImageView;
    @FXML private Button studentRoleBtn;
    @FXML private Button instructorRoleBtn;

    private final ObservableList<User> userList = FXCollections.observableArrayList();
    private final Alerts alerts = new Alerts();
    private String currentRole = "student";

    @FXML
    public void initialize() {
        setupExportButtonEffects();
        setupTableColumns();
        setupSearchEvents();
        setupTabSwitching();

        loadUsersByRole("student");
    }

    private void setupTabSwitching() {
        studentRoleBtn.setOnAction(event -> {
            currentRole = "STUDENT";
            loadUsersByRole(currentRole);
        });

        instructorRoleBtn.setOnAction(event -> {
            currentRole = "INSTRUCTOR";
            loadUsersByRole(currentRole);
        });
    }


    private void setupExportButtonEffects() {
        exportImageView.setOnMouseEntered(e -> {
            exportImageView.setScaleX(1.1);
            exportImageView.setScaleY(1.1);
        });
        exportImageView.setOnMouseExited(e -> {
            exportImageView.setScaleX(1.0);
            exportImageView.setScaleY(1.0);
        });
    }

    private void setupTableColumns() {
        studentTable.setEditable(true);
        colSelect.setEditable(true);

        colSelect.setCellValueFactory(param -> param.getValue().selectedProperty());
        colSelect.setCellFactory(CheckBoxTableCell.forTableColumn(colSelect));
        colSelect.setSortable(false);

        CheckBox selectAllCheckBox = new CheckBox();
        colSelect.setGraphic(selectAllCheckBox);
        selectAllCheckBox.setOnAction(e -> {
            boolean selected = selectAllCheckBox.isSelected();
            for (User user : studentTable.getItems()) {
                user.setSelected(selected);
            }
            studentTable.refresh();
        });

        colId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("fullname"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("userEmail"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("userPhoneNumber"));
        colCourses.setCellValueFactory(new PropertyValueFactory<>("roleName"));

        setCenterAlignment(colId);
        setCenterAlignment(colName);
        setCenterAlignment(colEmail);
        setCenterAlignment(colPhone);
        setCenterAlignment(colCourses);
    }

    private <T> void setCenterAlignment(TableColumn<User, T> column) {
        column.setCellFactory(tc -> {
            TableCell<User, T> cell = new TableCell<>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.toString());
                }
            };
            cell.setStyle("-fx-alignment: CENTER;");
            return cell;
        });
    }

    private void setupSearchEvents() {
        searchField.setOnAction(e -> onSearchClicked(null));
        searchImageView.setOnMouseEntered(e -> {
            searchImageView.setScaleX(1.1);
            searchImageView.setScaleY(1.1);
        });
        searchImageView.setOnMouseExited(e -> {
            searchImageView.setScaleX(1.0);
            searchImageView.setScaleY(1.0);
        });
    }

    @FXML
    private void onSearchClicked(MouseEvent event) {
        String keyword = searchField.getText().toLowerCase().trim();

        if (keyword.isEmpty()) {
            studentTable.setItems(userList);
            return;
        }

        ObservableList<User> filtered = FXCollections.observableArrayList(
                userList.stream()
                        .filter(s -> s.getFullname().toLowerCase().contains(keyword))
                        .collect(Collectors.toList())
        );

        studentTable.setItems(filtered);
    }

    @FXML
    private void onExportExcelClicked(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Luu danh sach nguoi dung");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        fileChooser.setInitialFileName(currentRole + "_list.xlsx");

        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            ExcelExporter.exportUserListToExcel(new ArrayList<>(studentTable.getItems()), file.getAbsolutePath());
            alerts.showSuccessAlert("Xuat file thanh cong!");
        }
    }

    @FXML
    private void onFilterClicked(ActionEvent event) {
        if (!currentRole.equals("student")) {
            alerts.showErrorAlert("Filter chi ap dung cho sinh vien.");
            return;
        }
        // Chưa làm chức năng filter theo course
        alerts.showInfoAlert("Thông báo", "Filter chi ap dung cho sinh vien.");

    }

    @FXML
    private void onEditClicked(ActionEvent event) {
        if (!currentRole.equals("student")) {
            alerts.showErrorAlert("Chi chinh sua duoc sinh vien.");
            return;
        }
        alerts.showInfoAlert("Thông báo", "Chua cai dat chuc nang chinh sua.");

    }

    private void loadUsersByRole(String roleName) {
        userList.clear();
        String sql = "SELECT u.user_id, u.email, u.full_name, u.phonenumber, u.create_date, r.role_name AS roleName "
                + "FROM Users u "
                + "JOIN Roles r ON u.role_id = r.role_id "
                + "WHERE r.role_name = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, roleName);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                userList.add(new User(
                        rs.getInt("user_id"),
                        rs.getString("email"),
                        rs.getString("full_name"),
                        roleName,                        // vì constructor nhận roleName
                        rs.getString("phonenumber"),     // ✅ đây là cột đúng
                        rs.getString("create_date")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        studentTable.setItems(userList);
    }

    @FXML
    private void handleStudentTab(ActionEvent event) {
        currentRole = "STUDENT";
        loadUsersByRole(currentRole);
    }

    @FXML
    private void handleInstructorTab(ActionEvent event) {
        currentRole = "INSTRUCTOR";
        loadUsersByRole(currentRole);
    }
}