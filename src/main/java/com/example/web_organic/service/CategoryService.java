package com.example.web_organic.service;
import com.example.web_organic.entity.Category;
import com.example.web_organic.modal.Enum.Category_Type;
import com.example.web_organic.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

@Autowired
private CategoryRepository categoryRepository;

    public List<Category> getCategoryByTypeAndStatus(Category_Type type) {
        return categoryRepository.findByTypeAndStatusTrue(type);
    }

    public Category getCategoryBySlug(String type) {
        return categoryRepository.findBySlug(type);
    }
}
