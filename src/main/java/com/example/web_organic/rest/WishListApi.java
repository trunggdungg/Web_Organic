package com.example.web_organic.rest;

import com.example.web_organic.entity.Product;
import com.example.web_organic.repository.WishListRepository;
import com.example.web_organic.service.UserService;
import com.example.web_organic.service.WishListService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class WishListApi {
    @Autowired
    private WishListService wishListService;

    @GetMapping("/wishlist")
    public ResponseEntity<?> getWishList() {
        List<Product> products = wishListService.getWishList();

        return ResponseEntity.ok(products);
    }


    @PostMapping("/wishlist/toggle/{productId}")
    public ResponseEntity<?> toggleWishList(@PathVariable Integer productId) {
        boolean isAdded = wishListService.toggleWishListItem(productId);
        return ResponseEntity.ok(Map.of("added", isAdded));
    }
}
