package com.example.web_organic.rest.admin;

import com.example.web_organic.entity.ProductVariants;
import com.example.web_organic.modal.request.UpSertProductVariantRequestAdmin;
import com.example.web_organic.service.ProductService;
import com.example.web_organic.service.ProductVariantService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/product-variant")
public class ProductVariantApiAdmin {

    @Autowired
    private ProductVariantService productVariantService;

    @PostMapping("/create")
    public ResponseEntity<?> createProductVariant(@RequestBody UpSertProductVariantRequestAdmin upSertProductVariantRequestAdmin) {
        ProductVariants productVariants = productVariantService.createProductVariant(upSertProductVariantRequestAdmin);
        return ResponseEntity.ok(productVariants);
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<?> updateProductVariant(@PathVariable Integer id,@RequestBody UpSertProductVariantRequestAdmin upSertProductVariantRequestAdmin) {
      ProductVariants productVariants = productVariantService.updateProductVariant(id,upSertProductVariantRequestAdmin);
        return ResponseEntity.ok(productVariants);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> deleteProductVariant(@PathVariable Integer id) {
        productVariantService.deleteProductVariant(id);
        return ResponseEntity.ok().build();
    }
}
