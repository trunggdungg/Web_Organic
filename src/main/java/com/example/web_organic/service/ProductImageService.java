package com.example.web_organic.service;
import com.example.web_organic.entity.Product;
import com.example.web_organic.entity.ProductImage;
import com.example.web_organic.modal.request.InsertImageProductRequest;
import com.example.web_organic.modal.response.FileResponse;
import com.example.web_organic.repository.ProductImageRepository;
import com.example.web_organic.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductImageService {
    @Autowired private ProductImageRepository productImageRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired
    private CloudinaryService cloudinaryService;
    public Page<ProductImage> getAllProductImages(int page, int pageSize) {
        return productImageRepository.findAll(PageRequest.of(page - 1, pageSize));
    }

    public List<ProductImage> findByProductIds(List<Integer> productIds) {
        return productImageRepository.findByProductIdIn(productIds);
    }



    public ProductImage createProductImage(Integer productId, String imageUrl, String altText) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));

        ProductImage productImage = ProductImage.builder()
            .product(product)
            .imageProduct(imageUrl)
            .altText(altText)
            .build();

        return productImageRepository.save(productImage);
    }

    public void deleteProductImage(Integer id) {
        ProductImage productImage = productImageRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product image not found"));
        productImageRepository.delete(productImage);
    }
}
