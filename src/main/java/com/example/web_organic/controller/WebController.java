package com.example.web_organic.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class WebController {
    @GetMapping("/")
    public String getHomePage() {
        return "/web/index";
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "/web/login";
    }

    @GetMapping("/register")
    public String getRegisterPage() {
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

}
