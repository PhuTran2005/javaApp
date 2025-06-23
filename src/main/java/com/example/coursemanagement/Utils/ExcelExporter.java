package com.example.coursemanagement.Utils;

import com.example.coursemanagement.Models.Payment;
import com.example.coursemanagement.Models.Student;

import com.example.coursemanagement.Models.User;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;


import java.util.Locale;

public class ExcelExporter {

    public static void exportStudentListToExcel(List<Student> students, String filePath) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Students");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Name");
            header.createCell(2).setCellValue("Email");
            header.createCell(3).setCellValue("Phone");
            header.createCell(4).setCellValue("Courses");

            // Tạo một CellStyle với WrapText
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setWrapText(true); // Kích hoạt tính năng wrap text

            int rowIndex = 1;
            for (Student student : students) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(student.getUserId());
                row.createCell(1).setCellValue(student.getFullname());
                row.createCell(2).setCellValue(student.getUserEmail());
                row.createCell(3).setCellValue(student.getUserPhoneNumber());

                // Tạo ô và gán giá trị danh sách khóa học, sau đó áp dụng cellStyle cho ô đó
                Cell coursesCell = row.createCell(4);
                String courses = String.join(", ", student.getEnrolledCourses());
                coursesCell.setCellValue(courses);
                coursesCell.setCellStyle(cellStyle); // Áp dụng wrap text cho ô này
            }

            for (int i = 0; i < 5; i++) {
                sheet.autoSizeColumn(i);
                // Thêm một khoảng trống nhỏ vào độ rộng cột
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 500);
            }

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Exports payment transactions to an Excel file.
     *
     * @param payments The list of payments to export
     * @param filePath The path where the Excel file will be saved
     * @return true if export was successful, false otherwise
     */
    public static boolean exportPaymentsToExcel(List<Payment> payments, String filePath) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Payment Transactions");

            // Create header row style
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] columns = {"Payment ID", "Order ID", "Amount", "Payment Method", "Status", "Payment Date"};

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // Create cell styles
            CellStyle dateCellStyle = workbook.createCellStyle();
            CreationHelper createHelper = workbook.getCreationHelper();
            dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy hh:mm:ss"));

            CellStyle currencyCellStyle = workbook.createCellStyle();
            currencyCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,##0 VND"));

            CellStyle successStyle = workbook.createCellStyle();
            successStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            successStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle pendingStyle = workbook.createCellStyle();
            pendingStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            pendingStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle failedStyle = workbook.createCellStyle();
            failedStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
            failedStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

            // Create data rows
            int rowNum = 1;
            for (Payment payment : payments) {
                Row row = sheet.createRow(rowNum++);

                // Payment ID
                Cell idCell = row.createCell(0);
                idCell.setCellValue(payment.getPaymentId());

                // Order ID
                Cell orderIdCell = row.createCell(1);
                orderIdCell.setCellValue(payment.getOrderId());

                // Amount (with currency format)
                Cell amountCell = row.createCell(2);
                amountCell.setCellValue(payment.getAmount().doubleValue());
                amountCell.setCellStyle(currencyCellStyle);

                // Payment Method
                Cell methodCell = row.createCell(3);
                methodCell.setCellValue(payment.getMethod());

                // Status (with color-coding)
                Cell statusCell = row.createCell(4);
                statusCell.setCellValue(payment.getStatus());

                switch (payment.getStatus()) {
                    case "Success":
                        statusCell.setCellStyle(successStyle);
                        break;
                    case "Pending":
                        statusCell.setCellStyle(pendingStyle);
                        break;
                    case "Failed":
                        statusCell.setCellStyle(failedStyle);
                        break;
                }

                // Payment Date
                Cell dateCell = row.createCell(5);
                if (payment.getPaymentDate() != null) {
                    dateCell.setCellValue(payment.getPaymentDate().format(dateFormatter));
                }
            }

            // Resize all columns to fit the content
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write the output to file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static void exportUserListToExcel(List<User> users, String filePath) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("User List");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Họ tên");
            headerRow.createCell(2).setCellValue("Email");
            headerRow.createCell(3).setCellValue("SĐT");

            int rowNum = 1;
            for (User user : users) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(user.getUserId());
                row.createCell(1).setCellValue(user.getFullname());
                row.createCell(2).setCellValue(user.getUserEmail());
                row.createCell(3).setCellValue(user.getUserPhoneNumber());
            }

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
