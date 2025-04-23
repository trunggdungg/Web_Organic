package com.example.web_organic.repository;

import com.example.web_organic.entity.Blog;
import com.example.web_organic.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Integer> {
    Page<Blog> findByStatus(boolean b, PageRequest createdAt);


    Page<Blog> findByCategoryAndStatusTrue(Category selectedCategory, Pageable pageable);

    Optional<Blog> findByIdAndSlug(Integer id, String slug);

    // Đếm tổng số bài viết
    @Query("SELECT COUNT(b) FROM Blog b")
    int countTotalBlogs();

    // Đếm số bài viết đã xuất bản
    @Query("SELECT COUNT(b) FROM Blog b WHERE b.status = true")
    int countPublishedBlogs();

    // Đếm số bài viết nháp
    @Query("SELECT COUNT(b) FROM Blog b WHERE b.status = false")
    int countDraftBlogs();

    // Đếm số bài viết mới trong tháng hiện tại
    @Query("SELECT COUNT(b) FROM Blog b WHERE MONTH(b.createdAt) = MONTH(CURRENT_DATE) AND YEAR(b.createdAt) = YEAR(CURRENT_DATE)")
    int countNewBlogsCurrentMonth();
}
