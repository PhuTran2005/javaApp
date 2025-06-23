package com.example.coursemanagement.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;

public class ValidatorUtil {
    public static boolean isValidUsername(String username) {
        return username.matches("^[a-zA-Z0-9_]{4,20}$");
    }

    public static boolean isValidPassword(String password, int length) {
        return password.length() >= length;
    }

    private static final Alerts alerts = new Alerts();

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

    public static boolean validateFullName(String fullname) {
        // Kiểm tra null hoặc chuỗi rỗng
        if (fullname == null || fullname.trim().isEmpty()) {
            return false;
        }

        // Cho phép chữ có dấu, dấu cách, dấu nháy đơn, chấm, và gạch nối
        return fullname.matches("^[\\p{L} .'-]+$");
    }

    public static LocalDate getLocalDateSafe(ResultSet rs, String column) throws SQLException {
        Date sqlDate = rs.getDate(column);
        return (sqlDate != null) ? ((java.sql.Date) sqlDate).toLocalDate() : null;
    }

}
