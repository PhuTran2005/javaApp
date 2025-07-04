package com.example.coursemanagement.Models;

import java.time.LocalDateTime;

public class Log {
    private int logId;
    private Integer userId;
    private String action;
    private LocalDateTime actionTime;
    private String email;
    private String role;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Log(int logId, Integer userId, String action, LocalDateTime actionTime, String email, String role) {
        this.logId = logId;
        this.userId = userId;
        this.action = action;
        this.actionTime = actionTime;
        this.email = email;
        this.role = role;
    }

    public Log() {
    }

    public Log(int logId, Integer userId, String action, LocalDateTime actionTime) {
        this.logId = logId;
        this.userId = userId;
        this.action = action;
        this.actionTime = actionTime;
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public LocalDateTime getActionTime() {
        return actionTime;
    }

    public void setActionTime(LocalDateTime actionTime) {
        this.actionTime = actionTime;
    }

    @Override
    public String toString() {
        return "Log{" +
                "logId=" + logId +
                ", userId=" + userId +
                ", action='" + action + '\'' +
                ", actionTime=" + actionTime +
                '}';
    }
}