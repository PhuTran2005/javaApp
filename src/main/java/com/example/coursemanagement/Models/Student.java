package com.example.coursemanagement.Models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Student {
    private int studentId;
    private String studentName;
    private String studentEmail;
    private String studentPhone;
    private BigDecimal accountBalance;
    private List<String> enrolledCourses; // Thay đổi từ String thành List<String>
    private BooleanProperty selected;

    // Constructor, getters, setters

    public Student() {
        this.enrolledCourses = new ArrayList<>();  // Khởi tạo danh sách khóa học
        this.selected = new SimpleBooleanProperty(false);  // Mặc định checkbox chưa được chọn
    }

    public Student(int studentId, String studentName, String studentEmail, String studentPhone, List<String> enrolledCourses) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.studentPhone = studentPhone;
        this.enrolledCourses = enrolledCourses; // Khởi tạo danh sách khóa học
        this.selected = new SimpleBooleanProperty(false);  // Mặc định checkbox chưa được chọn
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getStudentPhone() {
        return studentPhone;
    }

    public void setStudentPhone(String studentPhone) {
        this.studentPhone = studentPhone;
    }

    public List<String> getEnrolledCourses() {
        return enrolledCourses;
    }

    public void setEnrolledCourses(List<String> enrolledCourses) {
        this.enrolledCourses = enrolledCourses;
    }

    public BigDecimal getStudentBalance() { return accountBalance; }
    public void setStudentBalance(BigDecimal accountBalance) { this.accountBalance = accountBalance; }

    // Thêm một khóa học vào danh sách
    public void addCourse(String course) {
        this.enrolledCourses.add(course); // Thêm vào danh sách khóa học
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public boolean isSelected() {
        return selected.get();
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }


}
