package com.example.web_organic.repository;

import com.example.web_organic.entity.Category;
import com.example.web_organic.modal.Enum.Category_Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findByTypeAndStatusTrue(Category_Type type);

    Category findBySlug(String type);

    Category findByName(String name);

    // Đếm số danh mục đang hoạt động
    @Query("SELECT COUNT(c) FROM Category c WHERE c.status = true")
    int countActiveCategories();

    // Đếm số danh mục không hoạt động
    @Query("SELECT COUNT(c) FROM Category c WHERE c.status = false")
    int countInactiveCategories();

    // Đếm số danh mục mới trong tháng hiện tại
    @Query("SELECT COUNT(c) FROM Category c WHERE MONTH(c.createdAt) = MONTH(CURRENT_DATE) AND YEAR(c.createdAt) = YEAR(CURRENT_DATE)")
    int countNewCategoriesCurrentMonth();
}
