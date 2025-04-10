package com.example.coursemanagement.Models;

import javafx.beans.property.*;

public class Course {
    private int courseId, categoryId, instructorId;
    private String courseName, courseDescription, courseThumbnail;
    private double coursePrice;
    private String created_at;
    private Instructor instructor;
    private Category category;

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Course(int courseId, int categoryId, String courseName, String courseDescription, String courseThumbnail, double coursePrice, String created_at) {
        this.courseId = courseId;
        this.categoryId = categoryId;
        this.courseName = courseName;
        this.courseDescription = courseDescription;
        this.courseThumbnail = courseThumbnail;
        this.coursePrice = coursePrice;
        this.created_at = created_at;

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



    public Course(int courseId, int categoryId, int instructorId, String courseName, String courseDescription, String courseThumbnail, double coursePrice,Category category,Instructor instructor) {
        this.courseId = courseId;
        this.categoryId = categoryId;
        this.instructorId = instructorId;
        this.courseName = courseName;
        this.courseDescription = courseDescription;
        this.courseThumbnail = courseThumbnail;
        this.instructor = instructor;
        this.category = category;
        this.coursePrice = coursePrice;
    }

    public Course(String courseName, int instructorId, int categoryId, double coursePrice, String courseThumbnail, String courseDescription) {
        this.categoryId = categoryId;
        this.courseName = courseName;
        this.courseDescription = courseDescription;
        this.courseThumbnail = courseThumbnail;
        this.instructorId = instructorId;
        this.coursePrice = coursePrice;

    }

    public Course(int courseId, String courseName, int instructorId, int categoryId, double coursePrice, String courseThumbnail, String courseDescription) {
        this.categoryId = categoryId;
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseDescription = courseDescription;
        this.courseThumbnail = courseThumbnail;
        this.instructorId = instructorId;
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
