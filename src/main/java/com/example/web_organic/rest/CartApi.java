package com.example.web_organic.rest;

import com.example.web_organic.entity.CartItem;
import com.example.web_organic.entity.ProductVariants;
import com.example.web_organic.entity.User;
import com.example.web_organic.modal.request.AddToCartRequest;
import com.example.web_organic.modal.request.CartStockCheckRequest;
import com.example.web_organic.modal.request.UpdateCartRequest;
import com.example.web_organic.modal.response.CartStockResponse;
import com.example.web_organic.modal.response.ErrorResponse;
import com.example.web_organic.repository.ProductVariantRepository;
import com.example.web_organic.service.CartItemService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CartApi {
    @Autowired
    private CartItemService cartItemService;
    @Autowired
    private ProductVariantRepository productVariantRepository;
    @GetMapping("/cart-header")
    public ResponseEntity<?> getCartHeader(HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("CURRENT_USER");
        List<CartItem> cartItems = cartItemService.getCartItems(currentUser);

        // Nhóm các sản phẩm cùng loại 
        Map<Integer, CartItem> groupedItems = new HashMap<>();
        for (CartItem item : cartItems) {
            groupedItems.merge(item.getProductVariant().getId(), item, (existing, newItem) -> {
                existing.setQuantity(existing.getQuantity() + newItem.getQuantity());
                return existing;
            });
        }

        return ResponseEntity.ok(groupedItems.values());
    }


    @DeleteMapping("/delete-cart-item/{id}")
    public ResponseEntity<?> deleteCartItem(@PathVariable Integer id) {
        try {
            cartItemService.deleteCartItem(id);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(e.getMessage())
                .build();
            return ResponseEntity.badRequest().body(errorResponse);
        }

    }

    @PatchMapping("/update-cart-item/{id}")
    public ResponseEntity<?> updateCartItem(@PathVariable Integer id, @RequestBody UpdateCartRequest cartRequest) {
        try {
            cartItemService.updateCartItem(id, cartRequest.getQuantity());
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(e.getMessage())
                .build();
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }


    @PostMapping("/add-to-cart")
    public ResponseEntity<?> addToCart(@RequestBody AddToCartRequest addToCartRequest, HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("CURRENT_USER");
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        cartItemService.addToCart(currentUser, addToCartRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cart/check-stock")
    public ResponseEntity<?> checkCartStock(@RequestBody List<CartStockCheckRequest> items) {
        List<CartStockResponse> outOfStockItems = new ArrayList<>();

        for (CartStockCheckRequest item : items) {
            ProductVariants variant = productVariantRepository.findById(item.getVariantId())
                .orElse(null);

            if (variant == null || variant.getStock() < item.getQuantity()) {
                CartStockResponse response = new CartStockResponse();
                response.setVariantId(item.getVariantId());
                response.setProductName(variant != null ? variant.getProduct().getName() : "Sản phẩm không tồn tại");
                response.setVariantName(variant != null ? variant.getWeight() : "");
                response.setRequestedQuantity(item.getQuantity());
                response.setAvailableStock(variant != null ? variant.getStock() : 0);
                outOfStockItems.add(response);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", outOfStockItems.isEmpty());
        response.put("outOfStockItems", outOfStockItems);

        return ResponseEntity.ok(response);
    }


}
