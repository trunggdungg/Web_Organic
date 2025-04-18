package com.example.web_organic.repository;

import com.example.web_organic.entity.ProductVariants;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariants, Integer> {

    List<ProductVariants> findByProductId(Integer productId);

//    lấy bien tể tru của san pham
    ProductVariants findByProductIdAndIsDefaultTrue(Integer productId);

    List<ProductVariants> findByProduct_IdIn(List<Integer> productIds);

    @Query("""
    SELECT pv.id, p.name, pv.weight, pv.stock,
           CASE 
               WHEN pv.stock = 0 THEN 'Hết hàng'
               WHEN pv.stock <= 10 THEN 'Sắp hết'
               WHEN pv.stock <= 20 THEN 'Thấp'
               ELSE 'Đủ'
           END
    FROM ProductVariants pv
    JOIN pv.product p
    WHERE pv.stock <= 20
    ORDER BY pv.stock ASC
    """)
    List<Object[]> getStockAlerts(Pageable pageable);

}
