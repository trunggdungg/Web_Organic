package com.example.web_organic.service;
import com.example.web_organic.entity.CartItem;
import com.example.web_organic.entity.Product;
import com.example.web_organic.entity.ProductVariants;
import com.example.web_organic.entity.User;
import com.example.web_organic.modal.request.AddToCartRequest;
import com.example.web_organic.modal.request.UpdateCartRequest;
import com.example.web_organic.repository.CartItemRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartItemService {
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private HttpSession httpSession;

    public List<CartItem> getCartItems(User user) {
        return cartItemRepository.findByUser(user);
    }

    public void deleteCartItem(Integer id) {
        cartItemRepository.deleteById(id);
    }

    public void updateCartItem(Integer id, Integer quantity) {
        cartItemRepository.findById(id).ifPresent(cartItem -> {
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        });
    }

    public void addToCart(User currentUser, AddToCartRequest addToCartRequest) {
        ProductVariants productVariant = new ProductVariants();
        productVariant.setId(addToCartRequest.getProductVariantId());
// lấy quantity từ request

        CartItem cartItem = cartItemRepository.findByUserAndProductVariant(currentUser, productVariant)
            .orElse(CartItem.builder()
                .user(currentUser)
                .productVariant(productVariant)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .quantity(0)
                .build());

        cartItem.setQuantity(cartItem.getQuantity() + addToCartRequest.getQuantity());
        cartItemRepository.save(cartItem);

    }

//  lấy danh sách sản phẩm trong giỏ hàng
//    public List<Product> getCartItems() {
//        User currentUser = (User) httpSession.getAttribute("CURRENT_USER");
//        if (currentUser == null) {
//            return List.of(); // Trả về danh sách rỗng nếu chưa đăng nhập
//        }
//
//        // Lấy danh sách sản phẩm trong giỏ hàng của user
//        List<CartItem> cartItems = cartItemRepository.findByUser(currentUser);
//
//        // Chuyển danh sách CartItem sang danh sách Product
//        return cartItems.stream()
//            .map(CartItem::getProduct)
//            .collect(Collectors.toList());
//    }
}
