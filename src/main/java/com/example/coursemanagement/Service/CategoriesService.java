package com.example.coursemanagement.Service;

import com.example.coursemanagement.Models.Category;
import com.example.coursemanagement.Respository.CategoriesRespository;
import com.example.coursemanagement.Respository.UserRespository;

import java.util.List;

public class CategoriesService {
    private final CategoriesRespository categoriesRespository = new CategoriesRespository(); // Tạo repository

    public List<Category> getListCategory() {
        return categoriesRespository.getAllCategory();
    }

    public String getCategoryById(int id) {
        return categoriesRespository.getCategoryById(id).getCategoryName();

    }


}
