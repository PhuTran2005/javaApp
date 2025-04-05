package com.example.coursemanagement.Respository;

import com.example.coursemanagement.Models.Category;
import com.example.coursemanagement.Models.Course;
import com.example.coursemanagement.Utils.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CoursesRespository {


    public boolean addCourse(Course course) {
        String sql = "INSERT INTO Courses (courseName, courseDescription, categoryId,courseThumbnail,instructorName,coursePrice) VALUES (?, ?, ?,?,?,?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, course.getCourseName());
            stmt.setString(2, course.getCourseDescription());
            stmt.setInt(3, course.getcategoryId());
            stmt.setString(4, course.getCourseThumbnail());
            stmt.setString(5, course.getInstructorName());
            stmt.setDouble(6, course.getCoursePrice());
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM Courses";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                courses.add(new Course(
                        rs.getInt("courseId"),
                        rs.getInt("categoryId"),
                        rs.getString("courseName"),
                        rs.getString("courseDescription"),
                        rs.getString("courseThumbnail"),
                        rs.getString("instructorName"),
                        rs.getDouble("coursePrice"),
                        rs.getString("created_at")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return courses;
    }
}
