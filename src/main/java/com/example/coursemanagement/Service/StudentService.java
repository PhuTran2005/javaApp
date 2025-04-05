package com.example.coursemanagement.Service;

import com.example.coursemanagement.Models.Student;
import com.example.coursemanagement.Respository.StudentRepository;

import java.util.List;

public class StudentService {
    private StudentRepository studentRepo = new StudentRepository();

    public List<Student> getAllStudents() {
        return studentRepo.getAllStudentsWithCourses();
    }
}
