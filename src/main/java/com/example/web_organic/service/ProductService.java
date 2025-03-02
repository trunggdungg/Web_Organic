package com.example.web_organic.service;
import com.example.web_organic.entity.Product;
import com.example.web_organic.repository.ProductRepository;
import com.example.web_organic.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository productVariantsRepository;
    public Page<Product> getProductPopular(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("createdAt").descending());
        return productRepository.findFeaturedProductsWithRatingAboveThree(pageable);
    }



}
