package com.example.coursemanagement.Repository;

import com.example.coursemanagement.Models.LearningMaterial;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LearningMaterialRepository {

    public static void insertMaterial(Connection conn, LearningMaterial material) throws SQLException {
        String sql = """
            INSERT INTO LearningMaterials 
                (course_id, title, description, video_path, video_name, document_path, document_name, uploaded_by)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, material.getCourseId());
            stmt.setString(2, material.getTitle());
            stmt.setString(3, material.getDescription());
            stmt.setString(4, material.getVideoPath());
            stmt.setString(5, material.getVideoName());
            stmt.setString(6, material.getDocumentPath());
            stmt.setString(7, material.getDocumentName());
            stmt.setInt(8, material.getUploadedBy());

            stmt.executeUpdate();
        }
    }

    public List<LearningMaterial> getMaterialsByCourseId(Connection conn, int courseId) throws SQLException {
        List<LearningMaterial> list = new ArrayList<>();
        String sql = """
        SELECT m.*, c.course_name AS course_name
        FROM LearningMaterials m
        JOIN Courses c ON m.course_id = c.course_id
        WHERE m.course_id = ?
        ORDER BY m.upload_date DESC
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                LearningMaterial lm = new LearningMaterial();
                lm.setMaterialId(rs.getInt("material_id"));
                lm.setCourseId(rs.getInt("course_id"));
                lm.setTitle(rs.getString("title"));
                lm.setDescription(rs.getString("description"));
                lm.setVideoPath(rs.getString("video_path"));
                lm.setVideoName(rs.getString("video_name"));
                lm.setDocumentPath(rs.getString("document_path"));
                lm.setDocumentName(rs.getString("document_name"));
                lm.setUploadedBy(rs.getInt("uploaded_by"));
                lm.setUploadDate(rs.getTimestamp("upload_date"));
                lm.setViews(rs.getInt("views"));

                lm.setCourseName(rs.getString("course_name")); // <- bổ sung dòng này

                list.add(lm);
            }
        }

        return list;
    }



    // ✅ Thêm phương thức xóa
    public boolean deleteMaterialById(Connection conn, int materialId) throws SQLException {
        String sql = "DELETE FROM LearningMaterials WHERE material_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, materialId);
            return stmt.executeUpdate() > 0;
        }
    }

    // ✅ Thêm phương thức cập nhật
    public boolean updateMaterial(Connection conn, LearningMaterial material) throws SQLException {
        String sql = """
            UPDATE LearningMaterials 
            SET title = ?, description = ?, video_path = ?, video_name = ?, 
                document_path = ?, document_name = ?
            WHERE material_id = ?
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, material.getTitle());
            stmt.setString(2, material.getDescription());
            stmt.setString(3, material.getVideoPath());
            stmt.setString(4, material.getVideoName());
            stmt.setString(5, material.getDocumentPath());
            stmt.setString(6, material.getDocumentName());
            stmt.setInt(7, material.getMaterialId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean increaseViewCount(Connection conn, int materialId) {
        String sql = "UPDATE LearningMaterials SET views = views + 1 WHERE material_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, materialId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



}

