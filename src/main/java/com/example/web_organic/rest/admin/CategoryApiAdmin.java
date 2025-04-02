package com.example.web_organic.rest.admin;

import com.example.web_organic.entity.Category;
import com.example.web_organic.modal.request.UpSertCategoryRequestAdmin;
import com.example.web_organic.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/category")
public class CategoryApiAdmin {
    @Autowired
    private CategoryService categoryService;
    @PostMapping("/create")
    public ResponseEntity<Category> createCategory(@RequestBody UpSertCategoryRequestAdmin upSertCategoryRequestAdmin) {
        Category category = categoryService.createCategory(upSertCategoryRequestAdmin);
        return ResponseEntity.ok(category); // Trả về object category mới tạo
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable int id,@RequestBody UpSertCategoryRequestAdmin upSertCategoryRequestAdmin) {
        Category category = categoryService.updateCategory(id,upSertCategoryRequestAdmin);
        return ResponseEntity.ok(category); // Trả về object category mới tạo
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable int id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }
}
