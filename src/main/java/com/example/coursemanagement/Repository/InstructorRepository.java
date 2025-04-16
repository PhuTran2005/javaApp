package com.example.coursemanagement.Repository;

import com.example.coursemanagement.Models.Category;
import com.example.coursemanagement.Models.Instructor;
import com.example.coursemanagement.Models.User;
import com.example.coursemanagement.Utils.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InstructorRepository {
    public List<Instructor> getAllInstructor() {
        List<Instructor> instructors = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection()) {
            String sql = "SELECT * FROM Users u " +
                    "join Instructors i on i.instructor_id = u.user_id " +
                    " where role_id = 2";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                instructors.add(new Instructor(rs.getInt("user_id"), rs.getString("email"), rs.getString("full_name"), rs.getInt("role_id"), rs.getString("phonenumber"), rs.getString("create_date"), rs.getString("specialty"), rs.getString("degree"), rs.getInt("years_of_experience")));
            }
            return instructors;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public void addCourseAndInstructors(int courseId, int instructorId) {
        String sql = "INSERT INTO Course_Instructors (course_id, user_id) VALUES (?, ?)";

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
        String sql = "UPDATE Course_Instructors SET user_id = ? WHERE course_id = ?";

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

    public boolean updateInforInstructor(Instructor instructor) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false); // Bắt đầu transaction

            // Cập nhật bảng Users
            String userSql = "UPDATE Users SET full_name = ?, phonenumber = ? WHERE user_id = ?";
            try (PreparedStatement userStmt = conn.prepareStatement(userSql)) {
                userStmt.setString(1, instructor.getFullname());
                userStmt.setString(2, instructor.getUserPhoneNumber());
                userStmt.setInt(3, instructor.getUserId());

                if (userStmt.executeUpdate() == 0) {
                    conn.rollback();
                    return false;
                }
            }

            // Cập nhật bảng Instructors
            String instructorSql = "UPDATE Instructors SET specialty = ?, degree = ?, years_of_experience = ? WHERE instructor_id = ?";
            try (PreparedStatement instStmt = conn.prepareStatement(instructorSql)) {
                instStmt.setString(1, instructor.getExpertise());
                instStmt.setString(2, instructor.getDegree());
                instStmt.setInt(3, instructor.getYears_of_experience());
                instStmt.setInt(4, instructor.getUserId());

                if (instStmt.executeUpdate() == 0) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit(); // Thành công tất cả -> commit
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                // Nếu gặp lỗi thì rollback
                Connection conn = DatabaseConfig.getConnection();
                if (conn != null && !conn.getAutoCommit()) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }


    public Instructor getInstructorByEmail(String email) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String sql = "SELECT * FROM users u " +
                    "join Instructors i on i.instructor_id = u.user_id " +
                    "WHERE u.email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Instructor(rs.getInt("user_id"), rs.getString("email"), rs.getString("full_name"), rs.getInt("role_id"), rs.getString("phonenumber"), rs.getString("create_date"), rs.getString("specialty"), rs.getString("degree"), rs.getInt("years_of_experience"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
