package com.example.coursemanagement.Repository;

import com.example.coursemanagement.Models.Log;
import com.example.coursemanagement.Utils.DatabaseConfig;


import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LogRepository {

    // Insert a new log entry
    public boolean insertLog(Integer userId, String action) {
        String sql = "INSERT INTO Logs (user_id, action) VALUES (?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (userId == null) {
                pstmt.setNull(1, Types.INTEGER);
            } else {
                pstmt.setInt(1, userId);
            }
            pstmt.setString(2, action);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get logs with pagination
    public List<Log> getLogs(int page, int pageSize) {
        List<Log> logs = new ArrayList<>();
        String sql = "SELECT log_id, user_id, action, action_time FROM Logs " +
                "ORDER BY action_time DESC " +
                "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, (page - 1) * pageSize);
            pstmt.setInt(2, pageSize);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Log log = new Log();
                    log.setLogId(rs.getInt("log_id"));

                    int userId = rs.getInt("user_id");
                    if (rs.wasNull()) {
                        log.setUserId(null);
                    } else {
                        log.setUserId(userId);
                    }

                    log.setAction(rs.getString("action"));
                    log.setActionTime(rs.getTimestamp("action_time").toLocalDateTime());
                    logs.add(log);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return logs;
    }

    // Get total count of logs for pagination
    public int getTotalLogsCount() {
        String sql = "SELECT COUNT(*) FROM Logs";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    // Search logs with pagination
    public List<Log> searchLogs(String searchTerm, int page, int pageSize) {
        List<Log> logs = new ArrayList<>();
        String sql = "SELECT log_id, user_id, action, action_time FROM Logs " +
                "WHERE action LIKE ? OR CAST(user_id AS NVARCHAR) LIKE ? " +
                "ORDER BY action_time DESC " +
                "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setInt(3, (page - 1) * pageSize);
            pstmt.setInt(4, pageSize);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Log log = new Log();
                    log.setLogId(rs.getInt("log_id"));

                    int userId = rs.getInt("user_id");
                    if (rs.wasNull()) {
                        log.setUserId(null);
                    } else {
                        log.setUserId(userId);
                    }

                    log.setAction(rs.getString("action"));
                    log.setActionTime(rs.getTimestamp("action_time").toLocalDateTime());
                    logs.add(log);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return logs;
    }

    // Get search results count for pagination
    public int getSearchResultsCount(String searchTerm) {
        String sql = "SELECT COUNT(*) FROM Logs " +
                "WHERE action LIKE ? OR CAST(user_id AS NVARCHAR) LIKE ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
}