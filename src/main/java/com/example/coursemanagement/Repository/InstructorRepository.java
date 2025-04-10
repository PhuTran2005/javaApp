package com.example.coursemanagement.Repository;

import com.example.coursemanagement.Models.Category;
import com.example.coursemanagement.Models.Instructor;
import com.example.coursemanagement.Utils.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

public class InstructorRepository {
    public List<Instructor> getAllInstructor() {
        List<Instructor> instructors = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection()) {
            String sql = "SELECT * FROM Instructors";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                instructors.add(new Instructor(rs.getInt("instructorId"), rs.getString("instructorName"), rs.getString("expertise"),rs.getString("instructorEmail"),rs.getString("instructorPhone")));
            }
            return instructors;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
    public void addCourseAndInstructors(int courseId, int instructorId) {
        String sql = "INSERT INTO Course_Instructors (courseId, instructorId) VALUES (?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);
            stmt.setInt(2, instructorId);
            stmt.executeUpdate();

            System.out.println("Đã gán instructorId = " + instructorId + " cho courseId = " + courseId);

        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("⚠️ Instructor này đã dạy khóa học này!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean updateCourseInstructor(int courseId, int instructorId) {
        String sql = "UPDATE Course_Instructors SET instructorId = ? WHERE courseId = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, instructorId);
            stmt.setInt(2, courseId);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }



}
