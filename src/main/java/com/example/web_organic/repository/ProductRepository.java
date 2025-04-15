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
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
    Page<Product> findByCategorySlugAndStatusTrue(String slug, Pageable pageable);

    List<Product> findTop15ByStatusTrueOrderByDiscountDesc();

    Page<Product> findByBrandId(Integer brandId, Pageable pageable);

    //    lấy ra sản phẩm theo id và slug
    Optional<Product> findByIdAndSlug(Integer id, String slug);

    List<Product> findByCategoryIdAndIdNotAndStatusTrue(Integer categoryId, Integer excludeProductId);

    Page<Product> findByCategoryAndStatusTrue(Category selectedCategory, Pageable pageable);

    Product getProductById(Integer productId);


    @Query("SELECT SUM(od.quantity) FROM OrderDetail od JOIN od.order o " +
        "WHERE o.status = 'COMPLETED' AND o.orderDate >= :startDate AND o.orderDate < :endDate")
    Integer countSoldProductsBetween(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate); //  Tổng hợp doanh thu theo sản phẩm

    // Dữ liệu cho biểu đồ phân loại sản phẩm
    @Query("""
    SELECT c.name AS categoryName,
           COUNT(DISTINCT pv.product.id) AS productCount,
           SUM(od.quantity) AS totalQuantitySold
    FROM OrderDetail od
    JOIN od.productVariant pv
    JOIN pv.product p
    JOIN p.category c
    JOIN od.order o
    WHERE o.status = 'COMPLETED'
    GROUP BY c.id, c.name
    ORDER BY totalQuantitySold DESC
    """)
    List<Map<String, Object>> getProductCategoryDistribution();


    @Query("SELECT p.name AS productName, SUM(od.quantity) AS totalQuantitySold " +
        "FROM OrderDetail od " +
        "JOIN od.productVariant pv " +
        "JOIN pv.product p " +
        "JOIN od.order o " +
        "WHERE o.status = 'COMPLETED' AND MONTH(o.orderDate) = MONTH(CURRENT_DATE) AND YEAR(o.orderDate) = YEAR(CURRENT_DATE) " +
        "GROUP BY p.id, p.name " +
        "ORDER BY totalQuantitySold DESC")
    List<Map<String, Object>> getHotProducts(Pageable pageable);    // Dữ liệu cho biểu đồ sản phẩm bán chạy




}
