package com.example.web_organic.service;

import com.example.web_organic.entity.Blog;
import com.example.web_organic.entity.Category;
import com.example.web_organic.entity.User;
import com.example.web_organic.repository.BlogRepository;
import com.example.web_organic.repository.CategoryRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogService {
    @Autowired
    private BlogRepository blogRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private HttpSession httpSession;
    public Page<Blog> getBlogPopular(int page, int pageSize) {
        return blogRepository.findByStatus(true, PageRequest.of(page - 1, pageSize, Sort.by("createdAt").descending()));
    }


    public Page<Blog> getBlogsByCategory(Category selectedCategory, int page, int pageSize) {
        return blogRepository.findByCategoryAndStatusTrue(selectedCategory, PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    public Page<Blog> getAllBlogs(int page, int pageSize) {
        return blogRepository.findAll(PageRequest.of(page - 1 , pageSize, Sort.by(Sort.Direction.DESC,"createdAt")));
    }

    public Page<Blog> getAllBlogsAndStatus(int i, int pageSize) {
        return blogRepository.findByStatus(true, PageRequest.of(i, pageSize, Sort.by(Sort.Direction.DESC,"createdAt")));
    }

    public Blog getBlogById(Integer id) {
        return blogRepository.findById(id).orElse(null);
    }

    public Blog getBlogDetail(Integer id, String slug) {
       return blogRepository.findByIdAndSlug(id, slug).orElse(null);
    }

    public List<Blog> getAllBlog() {
        return blogRepository.findAll();
    }

    public Blog createBlog(String title, String content, Integer categoryId, String imageUrl, Boolean status, String description) {
        User user = (User) httpSession.getAttribute("CURRENT_USER");
        if (user == null) {
            throw new RuntimeException("User not logged in");
        }
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new RuntimeException("Category not found"));


        Blog blog = Blog.builder()
            .title(title)
            .content(content)
            .category(category)
            .thumbnail(imageUrl)
            .status(status)
            .user(user)
            .description(description)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        blog.setSlug(title.toLowerCase().replaceAll(" ", "-"));
        return blogRepository.save(blog);
    }


    public Blog updateBlog(Integer id, String title, String content, Integer categoryId, String imageUrl, Boolean status, String description) {
        Blog blog = blogRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Blog not found"));

        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new RuntimeException("Category not found"));

        blog.setTitle(title);
        blog.setContent(content);
        blog.setCategory(category);
        blog.setThumbnail(imageUrl);
        blog.setStatus(status);
        blog.setDescription(description);
        blog.setUpdatedAt(LocalDateTime.now());
        blog.setSlug(title.toLowerCase().replaceAll(" ", "-"));
        return blogRepository.save(blog);
    }

    public void deleteBlog(Integer id) {
        Blog blog = blogRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Blog not found"));
        blogRepository.delete(blog);
    }

    public int countTotalBlogs() {
        return blogRepository.countTotalBlogs();
    }

    public int countPublishedBlogs() {
        return blogRepository.countPublishedBlogs();
    }

    public int countDraftBlogs() {
        return blogRepository.countDraftBlogs();
    }

    public int countNewBlogsCurrentMonth() {
        return blogRepository.countNewBlogsCurrentMonth();
    }
}
