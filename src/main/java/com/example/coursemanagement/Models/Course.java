package com.example.coursemanagement.Models;

import javafx.beans.property.*;

import java.time.LocalDate;

public class Course {
    private int courseId, categoryId, instructorId;
    private String courseName, courseDescription, courseThumbnail;
    private boolean isDelete;

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    private double coursePrice;
    private String created_at;
    private LocalDate startDate, endDate;

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Course(int courseId, int categoryId, String courseName, String courseDescription, String courseThumbnail, double coursePrice, String created_at, boolean isDelete) {
        this.courseId = courseId;
        this.categoryId = categoryId;
        this.courseName = courseName;
        this.courseDescription = courseDescription;
        this.courseThumbnail = courseThumbnail;
        this.coursePrice = coursePrice;
        this.created_at = created_at;
        this.isDelete = isDelete;

    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(int instructorId) {
        this.instructorId = instructorId;
    }

    //detail con
    public Course(int courseId, int categoryId, int instructorId, String courseName, String courseDescription, String courseThumbnail, double coursePrice, String created_at, LocalDate startDate, LocalDate endDate, boolean isDelete) {
        this.courseId = courseId;
        this.categoryId = categoryId;
        this.instructorId = instructorId;
        this.courseName = courseName;
        this.courseDescription = courseDescription;
        this.courseThumbnail = courseThumbnail;
        this.created_at = created_at;
        this.coursePrice = coursePrice;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isDelete = isDelete;
    }

    //add course
    public Course(String courseName, int instructorId, int categoryId, double coursePrice, String courseThumbnail, String courseDescription, LocalDate startDate, LocalDate endDate) {
        this.categoryId = categoryId;
        this.courseName = courseName;
        this.courseDescription = courseDescription;
        this.courseThumbnail = courseThumbnail;
        this.instructorId = instructorId;
        this.coursePrice = coursePrice;
        this.startDate = startDate;
        this.endDate = endDate;

    }

    //edit course
    public Course(int courseId, String courseName, int instructorId, int categoryId, double coursePrice, String courseThumbnail, String courseDescription, LocalDate startDate, LocalDate endDate) {
        this.categoryId = categoryId;
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseDescription = courseDescription;
        this.courseThumbnail = courseThumbnail;
        this.instructorId = instructorId;
        this.coursePrice = coursePrice;
        this.startDate = startDate;
        this.endDate = endDate;

    }

    @Override
    public String toString() {
        return "Course{" +
                "courseId=" + courseId +
                ", categoryId=" + categoryId +
                ", courseName='" + courseName + '\'' +
                ", courseDescription='" + courseDescription + '\'' +
                ", courseThumbnail='" + courseThumbnail + '\'' +
                ", instructorName='" + instructorId + '\'' +
                ", coursePrice=" + coursePrice +
                ", created_at='" + created_at + '\'' +
                '}';
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }


    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    public String getCourseThumbnail() {
        return courseThumbnail;
    }

    public void setCourseThumbnail(String courseThumbnail) {
        this.courseThumbnail = courseThumbnail;
    }

    public double getCoursePrice() {
        return coursePrice;
    }

    public void setCoursePrice(double coursePrice) {
        this.coursePrice = coursePrice;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }


}
