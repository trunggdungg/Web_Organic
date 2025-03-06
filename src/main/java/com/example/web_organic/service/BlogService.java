package com.example.web_organic.service;

import com.example.web_organic.entity.Blog;
import com.example.web_organic.repository.BlogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlogService {
    @Autowired
    private BlogRepository blogRepository;

    public Page<Blog> getBlogPopular(int page, int pageSize) {
        return blogRepository.findByStatus(true, PageRequest.of(page - 1, pageSize, Sort.by("createdAt").descending()));
    }
}
