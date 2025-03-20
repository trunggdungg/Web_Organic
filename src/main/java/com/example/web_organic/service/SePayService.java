package com.example.web_organic.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class SePayService {
    private static final String ACCOUNT_NUMBER = "96247VYDE5"; // Tài khoản SePay
    private static final String BANK = "BIDV"; // Ngân hàng

    public String generateQrCode(String orderId, BigDecimal amount) {
        String description = "Thanh toán cho đơn hàng " + orderId; // Nội dung thanh toán là mã đơn hàng
        return "https://qr.sepay.vn/img?acc=" + ACCOUNT_NUMBER + "&bank=" + BANK + "&amount=" + amount + "&des=" + description;
    }
}
