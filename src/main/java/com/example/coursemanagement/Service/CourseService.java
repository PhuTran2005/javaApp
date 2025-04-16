package com.example.coursemanagement.Service;

import com.example.coursemanagement.Dto.CourseDetailDTO;
import com.example.coursemanagement.Models.Course;
import com.example.coursemanagement.Repository.CoursesRepository;

import java.util.List;

public class CourseService {
    private final CoursesRepository coursesRepository = new CoursesRepository(); // Tạo repository

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
