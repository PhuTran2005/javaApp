package com.example.coursemanagement.Controllers.Admin;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class FilterCoursesController {

    @FXML
    private ListView<CheckBox> courseListView;

    private final List<String> selectedCourses = new ArrayList<>();

    // Nhận danh sách khóa học từ bên ngoài và thêm vào ListView dưới dạng CheckBox
    public void setAvailableCourses(List<String> courses) {
        courseListView.getItems().clear();
        for (String course : courses) {
            CheckBox checkBox = new CheckBox(course);
            courseListView.getItems().add(checkBox);
        }
    }

    // Lấy các khóa học đã được chọn
    public List<String> getSelectedCourses() {
        return selectedCourses;
    }

    // Lấy các khóa học đã chọn khi nhấn "Cập nhật"
    @FXML
    private void onUpdateClicked(ActionEvent  event) {
        selectedCourses.clear();
        for (CheckBox checkBox : courseListView.getItems()) {
            if (checkBox.isSelected()) {
                selectedCourses.add(checkBox.getText());
            }
        }

        // Đóng cửa sổ popup khi nhấn Update
        javafx.stage.Stage stage = (javafx.stage.Stage) courseListView.getScene().getWindow();
        stage.close();
    }

    // Đóng cửa sổ popup khi nhấn "Hủy"
    @FXML
    private void onCancelClicked(ActionEvent  event) {
        javafx.stage.Stage stage = (javafx.stage.Stage) courseListView.getScene().getWindow();
        stage.close();
    }
}
