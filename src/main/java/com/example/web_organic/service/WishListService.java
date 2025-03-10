package com.example.web_organic.service;
import com.example.web_organic.entity.Product;
import com.example.web_organic.entity.User;
import com.example.web_organic.entity.WishList;
import com.example.web_organic.repository.ProductRepository;
import com.example.web_organic.repository.WishListRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishListService {
    @Autowired
    private UserService userService;
    @Autowired
    private WishListRepository wishListRepository;
    @Autowired
    private HttpSession httpSession;
    @Autowired
    private ProductRepository productRepository;

    public List<Product> getWishList() {
        User currentUser = (User) httpSession.getAttribute("CURRENT_USER");
        if (currentUser == null) {
            return List.of(); // Trả về danh sách rỗng nếu chưa đăng nhập
        }

        // Lấy danh sách yêu thích của user
        List<WishList> wishLists = wishListRepository.findByUser(currentUser);

        // Chuyển danh sách WishList sang danh sách Product
        return wishLists.stream()
            .map(WishList::getProduct)
            .collect(Collectors.toList());
    }

    public boolean toggleWishListItem(Integer productId) {
        User currentUser = (User) httpSession.getAttribute("CURRENT_USER");
        if (currentUser == null) {
            throw new RuntimeException("User must be logged in");
        }

        // Kiểm tra xem sản phẩm đã có trong wishlist chưa
        WishList existingItem = wishListRepository.findByUserAndProductId(currentUser, productId);

        if (existingItem != null) {
            // Xoá khỏi wishlist
            wishListRepository.delete(existingItem);
            return false; // Indicates item was removed
        } else {
            // Thêm vào wishlist
            // Kiểm tra xem sản phẩm có tồn tại không
            Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

            WishList wishList = new WishList();
            wishList.setUser(currentUser);
            wishList.setProduct(product);
            wishList.setCreatedAt(LocalDateTime.now());
            wishList.setUpdatedAt(LocalDateTime.now());
            wishListRepository.save(wishList);
            return true; // Indicates item was added
        }
    }
}
