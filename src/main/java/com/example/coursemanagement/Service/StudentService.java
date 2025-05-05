package com.example.coursemanagement.Service;

import com.example.coursemanagement.Models.Student;
import com.example.coursemanagement.Repository.StudentRepository;

import java.math.BigDecimal;
import java.util.List;

public class StudentService {
    private StudentRepository studentRepo = new StudentRepository();

    public List<Student> getAllStudents() {
        return studentRepo.getAllStudentsWithCourses();
    }
    public List<String> getAllCourses() {
        return studentRepo.getAllCourses(); // Sử dụng phương thức từ StudentRepository
    }

    public Student getStudentById(int studentId) {
        return null;
    }

    public BigDecimal getBalance(int studentId) {
        return null;
    }
}
