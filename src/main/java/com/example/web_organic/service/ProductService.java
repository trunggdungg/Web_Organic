package com.example.web_organic.service;

import com.example.web_organic.Specification.ProductSpecification;
import com.example.web_organic.entity.*;
import com.example.web_organic.repository.ProductImageRepository;
import com.example.web_organic.repository.ProductRepository;
import com.example.web_organic.repository.ProductVariantRepository;
import com.example.web_organic.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantService productVariantService;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private ReviewRepository reviewRepository;

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
//    lấy 15 sản phẩm có discount lớn nhất
    public List<Product> getProductDiscountMax() {
        return productRepository.findTop15ByOrderByDiscountDesc();
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
//    lấy stock của từng variant
    public Map<Integer, Integer> getProductVariantStocks(Integer productId) {
        return productVariantService.getStockByVariant(productId);
    }
//lây san pham lien quan
    public List<Product> getProductsByCategory(Integer categoryId, Integer excludeProductId) {
        return productRepository.findByCategoryIdAndIdNotAndStatusTrue(categoryId, excludeProductId);
    }
    public Page<Product> getProductsByCategory(Category selectedCategory, int page, int pageSize) {
        return productRepository.findByCategoryAndStatusTrue(selectedCategory, PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    public Page<Product> getAllProducts(int i, int pageSize) {
        return productRepository.findAll(PageRequest.of(i, pageSize, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    public List<ProductVariants> getProductVariantsByProductId(Integer productId) {
        return productVariantRepository.findByProductId(productId);
    }


    // Lấy danh sách variants của một sản phẩm
    public List<ProductVariants> getProductVariants(Integer productId) {
        return productVariantService.getVariantsByProductId(productId);
    }

    //Lọc sản phẩm theo brand
    public Page<Product> getProductByBrand(int page, int pageSize, Integer brandId) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("createdAt").descending());
        return productRepository.findByBrandId(brandId, pageable);
    }

    public Product getProductDetail(Integer id, String slug) {
        return productRepository.findByIdAndSlug(id, slug)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm có ID: " + id));
    }

    public List<ProductImage> getProductImages(Integer id) {
        return productImageRepository.findByProductId(id);
    }

//    Lấy cac review cua san phẩm
    public List<Review> getProductReviews(Integer productId) {
        return reviewRepository.findByProductId(productId);
    }


    public List<Product> searchProductsByName(String query) {
        // Tạo specification để tìm kiếm sản phẩm theo tên
        Specification<Product> spec = (root, criteriaQuery, criteriaBuilder) ->
            criteriaBuilder.like(
                criteriaBuilder.lower(root.get("name")),
                "%" + query.toLowerCase() + "%"
            );

        // Chỉ lấy sản phẩm có status = true
        Specification<Product> activeSpec = (root, criteriaQuery, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("status"), true);

        // Kết hợp cả hai specification
        Specification<Product> finalSpec = spec.and(activeSpec);

        // Tạo trang với 10 sản phẩm đầu tiên, sắp xếp theo tên
        Pageable pageable = PageRequest.of(0, 5, Sort.by("name").ascending());

        // Trả về danh sách sản phẩm
        return productRepository.findAll(finalSpec, pageable).getContent();
    }


}
