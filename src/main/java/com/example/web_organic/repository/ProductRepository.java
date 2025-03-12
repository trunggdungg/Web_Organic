package com.example.web_organic.repository;

import com.example.web_organic.entity.Category;
import com.example.web_organic.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {
    @Query("""
    SELECT p FROM Product p 
    WHERE (p.isFeatured = true 
    OR (SELECT AVG(r.rating) FROM Review r WHERE r.product.id = p.id) >= 3)
      AND p.status = true
""")
    //lấy ra các sản phẩm có isfeatures = true hoặc có rating trung bình từ các review >= 3
    Page<Product> findFeaturedProductsWithRatingAboveThree(Pageable pageable);
//    lấy ra các sản phẩm theo category
    Page<Product> findByCategoryId(Integer categoryId, Pageable pageable);
//    tim theo slug
    Page<Product> findByCategorySlug(String slug, Pageable pageable);

    List<Product> findTop15ByOrderByDiscountDesc();

    Page<Product> findByBrandId(Integer brandId, Pageable pageable);

    //    lấy ra sản phẩm theo id và slug
    Optional<Product> findByIdAndSlug(Integer id, String slug);

    List<Product> findByCategoryIdAndIdNotAndStatusTrue(Integer categoryId, Integer excludeProductId);

    Page<Product> findByCategoryAndStatusTrue(Category selectedCategory, Pageable pageable);
}
