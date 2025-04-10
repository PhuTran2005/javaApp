package com.example.coursemanagement.Service;

import com.example.coursemanagement.Models.Category;
import com.example.coursemanagement.Repository.CategoriesRepository;

import java.util.List;

public class CategoriesService {
    private final CategoriesRepository categoriesRespository = new CategoriesRepository(); // Tạo repository

    public List<Category> getListCategory() {
        return categoriesRespository.getAllCategory();
    }

    public String getCategoryById(int id) {
        return categoriesRespository.getCategoryById(id).getCategoryName();

    }


}
