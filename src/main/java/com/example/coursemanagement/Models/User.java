package com.example.coursemanagement.Models;

public class User {

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", fullname='" + fullname + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", roleName='" + roleName + '\'' +
                ", roleId=" + roleId +
                ", userEmail='" + userEmail + '\'' +
                ", userPhoneNumber='" + userPhoneNumber + '\'' +
                ", createDate='" + createDate + '\'' +
                '}';
    }

    public User(int userId, String userEmail, String fullname, int roleId, String userPhoneNumber) {
        this.userId = userId;
        this.fullname = fullname;
        this.roleId = roleId;
        this.userPhoneNumber = userPhoneNumber;
        this.userEmail = userEmail;
    }

    public User(int userId, String userEmail, String fullname, int roleId, String userPhoneNumber, String createDate) {
        this.userId = userId;
        this.fullname = fullname;
        this.roleId = roleId;
        this.userPhoneNumber = userPhoneNumber;
        this.userEmail = userEmail;
        this.createDate = createDate;
    }

    public User() {
    }

    ;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }


    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
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

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    protected int userId;
    protected String fullname;
    protected String userPassword;
    protected String roleName;
    protected int roleId;
    protected String userEmail;
    protected String userPhoneNumber;
    protected String createDate;

}
