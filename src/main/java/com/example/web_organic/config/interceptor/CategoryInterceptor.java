package com.example.web_organic.config.interceptor;

import com.example.web_organic.entity.Category;
import com.example.web_organic.modal.Enum.Category_Type;
import com.example.web_organic.service.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Component
public class CategoryInterceptor implements HandlerInterceptor {

    @Autowired
    private CategoryService categoryService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            List<Category> categoryProduct = categoryService.getCategoryByTypeAndStatus(Category_Type.PRODUCT);
            List<Category> categoryBlog = categoryService.getCategoryByTypeAndStatus(Category_Type.BLOG);

            modelAndView.addObject("categoryProduct", categoryProduct);
            modelAndView.addObject("categoryBlog", categoryBlog);

            System.out.println("🔹 Interceptor: Thêm danh mục vào Model.");
        }
    }
}