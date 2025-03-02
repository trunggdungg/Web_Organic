package com.example.web_organic.controller;

import com.example.web_organic.entity.Product;
import com.example.web_organic.entity.User;
import com.example.web_organic.modal.response.TokenConfirmMessageResponse;
import com.example.web_organic.service.ProductService;
import com.example.web_organic.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        model.addAttribute("products", productService.getProductPopular(page, pageSize));
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
