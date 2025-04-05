package com.example.coursemanagement.Models;

import javafx.beans.property.*;

public class Course {
    private int courseId, categoryId;
    private String courseName, courseDescription, courseThumbnail, instructorName, categories;
    private double coursePrice;
    private String created_at;


    public Course(int courseId,int categoryId, String courseName, String courseDescription, String courseThumbnail, String instructorName, double coursePrice, String created_at) {
        this.courseId = courseId;
        this.categoryId = categoryId;
        this.courseName = courseName;
        this.courseDescription = courseDescription;
        this.courseThumbnail = courseThumbnail;
        this.instructorName = instructorName;
        this.coursePrice = coursePrice;
        this.created_at = created_at;

    }

    public Course(String courseName, String instructorName, int categoryId, double coursePrice, String courseThumbnail, String courseDescription) {
        this.categoryId = categoryId;
        this.courseName = courseName;
        this.courseDescription = courseDescription;
        this.courseThumbnail = courseThumbnail;
        this.instructorName = instructorName;
        this.coursePrice = coursePrice;

    }

    @Override
    public String toString() {
        return "Course{" +
                "courseId=" + courseId +
                ", categoryId=" + categoryId +
                ", courseName='" + courseName + '\'' +
                ", courseDescription='" + courseDescription + '\'' +
                ", courseThumbnail='" + courseThumbnail + '\'' +
                ", instructorName='" + instructorName + '\'' +
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

    public int getcategoryId() {
        return categoryId;
    }

    public void setcategoryId(int categoryId) {
        this.categoryId = categoryId;
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

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
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
