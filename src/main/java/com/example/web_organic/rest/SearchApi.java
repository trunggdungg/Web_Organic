package com.example.web_organic.rest;

import com.example.web_organic.entity.Product;
import com.example.web_organic.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SearchApi {
    @Autowired
    private ProductService productService;

    @GetMapping("/search-products")
    public ResponseEntity<List<Map<String, Object>>> searchProducts(@RequestParam String query) {
        // Gọi service để tìm kiếm sản phẩm
        List<Product> products = productService.searchProductsByName(query);

        // Chuyển đổi kết quả sang dạng Map để trả về
        List<Map<String, Object>> result = new ArrayList<>();

        for (Product product : products) {
            Map<String, Object> productMap = new HashMap<>();
            productMap.put("id", product.getId());
            productMap.put("name", product.getName());
            productMap.put("slug", product.getSlug());
            productMap.put("image", product.getImageUrl());

            BigDecimal price = null;
            try {
                // Lấy giá của biến thể mặc định
                price = productService.getProductVariantPrice(product.getId());
            } catch (RuntimeException e) {
                // Nếu không tìm thấy biến thể mặc định, gán giá bằng 0
                price = BigDecimal.ZERO;
            }

            // Nếu có giảm giá, tính giá mới
            if (product.getDiscount() != null && product.getDiscount() > 0) {
                BigDecimal discountAmount = price.multiply(BigDecimal.valueOf(product.getDiscount() / 100.0));
                price = price.subtract(discountAmount);
            }

            productMap.put("price", price.toString());
            result.add(productMap);
        }

        return ResponseEntity.ok(result);
    }
}