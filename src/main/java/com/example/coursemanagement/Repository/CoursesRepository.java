package com.example.coursemanagement.Repository;

import com.example.coursemanagement.Dto.CourseDetailDTO;
import com.example.coursemanagement.Models.Category;
import com.example.coursemanagement.Models.Course;
import com.example.coursemanagement.Models.Instructor;
import com.example.coursemanagement.Utils.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CoursesRepository {


    public Course addCourse(Course course) {
        String courseSql = "INSERT INTO Courses (course_name, description, category_id, course_thumbnail, instructor_id, fee, start_date, end_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String mappingSql = "INSERT INTO Course_Instructors (course_id, user_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false); // 🔄 Bắt đầu giao dịch

            // Thêm khóa học
            try (PreparedStatement courseStmt = conn.prepareStatement(courseSql, Statement.RETURN_GENERATED_KEYS)) {
                courseStmt.setString(1, course.getCourseName());
                courseStmt.setString(2, course.getCourseDescription());
                courseStmt.setInt(3, course.getCategoryId());
                courseStmt.setString(4, course.getCourseThumbnail());
                courseStmt.setInt(5, course.getInstructorId());
                courseStmt.setDouble(6, course.getCoursePrice());
                courseStmt.setDate(7, java.sql.Date.valueOf(course.getStartDate()));
                courseStmt.setDate(8, java.sql.Date.valueOf(course.getEndDate()));

                int rowsInserted = courseStmt.executeUpdate();
                if (rowsInserted == 0) throw new SQLException("Không thể thêm khóa học!");

                // Lấy ID vừa tạo
                try (ResultSet generatedKeys = courseStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int newCourseId = generatedKeys.getInt(1);
                        course.setCourseId(newCourseId);

                        // Gán instructor vào khóa học (bảng phụ)
                        try (PreparedStatement mappingStmt = conn.prepareStatement(mappingSql)) {
                            mappingStmt.setInt(1, newCourseId);
                            mappingStmt.setInt(2, course.getInstructorId());
                            mappingStmt.executeUpdate();
                        }
                    } else {
                        throw new SQLException("Không lấy được ID khóa học!");
                    }
                }
            }

            conn.commit(); // ✅ Commit nếu mọi thứ thành công
            return course;

        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("⚠️ Instructor đã tồn tại trong khóa học này!");
            // Có thể rollback nếu cần
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<CourseDetailDTO> getAllCourseDetails(int check) {
        List<CourseDetailDTO> list = new ArrayList<>();

        String sql = "SELECT " +
                "    c.course_id, " +
                "    c.course_name, " +
                "    c.description AS course_description, " +
                "    c.fee, " +
                "    c.start_date, " +
                "    c.end_date, " +
                "    c.create_date AS course_create_date, " +
                "    c.course_thumbnail, " +
                "    c.is_deleted, " +

                // Instructor Info
                "    i.specialty, " +
                "    i.degree, " +
                "    i.years_of_experience, " +

                // User Info (người giảng dạy)
                "    u.user_id, " +
                "    u.full_name AS instructor_name, " +
                "    u.email AS instructor_email, " +
                "    u.role_id, " +
                "    u.phonenumber, " +
                "    u.create_date, " +

                // Category Info
                "    cat.category_id, " +
                "    cat.category_name, " +
                "    cat.description AS category_description " +  // ← Không có dấu phẩy ở cuối dòng này

                "FROM Courses c " +  // ← Nhớ thêm khoảng trắng
                "LEFT JOIN Instructors i ON c.instructor_id = i.instructor_id " +
                "LEFT JOIN Users u ON i.instructor_id = u.user_id " +
                "LEFT JOIN Categories cat ON c.category_id = cat.category_id " +
                "where c.is_deleted = ? ";


        try (Connection conn = DatabaseConfig.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, check);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Course course = new Course(rs.getInt("course_id"), rs.getInt("category_id"), rs.getInt("user_id"), rs.getString("course_name"), rs.getString("course_description"), rs.getString("course_thumbnail"), rs.getDouble("fee"), rs.getString("course_create_date"), rs.getDate("start_date").toLocalDate(), rs.getDate("end_date").toLocalDate(), rs.getBoolean("is_deleted"));
                Category category = new Category(rs.getInt("category_id"), rs.getString("category_name"), rs.getString("category_description"));
                Instructor instructor = new Instructor(rs.getInt("user_id"), rs.getString("instructor_email"), rs.getString("instructor_name"), rs.getInt("role_id"), rs.getString("phonenumber"), rs.getString("create_date"), rs.getString("specialty"), rs.getString("degree"), rs.getInt("years_of_experience"));
                CourseDetailDTO dto = new CourseDetailDTO(course, category, instructor);
                list.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }


    public List<CourseDetailDTO> getAllCoursesByName(String query) {
        List<CourseDetailDTO> list = new ArrayList<>();

        String sql = "SELECT " +
                "    c.course_id, " +
                "    c.course_name, " +
                "    c.description AS course_description, " +
                "    c.fee, " +
                "    c.start_date, " +
                "    c.end_date, " +
                "    c.create_date AS course_create_date, " +
                "    c.course_thumbnail, " +
                "    c.is_deleted, " +

                // Instructor Info
                "    i.specialty, " +
                "    i.degree, " +
                "    i.years_of_experience, " +

                // User Info (người giảng dạy)
                "    u.user_id, " +
                "    u.full_name AS instructor_name, " +
                "    u.email AS instructor_email, " +
                "    u.role_id, " +
                "    u.phonenumber, " +
                "    u.create_date, " +

                // Category Info
                "    cat.category_id, " +
                "    cat.category_name, " +
                "    cat.description AS category_description " +  // ← Không có dấu phẩy ở cuối dòng này

                "FROM Courses c " +  // ← Nhớ thêm khoảng trắng
                "LEFT JOIN Instructors i ON c.instructor_id = i.instructor_id " +
                "LEFT JOIN Users u ON i.instructor_id = u.user_id " +
                "LEFT JOIN Categories cat ON c.category_id = cat.category_id " +
                "WHERE c.course_name LIKE ? and c.is_deleted = 0;";


        try (Connection conn = DatabaseConfig.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + query + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Course course = new Course(rs.getInt("course_id"), rs.getInt("category_id"), rs.getInt("user_id"), rs.getString("course_name"), rs.getString("course_description"), rs.getString("course_thumbnail"), rs.getDouble("fee"), rs.getString("course_create_date"), rs.getDate("start_date").toLocalDate(), rs.getDate("end_date").toLocalDate(), rs.getBoolean("is_deleted"));

                Category category = new Category(rs.getInt("category_id"), rs.getString("category_name"), rs.getString("category_description"));
                Instructor instructor = new Instructor(rs.getInt("user_id"), rs.getString("instructor_email"), rs.getString("instructor_name"), rs.getInt("role_id"), rs.getString("phonenumber"), rs.getString("create_date"), rs.getString("specialty"), rs.getString("degree"), rs.getInt("years_of_experience"));
                CourseDetailDTO dto = new CourseDetailDTO(course, category, instructor);
                list.add(dto);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public CourseDetailDTO getCourse(int courseId) {
        String sql = "SELECT " +
                "    c.course_id, " +
                "    c.course_name, " +
                "    c.description AS course_description, " +
                "    c.fee, " +
                "    c.start_date, " +
                "    c.end_date, " +
                "    c.create_date AS course_create_date, " +
                "    c.course_thumbnail, " +
                "    c.is_deleted, " +

                // Instructor Info
                "    i.specialty, " +
                "    i.degree, " +
                "    i.years_of_experience, " +

                // User Info (người giảng dạy)
                "    u.user_id, " +
                "    u.full_name AS instructor_name, " +
                "    u.email AS instructor_email, " +
                "    u.role_id, " +
                "    u.phonenumber, " +
                "    u.create_date, " +

                // Category Info
                "    cat.category_id, " +
                "    cat.category_name, " +
                "    cat.description AS category_description " +  // ← Không có dấu phẩy ở cuối dòng này

                "FROM Courses c " +  // ← Nhớ thêm khoảng trắng
                "LEFT JOIN Instructors i ON c.instructor_id = i.instructor_id " +
                "LEFT JOIN Users u ON i.instructor_id = u.user_id " +
                "LEFT JOIN Categories cat ON c.category_id = cat.category_id "

                + "where c.course_id = ? and c.is_deleted = 0";
        try (Connection conn = DatabaseConfig.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Course course = new Course(rs.getInt("course_id"), rs.getInt("category_id"), rs.getInt("user_id"), rs.getString("course_name"), rs.getString("course_description"), rs.getString("course_thumbnail"), rs.getDouble("fee"), rs.getString("course_create_date"), rs.getDate("start_date").toLocalDate(), rs.getDate("end_date").toLocalDate(), rs.getBoolean("is_deleted"));

                Category category = new Category(rs.getInt("category_id"), rs.getString("category_name"), rs.getString("category_description"));
                Instructor instructor = new Instructor(rs.getInt("user_id"), rs.getString("instructor_email"), rs.getString("instructor_name"), rs.getInt("role_id"), rs.getString("phonenumber"), rs.getString("create_date"), rs.getString("specialty"), rs.getString("degree"), rs.getInt("years_of_experience"));
                CourseDetailDTO dto = new CourseDetailDTO(course, category, instructor);
                return dto;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean softDeleteCourse(int courseId) {
        String query = "update Courses set is_deleted = 1 WHERE course_id = ?";

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

    public boolean recoverCourse(int courseId) {
        String query = "update Courses set is_deleted = 0 WHERE course_id = ?";
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
            String sql = "UPDATE Courses SET course_name = ?, instructor_id = ?,category_id = ?,fee = ?,course_thumbnail = ?,description = ?, start_date = ?,end_date = ? WHERE course_id = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, course.getCourseName());
            stmt.setInt(2, course.getInstructorId());
            stmt.setInt(3, course.getCategoryId());
            stmt.setDouble(4, course.getCoursePrice());
            stmt.setString(5, course.getCourseThumbnail());
            stmt.setString(6, course.getCourseDescription());
            stmt.setDate(7, java.sql.Date.valueOf(course.getStartDate()));
            stmt.setDate(8, java.sql.Date.valueOf(course.getEndDate()));
            stmt.setInt(9, course.getCourseId());

            return stmt.executeUpdate() > 0; // Nếu có ít nhất 1 dòng được cập nhật
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


}
