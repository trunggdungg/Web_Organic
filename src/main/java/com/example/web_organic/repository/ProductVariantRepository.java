package com.example.web_organic.repository;

import com.example.web_organic.entity.ProductVariants;
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
}
