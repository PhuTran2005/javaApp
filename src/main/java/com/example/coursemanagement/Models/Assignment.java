package com.example.coursemanagement.Models;

import java.time.LocalDateTime;

public class Assignment {

    private int id;
    private String title;
    private String courseName;
    private int courseId;
    private LocalDateTime dueDate;
    private int completed;
    private int total;
    private String description;
    private String fileName;  // Store the name of the file (e.g., homework1.pdf)
    private String filePath;  // Store the full path to the file (e.g., C:/files/homework1.pdf)

    public Assignment() {
    }

    public Assignment(int id, String title, String courseName, int courseId, LocalDateTime dueDate, int completed, int total, String description, String fileName, String filePath) {
        this.id = id;
        this.title = title;
        this.courseName = courseName;
        this.courseId = courseId;
        this.dueDate = dueDate;
        this.completed = completed;
        this.total = total;
        this.description = description;
        this.fileName = fileName;  // Initialize fileName
        this.filePath = filePath;  // Initialize filePath
    }

    // Getters and setters for all properties
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public int getCompleted() {
        return completed;
    }

    public void setCompleted(int completed) {
        this.completed = completed;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Getter and Setter for fileName
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    // Getter and Setter for filePath
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return "Assignment{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", courseName='" + courseName + '\'' +
                ", courseId=" + courseId +
                ", dueDate=" + dueDate +
                ", completed=" + completed +
                ", total=" + total +
                ", description='" + description + '\'' +
                ", fileName='" + fileName + '\'' +  // Display file name
                ", filePath='" + filePath + '\'' +  // Display file path
                '}';
    }
}
