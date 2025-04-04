package com.example.web_organic.repository;

import com.example.web_organic.entity.Blog;
import com.example.web_organic.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Integer> {
    Page<Blog> findByStatus(boolean b, PageRequest createdAt);


    Page<Blog> findByCategoryAndStatusTrue(Category selectedCategory, Pageable pageable);
}
