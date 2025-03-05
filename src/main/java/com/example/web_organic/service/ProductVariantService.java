package com.example.web_organic.service;
import com.example.web_organic.entity.ProductVariants;
import com.example.web_organic.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductVariantService {
    @Autowired
    private ProductVariantRepository productVariantRepository;

    // Lấy tổng stock của một sản phẩm
    public Integer getTotalStockByProductId(Integer productId) {
        List<ProductVariants> variants = productVariantRepository.findByProductId(productId);
        return variants.stream()
                .mapToInt(ProductVariants::getStock)
                .sum();
    }

    // Lấy giá của biến thể mặc định
    public BigDecimal getDefaultVariantPrice(Integer productId) {
        ProductVariants defaultVariant = productVariantRepository.findByProductIdAndIsDefaultTrue(productId);
        if (defaultVariant == null) {
            // Nếu không có biến thể mặc định, lấy giá của biến thể đầu tiên
            List<ProductVariants> variants = productVariantRepository.findByProductId(productId);
            if (!variants.isEmpty()) {
                return variants.get(0).getPrice();
            }
            return BigDecimal.ZERO; // hoặc có thể throw exception tùy vào yêu cầu
        }
        return defaultVariant.getPrice();
    }

    // Lấy danh sách variants của một sản phẩm
    public List<ProductVariants> getVariantsByProductId(Integer productId) {
        return productVariantRepository.findByProductId(productId);
    }
}
