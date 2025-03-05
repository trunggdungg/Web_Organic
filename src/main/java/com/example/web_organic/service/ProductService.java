package com.example.web_organic.service;

import com.example.web_organic.entity.Product;
import com.example.web_organic.entity.ProductVariants;
import com.example.web_organic.repository.ProductRepository;
import com.example.web_organic.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantService productVariantService;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    //    lấy sản phẩm phổ biến theo rating + số lượng đánh giá>=3
    public Page<Product> getProductPopular(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("createdAt").descending());
        return productRepository.findFeaturedProductsWithRatingAboveThree(pageable);
    }

    //    lấy số lượng sản phẩm theo category ,
    public Page<Product> getProductByCategory(int page, int pageSize, String slug) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("createdAt").descending());
        return productRepository.findByCategorySlug(slug, pageable);
    }

    // lấy giá sản phẩm
    public BigDecimal getProductVariantPrice(Integer productId) {
        ProductVariants variant = productVariantRepository.findByProductIdAndIsDefaultTrue(productId);
        if (variant == null) {
            throw new RuntimeException("Không tìm thấy biến thể mặc định cho sản phẩm có ID: " + productId);
        }
        return variant.getPrice();
    }

    // Lấy tổng stock của một sản phẩm
    public Integer getProductStock(Integer productId) {
        return productVariantService.getTotalStockByProductId(productId);
    }

    // Lấy danh sách variants của một sản phẩm
    public List<ProductVariants> getProductVariants(Integer productId) {
        return productVariantService.getVariantsByProductId(productId);
    }
}
