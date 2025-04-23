package com.example.web_organic.service;
import com.example.web_organic.entity.Product;
import com.example.web_organic.entity.ProductVariants;
import com.example.web_organic.entity.User;
import com.example.web_organic.modal.request.UpSertProductVariantRequestAdmin;
import com.example.web_organic.repository.ProductRepository;
import com.example.web_organic.repository.ProductVariantRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductVariantService {
    @Autowired
    private ProductVariantRepository productVariantRepository;
    @Autowired
    private HttpSession httpSession;
    @Autowired
    private ProductRepository productRepository;

    // Lấy tổng stock của một sản phẩm
    public Integer getTotalStockByProductId(Integer productId) {
        List<ProductVariants> variants = productVariantRepository.findByProductId(productId);
        return variants.stream()
                .mapToInt(ProductVariants::getStock)
                .sum();
    }

    //     lay ra stock cua tung variant
    public Map<Integer, Integer> getStockByVariant(Integer productId) {
        List<ProductVariants> variants = productVariantRepository.findByProductId(productId);
        return variants.stream()
            .collect(Collectors.toMap(ProductVariants::getId, ProductVariants::getStock));
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


    public ProductVariants createProductVariant(UpSertProductVariantRequestAdmin upSertProductVariantRequestAdmin) {
        User user = (User) httpSession.getAttribute("CURRENT_USER");
        if (user== null){
            throw new RuntimeException("User not found");
        }
        Product product = productRepository.getProductById(upSertProductVariantRequestAdmin.getProductId());
        if (product == null){
            throw new RuntimeException("Product not found");
        }
        ProductVariants productVariants = ProductVariants.builder()
                .product(product)
                .price(upSertProductVariantRequestAdmin.getPrice())
                .stock(upSertProductVariantRequestAdmin.getStock())
                .weight(upSertProductVariantRequestAdmin.getWeight())
                .isDefault(upSertProductVariantRequestAdmin.getIsDefault())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return productVariantRepository.save(productVariants);

    }

    public ProductVariants updateProductVariant(Integer id, UpSertProductVariantRequestAdmin upSertProductVariantRequestAdmin) {
        User user = (User) httpSession.getAttribute("CURRENT_USER");
        if (user== null){
            throw new RuntimeException("User not found");
        }
        Product product = productRepository.getProductById(upSertProductVariantRequestAdmin.getProductId());
        if (product == null){
            throw new RuntimeException("Product not found");
        }
        ProductVariants productVariants = productVariantRepository.findById(id).orElse(null);
        if (productVariants == null){
            throw new RuntimeException("Product variant not found");
        }
        productVariants.setProduct(product);
        productVariants.setPrice(upSertProductVariantRequestAdmin.getPrice());
        productVariants.setStock(upSertProductVariantRequestAdmin.getStock());
        productVariants.setWeight(upSertProductVariantRequestAdmin.getWeight());
        productVariants.setIsDefault(upSertProductVariantRequestAdmin.getIsDefault());
        productVariants.setUpdatedAt(LocalDateTime.now());
        return productVariantRepository.save(productVariants);

    }

    public void deleteProductVariant(Integer id) {
        User user = (User) httpSession.getAttribute("CURRENT_USER");
        if (user== null){
            throw new RuntimeException("User not found");
        }
        ProductVariants productVariants = productVariantRepository.findById(id).orElse(null);
        if (productVariants == null){
            throw new RuntimeException("Product variant not found");
        }
        productVariantRepository.delete(productVariants);

    }

    public int countTotalVariants() {
        return productVariantRepository.countTotalVariants();
    }

    public int countDefaultVariants() {
        return productVariantRepository.countDefaultVariants();
    }

    public int getTotalStock() {
        Integer totalStock = productVariantRepository.getTotalStock();
        return totalStock != null ? totalStock : 0;
    }

    public int countOutOfStockVariants() {
        return productVariantRepository.countOutOfStockVariants();
    }
}
