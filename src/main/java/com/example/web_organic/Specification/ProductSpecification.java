package com.example.web_organic.Specification;

import com.example.web_organic.entity.Category;
import com.example.web_organic.entity.Product;
import com.example.web_organic.entity.ProductVariants;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {

    // 🔹 Lọc theo danh mục sản phẩm
    public static Specification<Product> filterByCategory(Category category) {
        return (root, query, criteriaBuilder) -> {
            if (category == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("category"), category);
        };
    }

    // 🔹 Lọc theo thương hiệu sản phẩm
    public static Specification<Product> filterByBrand(String brand) {
        return (root, query, criteriaBuilder) -> {
            if (brand == null || brand.equals("all")) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("brand").get("nameBrand"), brand);
        };
    }

    // 🔹 Lọc theo giá từ ProductVariants (dùng MIN() và MAX())
    public static Specification<Product> filterByPrice(String price) {
        return (root, query, criteriaBuilder) -> {
            if (price == null || price.equals("all")) {
                return criteriaBuilder.conjunction();
            }

            // Subquery lấy giá của ProductVariants mặc định
            Subquery<BigDecimal> subquery = query.subquery(BigDecimal.class);
            Root<ProductVariants> variantRoot = subquery.from(ProductVariants.class);
            subquery.select(variantRoot.get("price"));
            subquery.where(
                criteriaBuilder.equal(variantRoot.get("product"), root),
                criteriaBuilder.isTrue(variantRoot.get("isDefault"))
            );

            if (price.equals("less100")) {
                return criteriaBuilder.lessThan(subquery, new BigDecimal("100000"));
            } else if (price.equals("100to500")) {
                return criteriaBuilder.between(subquery, new BigDecimal("100000"), new BigDecimal("500000"));
            } else if (price.equals("than500")) {
                return criteriaBuilder.greaterThan(subquery, new BigDecimal("500000"));
            }

            return criteriaBuilder.conjunction();
        };
    }


    // 🔹 Sắp xếp theo tên hoặc giá từ ProductVariants
    public static Specification<Product> sortBy(String sort) {
        return (root, query, criteriaBuilder) -> {
            if (sort == null || sort.equals("all")) {
                return criteriaBuilder.conjunction();
            }

            // Subquery tính finalPrice = price * (1 - discount/100)
            Subquery<BigDecimal> subquery = query.subquery(BigDecimal.class);
            Root<ProductVariants> variantRoot = subquery.from(ProductVariants.class);

            Path<BigDecimal> price = variantRoot.get("price");
            Path<Integer> discountInt = variantRoot.get("product").get("discount");

            // Chuyển discount (Integer) sang BigDecimal an toàn
            Expression<BigDecimal> discount = discountInt.as(BigDecimal.class);

            // Tạo biểu thức 1 - (discount / 100)
            Expression<BigDecimal> discountDiv100 = criteriaBuilder.prod(
                discount,
                criteriaBuilder.literal(BigDecimal.valueOf(0.01))
            );

            Expression<BigDecimal> discountMultiplier = criteriaBuilder.diff(
                criteriaBuilder.literal(BigDecimal.ONE),
                discountDiv100
            );

            // price * (1 - discount/100)
            Expression<BigDecimal> finalPrice = criteriaBuilder.prod(price, discountMultiplier);

            subquery.select(finalPrice).where(
                criteriaBuilder.equal(variantRoot.get("product"), root),
                criteriaBuilder.isTrue(variantRoot.get("isDefault"))
            );

            // Apply sort
            if (sort.equals("sortAZ")) {
                query.orderBy(criteriaBuilder.asc(root.get("name")));
            } else if (sort.equals("sortZA")) {
                query.orderBy(criteriaBuilder.desc(root.get("name")));
            } else if (sort.equals("priceAsc")) {
                query.orderBy(criteriaBuilder.asc(subquery));
            } else if (sort.equals("priceDesc")) {
                query.orderBy(criteriaBuilder.desc(subquery));
            }

            return criteriaBuilder.conjunction();
        };
    }
}