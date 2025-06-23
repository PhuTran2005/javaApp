package com.example.coursemanagement.Models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class User {

    protected int userId;
    protected String fullname;
    protected String userPassword;
    protected String roleName;
    protected int roleId;
    protected String userEmail;
    protected String userPhoneNumber;
    protected String createDate;

    // Dùng để chọn trong bảng (TableView)
    private final BooleanProperty selected = new SimpleBooleanProperty(false);

    public User() {
    }

    public User(int userId, String userEmail, String fullname, int roleId, String userPhoneNumber) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.fullname = fullname;
        this.roleId = roleId;
        this.userPhoneNumber = userPhoneNumber;
    }

    public User(int userId, String userEmail, String fullname, int roleId, String userPhoneNumber, String createDate) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.fullname = fullname;
        this.roleId = roleId;
        this.userPhoneNumber = userPhoneNumber;
        this.createDate = createDate;
    }

    // Getter/Setter
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
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

    // Selected property cho checkbox trong bảng
    public BooleanProperty selectedProperty() {
        return selected;
    }

    public boolean isSelected() {
        return selected.get();
    }

    public void setSelected(boolean value) {
        selected.set(value);
    }

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
    public User(int userId, String email, String fullName, String roleName) {
        this.userId = userId;
        this.userEmail = email;
        this.fullname = fullName;
        this.roleName = roleName;
    }

    public User(int userId, String email, String fullName, String roleName, String phone, String createDate) {
        this.userId = userId;
        this.userEmail = email;
        this.fullname = fullName;
        this.roleName = roleName;
        this.userPhoneNumber = phone;
        this.createDate = createDate;
    }


}
