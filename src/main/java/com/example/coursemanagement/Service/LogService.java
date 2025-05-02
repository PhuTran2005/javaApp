package com.example.coursemanagement.Service;

import com.example.coursemanagement.Repository.LogRepository;

public class LogService {
    private final LogRepository logRepository = new LogRepository();

    public void createLog(int userId, String message) {
        logRepository.insertLog(userId, message);
    }


}
