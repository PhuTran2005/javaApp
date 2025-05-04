package com.example.coursemanagement.Repository;

import com.example.coursemanagement.Models.Category;
import com.example.coursemanagement.Models.Course;
import com.example.coursemanagement.Models.Instructor;
import com.example.coursemanagement.Utils.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CoursesRepository {

    public Course addCourse(Course course) {
        String sql = "INSERT INTO Courses (courseName, courseDescription, categoryId, courseThumbnail, instructorId, coursePrice) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, course.getCourseName());
            stmt.setString(2, course.getCourseDescription());
            stmt.setInt(3, course.getCategoryId());
            stmt.setString(4, course.getCourseThumbnail());
            stmt.setInt(5, course.getInstructorId());
            stmt.setDouble(6, course.getCoursePrice());

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int newCourseId = generatedKeys.getInt(1);
                    course.setCourseId(newCourseId);
                }
                return course;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Course> getAllCourseDetails() {
        List<Course> list = new ArrayList<>();

        // không join tới Categories vì bảng không tồn tại
        String sql = "SELECT c.courseId, c.categoryId, c.instructorId, c.courseName, c.courseDescription, c.courseThumbnail, c.coursePrice, " +
                "ins.instructorName, ins.expertise, ins.instructorEmail, ins.instructorPhone " +
                "FROM Courses c " +
                "LEFT JOIN Instructors ins ON c.instructorId = ins.instructorId";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // Tạo Category đơn giản chỉ với ID (name và description không có dữ liệu)
                Category category = new Category(rs.getInt("categoryId"), "", "");
                Instructor instructor = new Instructor(
                        rs.getInt("instructorId"),
                        rs.getString("instructorName"),
                        rs.getString("expertise"),
                        rs.getString("instructorEmail"),
                        rs.getString("instructorPhone")
                );
                Course dto = new Course(
                        rs.getInt("courseId"),
                        rs.getInt("categoryId"),
                        rs.getInt("instructorId"),
                        rs.getString("courseName"),
                        rs.getString("courseDescription"),
                        rs.getString("courseThumbnail"),
                        rs.getDouble("coursePrice"),
                        category,
                        instructor
                );

                list.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Course> getAllCoursesByName(String query) {
        List<Course> courses = new ArrayList<>();
        // thêm dấu cách trước WHERE
        String sql = "SELECT c.courseId, c.categoryId, c.instructorId, c.courseName, c.courseDescription, c.courseThumbnail, c.coursePrice, " +
                "ins.instructorName, ins.expertise, ins.instructorEmail, ins.instructorPhone " +
                "FROM Courses c " +
                "LEFT JOIN Instructors ins ON c.instructorId = ins.instructorId " + // bỏ join Categories
                "WHERE courseName LIKE ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + query + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Category category = new Category(rs.getInt("categoryId"), "", ""); // Đã sửa
                    Instructor instructor = new Instructor(
                            rs.getInt("instructorId"),
                            rs.getString("instructorName"),
                            rs.getString("expertise"),
                            rs.getString("instructorEmail"),
                            rs.getString("instructorPhone")
                    );
                    Course dto = new Course(
                            rs.getInt("courseId"),
                            rs.getInt("categoryId"),
                            rs.getInt("instructorId"),
                            rs.getString("courseName"),
                            rs.getString("courseDescription"),
                            rs.getString("courseThumbnail"),
                            rs.getDouble("coursePrice"),
                            category,
                            instructor
                    );
                    courses.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return courses;
    }

    public Course getCourse(int courseId) {
        // thêm khoảng trắng trước where và bỏ join Categories
        String sql = "SELECT c.courseId, c.categoryId, c.instructorId, c.courseName, c.courseDescription, c.courseThumbnail, c.coursePrice, " +
                "ins.instructorName, ins.expertise, ins.instructorEmail, ins.instructorPhone " +
                "FROM Courses c " +
                "LEFT JOIN Instructors ins ON c.instructorId = ins.instructorId " +
                "WHERE courseId = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Category category = new Category(rs.getInt("categoryId"), "", ""); // Đã sửa
                    Instructor instructor = new Instructor(
                            rs.getInt("instructorId"),
                            rs.getString("instructorName"),
                            rs.getString("expertise"),
                            rs.getString("instructorEmail"),
                            rs.getString("instructorPhone")
                    );
                    Course dto = new Course(
                            rs.getInt("courseId"),
                            rs.getInt("categoryId"),
                            rs.getInt("instructorId"),
                            rs.getString("courseName"),
                            rs.getString("courseDescription"),
                            rs.getString("courseThumbnail"),
                            rs.getDouble("coursePrice"),
                            category,
                            instructor
                    );
                    return dto;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean deleteCourse(int courseId) {
        String query = "DELETE FROM Courses WHERE courseId = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, courseId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCourse(Course course) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String sql = "UPDATE Courses SET courseName = ?, instructorId = ?, categoryId = ?, coursePrice = ?, courseThumbnail = ?, courseDescription = ? WHERE courseId = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, course.getCourseName());
            stmt.setInt(2, course.getInstructorId());
            stmt.setInt(3, course.getCategoryId());
            stmt.setDouble(4, course.getCoursePrice());
            stmt.setString(5, course.getCourseThumbnail());
            stmt.setString(6, course.getCourseDescription());
            stmt.setInt(7, course.getCourseId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
