package com.example.coursemanagement.Controllers.Client;

import com.example.coursemanagement.Models.Student;
import com.example.coursemanagement.Repository.StudentRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.Stage;

import java.util.List;

public class ViewStudentListController {

    @FXML
    private TableView<Student> studentTableView;

    @FXML
    private Button closeButton;
    @FXML
    private TableColumn<Student, String> studentNameColumn;

    @FXML
    private TableColumn<Student, String> studentEmailColumn;

    @FXML
    private TableColumn<Student, String> studentProgressColumn;

    private final StudentRepository studentRepository = new StudentRepository();
    private int courseId;  // Biến courseId để lưu id của khóa học

    // Phương thức setter để nhận courseId
    public void setCourseId(int courseId) {
        this.courseId = courseId;
        loadStudentData();  // Sau khi nhận courseId, gọi lại loadStudentData() để load dữ liệu
    }

    public void loadStudentData() {
        // để lấy danh sách học viên theo courseId
        List<Student> students = studentRepository.getStudentsByCourseId(courseId);

        if (students == null || students.isEmpty()) {
            // Xử lý khi không có dữ liệu học viên
            System.out.println("Không có dữ liệu học viên.");
            return;
        }

        ObservableList<Student> studentData = FXCollections.observableArrayList(students);
        studentTableView.setItems(studentData);
    }

    @FXML
    public void initialize() {
        studentNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFullname())
        );

        studentEmailColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getUserEmail())
        );

        // Hiển thị progress dạng "75%"
        studentProgressColumn.setCellValueFactory(cellData -> {
            float progress = cellData.getValue().getProgress();
            String progressString = String.format("%.0f%%", progress); // làm tròn không có số lẻ
            return new SimpleStringProperty(progressString);
        });
        closeButton.setOnAction(event -> handleClose());
    }
    @FXML
    public void handleClose() {
        // Lấy cửa sổ hiện tại và đóng nó
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
