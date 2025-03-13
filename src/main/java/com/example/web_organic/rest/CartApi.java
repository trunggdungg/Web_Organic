package com.example.web_organic.rest;

import com.example.web_organic.entity.CartItem;
import com.example.web_organic.entity.User;
import com.example.web_organic.modal.request.AddToCartRequest;
import com.example.web_organic.modal.request.UpdateCartRequest;
import com.example.web_organic.modal.response.ErrorResponse;
import com.example.web_organic.service.CartItemService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CartApi {
    @Autowired
    private CartItemService cartItemService;
    @GetMapping("/cart-header")
    public ResponseEntity<?> getCartHeader(HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("CURRENT_USER");
        List<CartItem> cartItems = cartItemService.getCartItems(currentUser);

        // Nhóm các sản phẩm cùng loại (giả sử productId xác định sản phẩm)
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


}
