package com.example.web_organic.rest.admin;

import com.example.web_organic.entity.Blog;
import com.example.web_organic.modal.request.UpsertBlogRequest;
import com.example.web_organic.service.BlogService;
import com.example.web_organic.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/blog")
public class BlogApiAdmin {
    @Autowired
    private BlogService blogService;
    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping("/create")
    public ResponseEntity<?> createBlog(@ModelAttribute UpsertBlogRequest upsertBlogRequest) throws IOException {
        MultipartFile file = upsertBlogRequest.getThumbnail();
        Map uploadResult = cloudinaryService.uploadFile(file, "blog-image");
        System.out.println("Upload result: " + uploadResult);
        String imageUrl = (String) uploadResult.get("url");

        upsertBlogRequest.setThumbnail(null); // Không lưu MultipartFile vào database
        Blog blog = blogService.createBlog(
            upsertBlogRequest.getTitle(),
            upsertBlogRequest.getContent(),
            upsertBlogRequest.getCategoryId(),
            imageUrl,
            upsertBlogRequest.getStatus(),
            upsertBlogRequest.getDescription()
        );
        return ResponseEntity.ok(blog);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateBlog(@PathVariable Integer id, @ModelAttribute UpsertBlogRequest upsertBlogRequest) throws IOException {
        MultipartFile file = upsertBlogRequest.getThumbnail();
        String imageUrl = null;
        if (file != null && !file.isEmpty()) {
            Map uploadResult = cloudinaryService.uploadFile(file, "blog-image");
            System.out.println("Upload result: " + uploadResult);
            imageUrl = (String) uploadResult.get("url");
        }

        upsertBlogRequest.setThumbnail(null); // Không lưu MultipartFile vào database
        Blog blog = blogService.updateBlog(
            id,
            upsertBlogRequest.getTitle(),
            upsertBlogRequest.getContent(),
            upsertBlogRequest.getCategoryId(),
            imageUrl,
            upsertBlogRequest.getStatus(),
            upsertBlogRequest.getDescription()
        );
        return ResponseEntity.ok(blog);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteBlog(@PathVariable Integer id) {
        blogService.deleteBlog(id);
        return ResponseEntity.ok("Blog deleted successfully");
    }
}
