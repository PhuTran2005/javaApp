package com.example.coursemanagement.Service;

import com.example.coursemanagement.Models.Course;
import com.example.coursemanagement.Respository.CoursesRespository;
import com.example.coursemanagement.Respository.UserRespository;

import java.util.List;

public class CourseService {
    private final CoursesRespository coursesRespository = new CoursesRespository(); // Tạo repository


    public boolean createNewCourse(Course course){
        return coursesRespository.addCourse(course);
    }
    public List<Course> getCourseList(){
        return coursesRespository.getAllCourses();
    }


}
