package com.example.web_organic.config;

import com.example.web_organic.config.interceptor.AuthenticationInterceptor;
import com.example.web_organic.config.interceptor.CategoryInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private CategoryInterceptor categoryInterceptor;

    @Autowired
    private AuthenticationInterceptor authenticationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(categoryInterceptor)
            .addPathPatterns("/**"); // Áp dụng cho tất cả URL


//        đây là interceptor để kiểm tra xem người dùng đã đăng nhập chưa
        registry.addInterceptor(authenticationInterceptor)
            .addPathPatterns(
                "/api/reviews",
                "/admin/**",
                "/api/reviews/**",
                "/api/blogs/**",
                "/thong-tin-ca-nhan",
                "/sanpham-yeu-thich"
                // Không có api là trả về giao diện, có api là trả về JSON
            );

    }
}
