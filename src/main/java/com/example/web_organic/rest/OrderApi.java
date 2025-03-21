package com.example.web_organic.rest;

import com.example.web_organic.entity.Order;
import com.example.web_organic.modal.request.OrderRequest;
import com.example.web_organic.repository.OrderRepository;
import com.example.web_organic.service.OrderService;
import com.example.web_organic.service.SePayService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderApi {
    @Autowired
    private OrderService orderService;
    @Autowired
    private SePayService sePayService;
    @Autowired
    private OrderRepository orderRepository;

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest) {
        Order order = orderService.createOrder(orderRequest);

        if ("Bank".equalsIgnoreCase(orderRequest.getPaymentMethod())) {
            String qrCode = sePayService.generateQrCode(order.getId(), order.getTotal());
            return ResponseEntity.ok(Map.of("orderId", order.getId(), "qrCode", qrCode, "checkStatusUrl", "/check-status/" + order.getId()));
        }

        return ResponseEntity.ok(Map.of("orderId", order.getId()));
    }


    // Cập nhật trạng thái đơn hàng COD
    @PutMapping("/confirm/order/{orderId}")
    public ResponseEntity<?> confirmOrder(@PathVariable String orderId,  @RequestParam boolean customerReceived) {
        orderService.confirmOrder(orderId, customerReceived);
        return ResponseEntity.ok("Cập nhật trạng thái đơn hàng thành công!");
    }

    // Lấy QR code cho đơn hàng (bỏ)?
    @GetMapping("/order/{orderId}/generate-qr")
    public ResponseEntity<?> generateQrCode(@PathVariable String orderId) {
        Order order = orderRepository.findOrderById(orderId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        String qrCodeUrl = sePayService.generateQrCode(order.getId(), order.getTotal());
        return ResponseEntity.ok(Map.of("qrCode", qrCodeUrl));
    }

    // Tạo QR code thanh toán  (bỏ)?
    @PostMapping("/orders/generate-qr")
    public ResponseEntity<Map<String, String>> generateQR(@RequestBody Map<String, Object> request) {
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        String accountNumber = "96247VYDE5"; // Tài khoản SePay
        String bankCode = "BIDV"; // Ngân hàng
        String description = "Thanh toán đơn hàng"; // Nội dung

        String qrUrl = String.format("https://qr.sepay.vn/img?acc=%s&bank=%s&amount=%s&des=%s",
            accountNumber, bankCode, amount, description);

        Map<String, String> response = new HashMap<>();
        response.put("qrUrl", qrUrl);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-status/{orderId}")
    public ResponseEntity<?> checkOrderPaymentStatus(@PathVariable String orderId) {
        Optional<Order> orderOptional = orderRepository.findOrderById(orderId);
        if (orderOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Không tìm thấy đơn hàng", "status", "NOT_FOUND"));
        }

        Order order = orderOptional.get();
        return ResponseEntity.ok(Map.of("orderId", order.getId(), "paymentStatus", order.getPaymentStatus()));
    }



}
