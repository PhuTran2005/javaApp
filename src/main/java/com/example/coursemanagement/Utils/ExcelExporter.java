package com.example.coursemanagement.Utils;

import com.example.coursemanagement.Models.Student;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

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
                row.createCell(0).setCellValue(student.getStudentId());
                row.createCell(1).setCellValue(student.getStudentName());
                row.createCell(2).setCellValue(student.getStudentEmail());
                row.createCell(3).setCellValue(student.getStudentPhone());

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

}
