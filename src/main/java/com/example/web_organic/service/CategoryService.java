package com.example.web_organic.service;
import com.example.web_organic.entity.Category;
import com.example.web_organic.entity.User;
import com.example.web_organic.modal.Enum.Category_Type;
import com.example.web_organic.modal.Enum.User_Role;
import com.example.web_organic.modal.request.UpSertCategoryRequestAdmin;
import com.example.web_organic.repository.CategoryRepository;
import com.github.slugify.Slugify;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

@Autowired
private CategoryRepository categoryRepository;
@Autowired
private HttpSession httpSession;
    public List<Category> getCategoryByTypeAndStatus(Category_Type type) {
        return categoryRepository.findByTypeAndStatusTrue(type);
    }

    public Category getCategoryBySlug(String type) {
        return categoryRepository.findBySlug(type);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }


    public Category createCategory(UpSertCategoryRequestAdmin upSertCategoryRequestAdmin) {
        User user = (User) httpSession.getAttribute("CURRENT_USER");
        if (user == null) {
           throw new RuntimeException("User not logged in");
        }

        if (user.getRole() != User_Role.ADMIN) {
            throw new RuntimeException("You do not have permission to perform this action");
        }

        Optional<Category> existingCategory = Optional.ofNullable(categoryRepository.findByName(upSertCategoryRequestAdmin.getName()));
        if (existingCategory.isPresent()) {
            throw new RuntimeException("Category already exists");
        }
// Tạo slug từ tên danh mục
        Slugify slugify = Slugify.builder().build();
        String slug = slugify.slugify(upSertCategoryRequestAdmin.getName());


        Category category = new Category();
        category.setName(upSertCategoryRequestAdmin.getName());
        category.setType(upSertCategoryRequestAdmin.getType());
        category.setStatus(upSertCategoryRequestAdmin.getStatus());
        category.setSlug(slug);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        categoryRepository.save(category);
        return category;
    }

    public Category updateCategory(Integer id,UpSertCategoryRequestAdmin upSertCategoryRequestAdmin) {
        User user = (User) httpSession.getAttribute("CURRENT_USER");
        if (user == null) {
            throw new RuntimeException("User not logged in");
        }

        if (user.getRole() != User_Role.ADMIN) {
            throw new RuntimeException("You do not have permission to perform this action");
        }

        Optional<Category> existingCategory = categoryRepository.findById(id);
        if (existingCategory.isEmpty()) {
            throw new RuntimeException("Category not found");
        }
        //check if category already exists
        Optional<Category> existingCategoryName = Optional.ofNullable(categoryRepository.findByName(upSertCategoryRequestAdmin.getName()));
        if (existingCategoryName.isPresent() && !existingCategoryName.get().getId().equals(id)) {
            throw new RuntimeException("Category already exists");
        }
        Category category = existingCategory.get();
        category.setName(upSertCategoryRequestAdmin.getName());
        category.setType(upSertCategoryRequestAdmin.getType());
        category.setStatus(upSertCategoryRequestAdmin.getStatus());
        category.setSlug(upSertCategoryRequestAdmin.getName().toLowerCase().replace(" ", "-"));
        category.setUpdatedAt(LocalDateTime.now());
        categoryRepository.save(category);
        return category;
    }

    public void deleteCategory(int id) {
        User user = (User) httpSession.getAttribute("CURRENT_USER");
        if (user == null) {
            throw new RuntimeException("User not logged in");
        }

        if (user.getRole() != User_Role.ADMIN) {
            throw new RuntimeException("You do not have permission to perform this action");
        }

        Optional<Category> existingCategory = categoryRepository.findById(id);
        if (existingCategory.isEmpty()) {
            throw new RuntimeException("Category not found");
        }
        categoryRepository.deleteById(id);
    }
}
