package com.example.web_organic.rest.admin;

import com.example.web_organic.entity.ProductImage;
import com.example.web_organic.modal.request.InsertImageProductRequest;
import com.example.web_organic.modal.response.FileResponse;
import com.example.web_organic.service.CloudinaryService;
import com.example.web_organic.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/productImage")
public class ProductImageApiAdmin {
    @Autowired
    private ProductImageService productImageService;
    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadProductImage(@ModelAttribute InsertImageProductRequest insertImageProductRequest) throws IOException {
        // Upload ảnh lên Cloudinary
        MultipartFile file = insertImageProductRequest.getImageUrl();
        Map uploadResult = cloudinaryService.uploadFile(file, "organic-product");
        System.out.println("Upload result: " + uploadResult);
        // Lấy URL ảnh từ kết quả upload
        String imageUrl = (String) uploadResult.get("url");

        // Tạo ProductImage
        insertImageProductRequest.setImageUrl(null); // Không lưu MultipartFile vào database
        ProductImage productImage = productImageService.createProductImage(
            insertImageProductRequest.getProductId(),
            imageUrl,
            insertImageProductRequest.getAltText()
        );

        return ResponseEntity.ok(productImage);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProductImage(@PathVariable Integer id) {
        productImageService.deleteProductImage(id);
        return ResponseEntity.ok().build();
    }

}
