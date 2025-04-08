package com.example.web_organic.rest.admin;

import com.example.web_organic.entity.Product;
import com.example.web_organic.modal.request.UpSertProductRequestAdmin;
import com.example.web_organic.modal.response.FileResponse;
import com.example.web_organic.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/product")
public class ProductApiAdmin {
@Autowired
private ProductService productService;
    @PostMapping("/{id}/upload-poster")
    public ResponseEntity<?> uploadPoster(@PathVariable Integer id, @RequestParam MultipartFile file) {//MultipartFile file để upload file,la dai dien cac file dc tu client gui len
        String path = productService.uploadPoster(id, file);
        FileResponse fileResponse = FileResponse.builder()
            .url(path)
            .build();
        return ResponseEntity.ok(fileResponse);
    }

    @GetMapping("/uploaded-images")
    public ResponseEntity<List<FileResponse>> getUploadedImages() {
        List<FileResponse> uploadedImages = productService.getUploadedImages();
        return ResponseEntity.ok(uploadedImages);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createProduct(@RequestBody UpSertProductRequestAdmin upSertProductRequestAdmin) {
        Product product = productService.createProduct(upSertProductRequestAdmin);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Integer id, @RequestBody UpSertProductRequestAdmin upSertProductRequestAdmin) {
        Product product = productService.updateProduct(id, upSertProductRequestAdmin);
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }

    // check product variant của sản pham
    @GetMapping("/variants/{id}")
    public ResponseEntity<?> getProductVariants(@PathVariable Integer id) {
        return ResponseEntity.ok(productService.getProductVariants(id));
    }

}
