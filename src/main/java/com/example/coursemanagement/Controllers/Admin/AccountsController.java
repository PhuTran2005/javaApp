package com.example.coursemanagement.Controllers.Admin;

import com.example.coursemanagement.Models.Student;
import com.example.coursemanagement.Service.StudentService;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class AccountsController {

    @FXML
    private TableView<Student> studentTable;

    @FXML
    private TableColumn<Student, Integer> colId;

    @FXML
    private TableColumn<Student, String> colName, colEmail, colPhone, colCourses;

    private StudentService studentService = new StudentService();

    @FXML
    public void initialize() {
        // Thiết lập cột và các cell factory cho TableView
        colId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("studentEmail"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("studentPhone"));
        colCourses.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                String.join(", ", cellData.getValue().getEnrolledCourses())
        ));

        // Căn giữa tất cả các cột trong TableView
        setCenterAlignment(colId);
        setCenterAlignment(colName);
        setCenterAlignment(colEmail);
        setCenterAlignment(colPhone);
        setCenterAlignment(colCourses);

        List<Student> students = studentService.getAllStudents();
        studentTable.getItems().setAll(students);
    }

    // Hàm giúp căn giữa tất cả các cột
    private <T> void setCenterAlignment(TableColumn<Student, T> column) {
        column.setCellFactory(tc -> {
            TableCell<Student, T> cell = new TableCell<>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.toString());
                    }
                }
            };
            // Căn giữa nội dung trong mỗi ô
            cell.setStyle("-fx-alignment: CENTER;");
            return cell;
        });
    }
}
