package com.example.coursemanagement.Utils;

public class ValidatorUtil {
    public static boolean isValidUsername(String username) {
        return username.matches("^[a-zA-Z0-9_]{4,20}$");
    }

    public static boolean isValidPassword(String password,int length) {
        return password.length() >= length;
    }

    public static boolean isValidEmail(String email) {
        return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
    }
    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return true;
        }
        // Biểu thức chính quy: Số điện thoại bắt đầu bằng 0 và có 10 chữ số
        String regex = "^0\\d{9}$";
        return phoneNumber.matches(regex);
    }

}
