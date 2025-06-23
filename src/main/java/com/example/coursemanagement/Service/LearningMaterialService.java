package com.example.coursemanagement.Service;

import com.example.coursemanagement.Models.LearningMaterial;
import com.example.coursemanagement.Repository.LearningMaterialRepository;
import com.example.coursemanagement.Utils.DatabaseConfig;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class LearningMaterialService {

    private final LearningMaterialRepository repository = new LearningMaterialRepository();

    // Lấy danh sách bài giảng theo courseId
    public List<LearningMaterial> getMaterialsByCourseId(int courseId) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            return repository.getMaterialsByCourseId(conn, courseId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Thêm bài giảng mới
    public boolean insertMaterial(LearningMaterial material) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            LearningMaterialRepository.insertMaterial(conn, material);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật bài giảng
    public boolean updateMaterial(LearningMaterial material) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            return repository.updateMaterial(conn, material);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa bài giảng
    public boolean deleteMaterial(int materialId) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            return repository.deleteMaterialById(conn, materialId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean increaseViewCount(int materialId) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            return new LearningMaterialRepository().increaseViewCount(conn, materialId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
