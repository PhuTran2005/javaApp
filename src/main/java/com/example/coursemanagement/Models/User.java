package com.example.coursemanagement.Models;

public class User {

    public User(String userEmail, String role) {
        this.role = role;
        this.userEmail = userEmail;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", role='" + role + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", userPhoneNumber='" + userPhoneNumber + '\'' +
                ", createDate='" + createDate + '\'' +
                '}';
    }

    public User(int userId, String userPassword, String userEmail) {
        this.userId = userId;
        this.userPassword = userPassword;
        this.userEmail = userEmail;
    }

    public User(String userPassword, String userEmail,String role) {
        this.userPassword = userPassword;
        this.userEmail = userEmail;
        this.role = role;
    }

    public User(int userId, String username, String userPassword, String role, String userEmail, String userPhoneNumber, String createDate) {
        this.userId = userId;
        this.username = username;
        this.userPassword = userPassword;
        this.role = role;
        this.userEmail = userEmail;
        this.userPhoneNumber = userPhoneNumber;
        this.createDate = createDate;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    private int userId;
    private String username;
    private String userPassword;
    private String role;
    private String userEmail;
    private String userPhoneNumber;
    private String createDate;

}
