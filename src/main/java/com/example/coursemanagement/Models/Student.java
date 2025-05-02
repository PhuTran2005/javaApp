package com.example.coursemanagement.Models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;

import java.util.ArrayList;
import java.util.List;

public class Student extends User {
    private List<String> enrolledCourses; // Thay đổi từ String thành List<String>
    private String classes;
    private int enrollment_year;
    public float gpa;
    private BooleanProperty selected;
    private FloatProperty progress = new SimpleFloatProperty(0);

    // Constructor, getters, setters

    public Student() {
        super();
        this.enrolledCourses = new ArrayList<>();  // Khởi tạo danh sách khóa học
        this.selected = new SimpleBooleanProperty(false);  // Mặc định checkbox chưa được chọn
    }

    public Student(int userId, String fullname, String userEmail, String userPhoneNumber, List<String> enrolledCourses) {
        this.userId = userId;
        this.fullname = fullname;
        this.userEmail = userEmail;
        this.userPhoneNumber = userPhoneNumber;
        this.enrolledCourses = enrolledCourses; // Khởi tạo danh sách khóa học
        this.selected = new SimpleBooleanProperty(false);  // Mặc định checkbox chưa được chọn
    }

    public Student(int userId, String userEmail, String fullname, int roleId, String userPhoneNumber, String createDate, String classes, int enrollment_year, float gpa) {
        super(userId, userEmail, fullname, roleId, userPhoneNumber, createDate);
        this.classes = classes;
        this.enrollment_year = enrollment_year;
        this.gpa = gpa;
    }

    public String getClasses() {
        return classes;
    }

    public void setClasses(String classes) {
        this.classes = classes;
    }

    public int getEnrollment_year() {
        return enrollment_year;
    }

    public void setEnrollment_year(int enrollment_year) {
        this.enrollment_year = enrollment_year;
    }

    public float getGpa() {
        return gpa;
    }

    public void setGpa(float gpa) {
        this.gpa = gpa;
    }

    public List<String> getEnrolledCourses() {
        return enrolledCourses;
    }

    public void setEnrolledCourses(List<String> enrolledCourses) {
        this.enrolledCourses = enrolledCourses;
    }

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
    public float getProgress() {
        return progress.get();
    }

    public void setProgress(float progress) {
        this.progress.set(progress);
    }

    public FloatProperty progressProperty() {
        return progress;
    }
}
