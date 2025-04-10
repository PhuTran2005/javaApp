package com.example.coursemanagement.Models;

public class Instructor {
    private int instructorId;
    private String instructorName,expertise,instructorEmail,instructorPhone;

    public Instructor(int instructorId, String instructorName, String expertise, String instructorEmail, String instructorPhone) {
        this.instructorId = instructorId;
        this.instructorName = instructorName;
        this.expertise = expertise;
        this.instructorEmail = instructorEmail;
        this.instructorPhone = instructorPhone;
    }

    @Override
    public String toString() {
        return "Instructor{" +
                "instructorId=" + instructorId +
                ", instructorName='" + instructorName + '\'' +
                ", expertise='" + expertise + '\'' +
                ", instructorEmail='" + instructorEmail + '\'' +
                ", instructorPhone='" + instructorPhone + '\'' +
                '}';
    }

    public int getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(int instructorId) {
        this.instructorId = instructorId;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public String getExpertise() {
        return expertise;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    public String getInstructorEmail() {
        return instructorEmail;
    }

    public void setInstructorEmail(String instructorEmail) {
        this.instructorEmail = instructorEmail;
    }

    public String getInstructorPhone() {
        return instructorPhone;
    }

    public void setInstructorPhone(String instructorPhone) {
        this.instructorPhone = instructorPhone;
    }
}
