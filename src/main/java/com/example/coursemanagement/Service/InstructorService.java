package com.example.coursemanagement.Service;

import com.example.coursemanagement.Models.Instructor;
import com.example.coursemanagement.Repository.InstructorRepository;

import java.util.List;

public class InstructorService {
    private final InstructorRepository instructorRepository = new InstructorRepository();

    public List<Instructor> getAllInstructor() {
        return instructorRepository.getAllInstructor();
    }

    public void relCourseAndInstructor(int courseId, int instructorId) {
        instructorRepository.addCourseAndInstructors(courseId, instructorId);
    }

    public boolean updateRelCourseAndInstructor(int courseId, int instructorId) {
        return instructorRepository.updateCourseInstructor(courseId, instructorId);

    }


}
