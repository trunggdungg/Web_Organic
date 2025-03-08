package com.example.web_organic.repository;

import com.example.web_organic.entity.Category;
import com.example.web_organic.modal.Enum.Category_Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findByTypeAndStatusTrue(Category_Type type);

    Category findBySlug(String type);
}
