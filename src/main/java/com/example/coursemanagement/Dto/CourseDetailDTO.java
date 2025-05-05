package com.example.coursemanagement.Dto;

import com.example.coursemanagement.Models.Category;
import com.example.coursemanagement.Models.Course;
import com.example.coursemanagement.Models.Instructor;

public class CourseDetailDTO {
    private Course course;
    private Category category;
    private Instructor instructor;

    public CourseDetailDTO(Course course, Category category, Instructor instructor) {
        this.course = course;
        this.category = category;
        this.instructor = instructor;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

// Constructor, Getters, Setters...
}
