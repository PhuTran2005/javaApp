package com.example.coursemanagement.Models;

import java.util.Collections;
import java.util.List;

public class Student {
    private int studentId;
    private String studentName;
    private String studentEmail;
    private String studentPhone;
    private String enrolledCourses;

    // Constructor, getters, setters

    public Student() {
    }

    public Student(int studentId, String studentName, String studentEmail, String studentPhone, List<String> enrolledCourses) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.studentPhone = studentPhone;
        this.enrolledCourses = String.valueOf(enrolledCourses);
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
        return Collections.singletonList(enrolledCourses);
    }

    public void setEnrolledCourses(String enrolledCourses) {
        this.enrolledCourses = enrolledCourses;
    }
}