package com.example.web_organic.rest.admin;

import com.example.web_organic.entity.Brand;
import com.example.web_organic.modal.request.InsertBrandRequest;
import com.example.web_organic.service.BrandService;
import com.example.web_organic.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/brand")
public class BrandApiAdmin {
    @Autowired
    private BrandService brandService;
    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping("/create")
    public ResponseEntity<?> createBrand(@ModelAttribute InsertBrandRequest insertBrandRequest) throws IOException {
        MultipartFile file = insertBrandRequest.getLogo();
        Map uploadResult = cloudinaryService.uploadFile(file, "brand-logo");
        System.out.println("Upload result: " + uploadResult);
        String imageUrl = (String) uploadResult.get("url");

        insertBrandRequest.setLogo(null); // Không lưu MultipartFile vào database
        Brand brand = brandService.createBrand(
                insertBrandRequest.getNameBrand(),
                imageUrl,
                insertBrandRequest.getStatus()
        );
        return ResponseEntity.ok(brand);
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<?> updateBrand(@PathVariable Integer id, @ModelAttribute InsertBrandRequest insertBrandRequest) throws IOException {
        MultipartFile file = insertBrandRequest.getLogo();
        String imageUrl = null;
        if (file != null && !file.isEmpty()) {
            Map uploadResult = cloudinaryService.uploadFile(file, "brand-logo");
            System.out.println("Upload result: " + uploadResult);
            imageUrl = (String) uploadResult.get("url");
        }

        Brand brand = brandService.updateBrand(
                id,
                insertBrandRequest.getNameBrand(),
                imageUrl,
                insertBrandRequest.getStatus()
        );
        return ResponseEntity.ok(brand);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> deleteBrand(@PathVariable Integer id) {
        brandService.deleteBrand(id);
        return ResponseEntity.ok("Brand deleted successfully");
    }

}
