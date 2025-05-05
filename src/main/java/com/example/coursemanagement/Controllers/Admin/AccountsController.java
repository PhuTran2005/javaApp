package com.example.coursemanagement.Controllers.Admin;

import com.example.coursemanagement.Models.Student;
import com.example.coursemanagement.Service.StudentService;

import com.example.coursemanagement.Utils.Alerts;
import com.example.coursemanagement.Utils.ExcelExporter;
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
import java.util.List;
import java.util.stream.Collectors;

public class AccountsController {

    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, Boolean> colSelect;
    @FXML private TableColumn<Student, Integer> colId;
    @FXML private TableColumn<Student, String> colName, colEmail, colPhone, colCourses;
    @FXML private TextField searchField;
    @FXML private ImageView searchImageView;
    @FXML private Button filterButton;
    @FXML private Button editButton;
    @FXML private ImageView exportImageView;

    private List<Student> allStudents;
    private final Alerts alerts = new Alerts();
    private final StudentService studentService = new StudentService();

    @FXML
    public void initialize() {
        setupExportButtonEffects();
        setupTableColumns();
        loadStudentData();
        setupSearchEvents();

        studentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                System.out.println("Selected student: " + newSelection.getFullname());
            } else {
                System.out.println("No student selected");
            }
        });

        studentTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
                if (selectedStudent != null) {
                    System.out.println("Student selected: " + selectedStudent.getFullname());
                }
            }
        });
    }

    public void loadStudentData() {
        // Lấy danh sách sinh viên từ StudentService
        allStudents = studentService.getAllStudents();
        studentTable.getItems().setAll(allStudents);

        // Đồng bộ checkbox "Select All"
        CheckBox selectAllCheckBox = (CheckBox) colSelect.getGraphic();
        for (Student student : allStudents) {
            student.selectedProperty().addListener((obs, oldVal, newVal) -> {
                boolean allSelected = studentTable.getItems().stream().allMatch(Student::isSelected);
                selectAllCheckBox.setSelected(allSelected);
            });
        }
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

        colSelect.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        colSelect.setCellFactory(CheckBoxTableCell.forTableColumn(colSelect));
        colSelect.setSortable(false);

        CheckBox selectAllCheckBox = new CheckBox();
        colSelect.setGraphic(selectAllCheckBox);
        selectAllCheckBox.setOnAction(e -> {
            boolean selected = selectAllCheckBox.isSelected();
            for (Student student : studentTable.getItems()) {
                student.setSelected(selected);
            }
            studentTable.refresh();
        });

        colId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("fullname"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("userEmail"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("userPhoneNumber"));
        colCourses.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                String.join("\n", cellData.getValue().getEnrolledCourses())
        ));
        colCourses.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
            }
        });

        setCenterAlignment(colId);
        setCenterAlignment(colName);
        setCenterAlignment(colEmail);
        setCenterAlignment(colPhone);
        setCenterAlignment(colCourses);
    }

    private <T> void setCenterAlignment(TableColumn<Student, T> column) {
        column.setCellFactory(tc -> {
            TableCell<Student, T> cell = new TableCell<>() {
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

    @FXML
    private void onExportExcelClicked(MouseEvent event) {
        List<Student> students = studentTable.getItems();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu danh sách sinh viên");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        fileChooser.setInitialFileName("student_list.xlsx");

        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            ExcelExporter.exportStudentListToExcel(students, file.getAbsolutePath());
            alerts.showSuccessAlert("Xuất file Excel thành công!");
        }
    }

    @FXML
    private void onSearchClicked(MouseEvent event) {
        String keyword = searchField.getText().toLowerCase().trim();

        if (keyword.isEmpty()) {
            studentTable.getItems().setAll(allStudents);
            return;
        }

        List<Student> filtered = allStudents.stream()
                .filter(s -> s.getFullname().toLowerCase().contains(keyword))
                .collect(Collectors.toList());

        studentTable.getItems().setAll(filtered);
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
    private void onFilterClicked(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/FilterCoursesDialog.fxml"));
            Parent root = loader.load();

            FilterCoursesController filterController = loader.getController();
            filterController.setAvailableCourses(studentService.getAllCourses());

            Stage stage = new Stage();
            stage.setTitle("Filter list by course");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            List<String> selectedCourses = filterController.getSelectedCourses();
            if (selectedCourses != null && !selectedCourses.isEmpty()) {
                List<Student> filtered = allStudents.stream()
                        .filter(student -> student.getEnrolledCourses().containsAll(selectedCourses))
                        .collect(Collectors.toList());
                studentTable.getItems().setAll(filtered);
            } else {
                studentTable.getItems().setAll(allStudents);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onEditClicked(ActionEvent event) {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();

        if (selectedStudent != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/EditStudentDialog.fxml"));
                Parent root = loader.load();

                EditStudentController editController = loader.getController();
                editController.setStudent(selectedStudent);

                Stage stage = new Stage();
                stage.setTitle("Course Updates");
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();

                loadStudentData(); // Tải lại dữ liệu sau khi chỉnh sửa
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            alerts.showErrorAlert("Vui lòng chọn sinh viên để chỉnh sửa.");
        }
    }
}
