package com.example.coursemanagement.Service;

import com.example.coursemanagement.Models.Course;
import com.example.coursemanagement.Repository.CoursesRepository;

import java.util.List;

public class CourseService {
    private final CoursesRepository coursesRepository = new CoursesRepository(); // Tạo repository
    public String getCourseNameById(int courseId){
        return coursesRepository.getCourse(courseId).getCourseName();
    }
    public Double getCoursePriceById(int courseId){
        return coursesRepository.getCourse(courseId).getCoursePrice();
    }


    public Course createNewCourse(Course course) {
        return coursesRepository.addCourse(course);
    }

    public List<Course> getCourseList() {
        return coursesRepository.getAllCourseDetails();
    }
    public List<Course> getCourseListByName(String query) {
        return coursesRepository.getAllCoursesByName(query);
    }

    public boolean deleteCourseById(int courseId) {
        return coursesRepository.deleteCourse(courseId);
    }

    public boolean updateInforCourse(Course course) {
        return coursesRepository.updateCourse(course);
    }

    public Course getCourseById(int courseId) {
        return coursesRepository.getCourse(courseId);
    }


}
