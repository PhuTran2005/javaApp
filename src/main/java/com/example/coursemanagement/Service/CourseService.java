package com.example.coursemanagement.Service;

import com.example.coursemanagement.Dto.CourseDetailDTO;
import com.example.coursemanagement.Models.Category;
import com.example.coursemanagement.Models.Course;
import com.example.coursemanagement.Models.Instructor;
import com.example.coursemanagement.Models.PageResult;
import com.example.coursemanagement.Repository.CoursesRepository;
import com.example.coursemanagement.Utils.DatabaseConfig;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import java.sql.*;

public class CourseService {
    private final CoursesRepository coursesRepository = new CoursesRepository(); // Tạo repository
    private Connection connection;

    public CourseService() {
        connection = DatabaseConfig.getConnection();
    }

    // Phương thức lấy danh sách khóa học có phân trang
    public PageResult<CourseDetailDTO> getPaginatedCourseList(int pageNumber, int pageSize) {
        List<CourseDetailDTO> courses = new ArrayList<>();
        int totalRecords = 0;
        int totalPages = 0;

        try {
            CallableStatement statement = connection.prepareCall("{call GetPaginatedCourses(?, ?, ?)}");
            statement.setInt(1, pageNumber);
            statement.setInt(2, pageSize);
            statement.registerOutParameter(3, Types.INTEGER);

            boolean hasResults = statement.execute();

            if(hasResults) {
                ResultSet rs = statement.getResultSet();
                while (rs.next()) {
                    Course course = new Course(rs.getInt("course_id"), rs.getInt("category_id"), rs.getInt("user_id"), rs.getString("course_name"), rs.getString("course_description"), rs.getString("course_thumbnail"), rs.getDouble("fee"), rs.getString("course_create_date"), rs.getDate("start_date").toLocalDate(), rs.getDate("end_date").toLocalDate(), rs.getBoolean("is_deleted"));
                    Category category = new Category(rs.getInt("category_id"), rs.getString("category_name"), rs.getString("category_description"));
                    Instructor instructor = new Instructor(rs.getInt("user_id"), rs.getString("instructor_email"), rs.getString("instructor_name"), rs.getInt("role_id"), rs.getString("phonenumber"), rs.getString("create_date"), rs.getString("specialty"), rs.getString("degree"), rs.getInt("years_of_experience"));
                    CourseDetailDTO dto = new CourseDetailDTO(course, category, instructor);
                    courses.add(dto);

                }
                rs.close();
            }

            totalRecords = statement.getInt(3);
            totalPages = (int) Math.ceil((double) totalRecords / pageSize);

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new PageResult<>(courses, pageNumber, pageSize, totalPages, totalRecords);
    }

    // Phương thức tìm kiếm khóa học có phân trang
    public PageResult<CourseDetailDTO> searchPaginatedCourses(String query, int pageNumber, int pageSize) {
        List<CourseDetailDTO> courses = new ArrayList<>();
        int totalRecords = 0;
        int totalPages = 0;

        try {
            CallableStatement statement = connection.prepareCall("{call SearchPaginatedCourses(?, ?, ?, ?)}");
            statement.setString(1, query);
            statement.setInt(2, pageNumber);
            statement.setInt(3, pageSize);
            statement.registerOutParameter(4, Types.INTEGER);

            boolean hasResults = statement.execute();

            if (hasResults) {
                ResultSet rs = statement.getResultSet();
                while (rs.next()) {
                    Course course = new Course(rs.getInt("course_id"), rs.getInt("category_id"), rs.getInt("user_id"), rs.getString("course_name"), rs.getString("course_description"), rs.getString("course_thumbnail"), rs.getDouble("fee"), rs.getString("course_create_date"), rs.getDate("start_date").toLocalDate(), rs.getDate("end_date").toLocalDate(), rs.getBoolean("is_deleted"));

                    Category category = new Category(rs.getInt("category_id"), rs.getString("category_name"), rs.getString("category_description"));
                    Instructor instructor = new Instructor(rs.getInt("user_id"), rs.getString("instructor_email"), rs.getString("instructor_name"), rs.getInt("role_id"), rs.getString("phonenumber"), rs.getString("create_date"), rs.getString("specialty"), rs.getString("degree"), rs.getInt("years_of_experience"));
                    CourseDetailDTO dto = new CourseDetailDTO(course, category, instructor);
                    courses.add(dto);

                }
                rs.close();
            }

            totalRecords = statement.getInt(4);
            totalPages = (int) Math.ceil((double) totalRecords / pageSize);

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new PageResult<>(courses, pageNumber, pageSize, totalPages, totalRecords);
    }

    // Phương thức lọc khóa học theo danh mục có phân trang
    public PageResult<CourseDetailDTO> filterPaginatedCoursesByCategory(int categoryId, int pageNumber, int pageSize) {
        List<CourseDetailDTO> courses = new ArrayList<>();
        int totalRecords = 0;
        int totalPages = 0;

        try {
            CallableStatement statement = connection.prepareCall("{call FilterPaginatedCoursesByCategory(?, ?, ?, ?)}");
            statement.setInt(1, categoryId);
            statement.setInt(2, pageNumber);
            statement.setInt(3, pageSize);
            statement.registerOutParameter(4, Types.INTEGER);

            boolean hasResults = statement.execute();

            if (hasResults) {
                ResultSet rs = statement.getResultSet();
                while (rs.next()) {
                    Course course = new Course(rs.getInt("course_id"), rs.getInt("category_id"), rs.getInt("user_id"), rs.getString("course_name"), rs.getString("course_description"), rs.getString("course_thumbnail"), rs.getDouble("fee"), rs.getString("course_create_date"), rs.getDate("start_date").toLocalDate(), rs.getDate("end_date").toLocalDate(), rs.getBoolean("is_deleted"));

                    Category category = new Category(rs.getInt("category_id"), rs.getString("category_name"), rs.getString("category_description"));
                    Instructor instructor = new Instructor(rs.getInt("user_id"), rs.getString("instructor_email"), rs.getString("instructor_name"), rs.getInt("role_id"), rs.getString("phonenumber"), rs.getString("create_date"), rs.getString("specialty"), rs.getString("degree"), rs.getInt("years_of_experience"));
                    CourseDetailDTO dto = new CourseDetailDTO(course, category, instructor);
                    courses.add(dto);

                }
                rs.close();
            }

            totalRecords = statement.getInt(4);
            totalPages = (int) Math.ceil((double) totalRecords / pageSize);

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new PageResult<>(courses, pageNumber, pageSize, totalPages, totalRecords);
    }

    // Phương thức cũ hỗ trợ compatibility
    public List<CourseDetailDTO> getPageCourseList(int action) {
        // Gọi phương thức phân trang với mặc định trang 1
        PageResult<CourseDetailDTO> pageResult = getPaginatedCourseList(1, 8);
        return pageResult.getItems();
    }

    // Phương thức cũ hỗ trợ compatibility
    public List<CourseDetailDTO> getPageCourseListByName(String name) {
        // Gọi phương thức tìm kiếm với phân trang mặc định trang 1
        PageResult<CourseDetailDTO> pageResult = searchPaginatedCourses(name, 1, 8);
        return pageResult.getItems();
    }

    // Phương thức mapping từ ResultSet sang CourseDetailDTO

    public boolean isExistCourse(int userId, int courseId) {
        return coursesRepository.checkBuyCourse(userId, courseId);
    }

    public String getCourseNameById(int courseId) {
        return coursesRepository.getCourse(courseId).getCourse().getCourseName();
    }

    public Double getCoursePriceById(int courseId) {
        return coursesRepository.getCourse(courseId).getCourse().getCoursePrice();
    }

    public Course createNewCourse(Course course) {
        return coursesRepository.addCourse(course);
    }

    public List<CourseDetailDTO> getCourseList(int flag) {
        return coursesRepository.getAllCourseDetails(flag);
    }
    public List<CourseDetailDTO> getCourseListByInstructorId(int instructorId) {
        return coursesRepository.getCoursesByInstructor(instructorId);
    }

    public List<CourseDetailDTO> getCourseListByName(String query) {
        return coursesRepository.getAllCoursesByName(query);
    }

    public boolean deleteCourseById(int courseId) {
        return coursesRepository.softDeleteCourse(courseId);
    }

    public boolean recoveryCourse(int courseId) {
        return coursesRepository.recoverCourse(courseId);
    }

    public boolean updateInforCourse(Course course) {
        return coursesRepository.updateCourse(course);
    }

    public CourseDetailDTO getCourseById(int courseId) {
        return coursesRepository.getCourse(courseId);
    }


}
