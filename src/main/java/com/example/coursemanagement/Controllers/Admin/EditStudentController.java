package com.example.coursemanagement.Controllers.Admin;

import com.example.coursemanagement.Models.Student;
import com.example.coursemanagement.Respository.StudentRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;

public class EditStudentController {

    private StudentRepository studentRepository;
    private Student selectedStudent;

    @FXML
    private TextField idField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;

    @FXML
    private VBox coursesContainer;

    public EditStudentController() {
        studentRepository = new StudentRepository();
    }

    // Set selected student to be edited
    public void setStudent(Student student) {
        this.selectedStudent = student;


        // Debug thêm
        System.out.println("Setting student in EditStudentController: " + student);

        // Hiển thị thông tin sinh viên
        idField.setText(String.valueOf(student.getStudentId()));
        nameField.setText(student.getStudentName());
        emailField.setText(student.getStudentEmail());
        phoneField.setText(student.getStudentPhone());

        // Hiển thị danh sách khóa học đã đăng ký
        loadCourses();
    }

    // Load all courses and check which ones the student is enrolled in
    private void loadCourses() {
        List<String> allCourses = studentRepository.getAllCourses(); // Lấy danh sách khóa học từ CourseRepository
        coursesContainer.getChildren().clear();

        // Hiển thị danh sách khóa học với checkbox
        for (String courseName : allCourses) {
            CheckBox checkBox = new CheckBox(courseName);
            if (selectedStudent.getEnrolledCourses().contains(courseName)) {
                checkBox.setSelected(true); // Nếu sinh viên đã đăng ký khóa học, đánh dấu checkbox
            }

            checkBox.setOnAction(event -> updateStudentCourses());
            coursesContainer.getChildren().add(checkBox);
        }
    }

    // Cập nhật danh sách khóa học của sinh viên khi checkbox được thay đổi
    private void updateStudentCourses() {
        // Cập nhật lại danh sách khóa học đã chọn
        selectedStudent.getEnrolledCourses().clear();
        for (var node : coursesContainer.getChildren()) {
            if (node instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) node;
                if (checkBox.isSelected()) {
                    selectedStudent.addCourse(checkBox.getText()); // Thêm khóa học đã chọn vào danh sách
                }
            }
        }
    }

    // Lưu thay đổi khi người dùng nhấn nút "Cập nhật"
    @FXML
    private void onUpdateClicked() {
        // Cập nhật các khóa học đã chọn vào database và tính toán học phí
        studentRepository.updateStudentCourses(selectedStudent);

        // Cập nhật lại giá khóa học
        double totalFee = 0;
        for (String courseName : selectedStudent.getEnrolledCourses()) {
            double fee = studentRepository.getCourseFee(courseName); // Lấy học phí từ bảng Courses
            totalFee += fee;
        }

        // Cập nhật học phí vào database nếu cần
        studentRepository.updateStudentFee(selectedStudent.getStudentId(), totalFee);

        // Đóng popup sau khi cập nhật
        closePopup();
    }

    // Hủy bỏ thay đổi
    @FXML
    private void onCancelClicked() {
        closePopup();
    }

    private void closePopup() {
        // Đóng cửa sổ popup
    }
}
