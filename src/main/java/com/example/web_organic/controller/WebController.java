package com.example.web_organic.controller;

import com.example.web_organic.entity.Product;
import com.example.web_organic.entity.User;
import com.example.web_organic.modal.response.TokenConfirmMessageResponse;
import com.example.web_organic.service.ProductService;
import com.example.web_organic.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Controller
@RequiredArgsConstructor
public class WebController {
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @GetMapping("/")
    public String getHomePage(@RequestParam(required = false,defaultValue = "1") int page,
        @RequestParam(required = false,defaultValue = "10") int pageSize, Model model) {

        Page<Product> products = productService.getProductPopular(page, pageSize);
        Page<Product> productCategory = productService.getProductByCategory(1, 10,6);

        // Thêm thông tin stock và giá cho mỗi sản phẩm
        Map<Integer, Integer> productStocks = new HashMap<>();
        Map<Integer, BigDecimal> productVariantPrices = new HashMap<>();

        // Hàm dùng chung để thêm dữ liệu vào productStocks & productVariantPrices
        Consumer<Product> addProductInfo = product -> {
            int productId = product.getId();
            productStocks.putIfAbsent(productId, productService.getProductStock(productId)); // Tránh ghi đè nếu đã có
            productVariantPrices.putIfAbsent(productId, productService.getProductVariantPrice(productId)); // Tránh ghi đè nếu đã có
        };

        // Lấy stock và giá cho sản phẩm phổ biến
        products.getContent().forEach(addProductInfo);
        // Lấy stock và giá cho sản phẩm theo danh mục
        productCategory.getContent().forEach(addProductInfo);

        // Lấy stock và giá cho sản phẩm ăn ngay
//      productReadyToEat.getContent().forEach(addProductInfo);
        model.addAttribute("products", products); //sản phẩm phổ biến
        model.addAttribute("productStocks", productStocks); //số lượng sản phẩm
        model.addAttribute("productVariantPrices", productVariantPrices); //giá sản phẩm
        model.addAttribute("productCategory", productCategory); //sản phẩm theo category


        return "/web/index";
    }

    @GetMapping("/login")
    public String getLoginPage(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("CURRENT_USER");
        if (user != null) {
            return "redirect:/";
        }
        return "/web/login";
    }

    @GetMapping("/register")
    public String getRegisterPage(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("CURRENT_USER");
        if (user != null) {
            return "redirect:/";
        }
        return "/web/signup";
    }

    @GetMapping("/product")
    public String getProductPage() {
        return "/web/product";
    }

    @GetMapping("/cart")
    public String getCartPage() {
        return "/web/cart";
    }

    @GetMapping("/blog")
    public String getBlogPage() {
        return "/web/blog";
    }

    @GetMapping("/contact")
    public String getContactPage() {
        return "/web/contact";
    }

    @GetMapping("/profile")
    public String getProfilePage() {
        return "/web/info-user";
    }

    @GetMapping("/promotion")
    public String getPromotionPage() {
        return "/web/promotion";
    }

    @GetMapping("/payment")
    public String getPaymentPage() {
        return "/web/payment";
    }

    @GetMapping("/checkout")
    public String getCheckoutPage() {
        return "/web/checkout";
    }

    @GetMapping("/xac-thuc-tai-khoan")
    public String confirmAccount(@RequestParam String token, Model model){
        TokenConfirmMessageResponse response = userService.verifyAccount(token);
        model.addAttribute("response",response);
        return "web/xac-thuc-tai-khoan";
    }

}
