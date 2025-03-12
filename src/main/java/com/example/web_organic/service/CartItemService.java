package com.example.web_organic.service;
import com.example.web_organic.entity.CartItem;
import com.example.web_organic.entity.Product;
import com.example.web_organic.entity.User;
import com.example.web_organic.repository.CartItemRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
