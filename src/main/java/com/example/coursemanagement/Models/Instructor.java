package com.example.coursemanagement.Models;

public class Instructor extends User {
    private String expertise, degree;
    private int years_of_experience;

    public Instructor(int userId, String fullname) {
        this.userId = userId;
        this.fullname = fullname;
    }

    public Instructor(int userId, String userEmail, String fullname, int roleId, String userPhoneNumber, String createDate, String expertise, String degree, int years_of_experience) {
        super(userId, userEmail, fullname, roleId, userPhoneNumber, createDate);
        this.degree = degree;
        this.expertise = expertise;
        this.years_of_experience = years_of_experience;
    }


    public String getExpertise() {
        return expertise;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public int getYears_of_experience() {
        return years_of_experience;
    }

    public void setYears_of_experience(int years_of_experience) {
        this.years_of_experience = years_of_experience;
    }
}
