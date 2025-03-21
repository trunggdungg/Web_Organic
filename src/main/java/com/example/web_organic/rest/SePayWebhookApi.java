package com.example.web_organic.rest;

import com.example.web_organic.entity.Order;
import com.example.web_organic.entity.Transaction;
import com.example.web_organic.modal.Enum.Payment_Status_Enum;
import com.example.web_organic.repository.CartItemRepository;
import com.example.web_organic.repository.OrderRepository;
import com.example.web_organic.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
public class SePayWebhookApi {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Transactional
    @PostMapping("/sepay")
    public ResponseEntity<?> handleSePayWebhook(@RequestBody Map<String, String> payload) {
        System.out.println("🔹 Nhận Webhook từ SePay: " + payload);

        // Kiểm tra nếu payload rỗng
        if (payload == null || payload.isEmpty()) {
            System.out.println("⚠ Webhook nhận được nhưng payload rỗng!");
            return ResponseEntity.badRequest().body("⚠ Webhook nhận được nhưng payload rỗng!");
        }

        try {
            String referenceNumber = payload.get("referenceCode");
            String transactionContent = payload.get("content");
            String transferType = payload.get("transferType");
            String amountString = payload.get("transferAmount");
            String transactionDateString = payload.get("transactionDate");

            // Kiểm tra nếu thiếu dữ liệu quan trọng
            if (transactionContent == null || amountString == null || transactionDateString == null) {
                System.out.println("⚠ Webhook thiếu thông tin quan trọng!");
                return ResponseEntity.badRequest().body("⚠ Webhook thiếu thông tin quan trọng!");
            }

            // Chuyển đổi giá trị amount
            BigDecimal amount;
            try {
                amount = new BigDecimal(amountString);
            } catch (NumberFormatException e) {
                System.out.println("⚠ Lỗi parse transferAmount: " + e.getMessage());
                return ResponseEntity.badRequest().body("⚠ Lỗi parse transferAmount");
            }

            // Chuyển đổi giá trị transactionDate
            LocalDateTime transactionDate;
            try {
                transactionDate = LocalDateTime.parse(transactionDateString.replace(" ", "T"));
            } catch (Exception e) {
                System.out.println("⚠ Lỗi parse transactionDate: " + e.getMessage());
                return ResponseEntity.badRequest().body("⚠ Lỗi parse transactionDate");
            }

            // Lấy Order ID từ nội dung giao dịch
            String orderId = extractOrderId(transactionContent);
            if (orderId == null) {
                System.out.println("⚠ Không tìm thấy Order ID trong nội dung giao dịch!");
                return ResponseEntity.badRequest().body("⚠ Không tìm thấy Order ID");
            }

            System.out.println("🔹 Order ID sau khi chuẩn hóa: " + orderId);

            Optional<Order> optionalOrder = orderRepository.findOrderById(orderId);
            if (optionalOrder.isEmpty()) {
                System.out.println("⚠ Không tìm thấy đơn hàng trong database!");
                return ResponseEntity.badRequest().body("⚠ Không tìm thấy đơn hàng");
            }

            Order order = optionalOrder.get();

            if (order.getTotal().compareTo(amount) != 0) {
                System.out.println("⚠ Số tiền không khớp! Đơn hàng: " + order.getTotal() + ", Webhook: " + amount);
                return ResponseEntity.badRequest().body("⚠ Số tiền không khớp với đơn hàng! Đơn hàng: " + order.getTotal() + ", Webhook: " + amount);
            }

            // Lưu giao dịch vào bảng transactions
            Transaction transaction = Transaction.builder()
                .gateway("SePay")
                .transactionDate(transactionDate)
                .referenceNumber(referenceNumber)
                .amountIn(amount)
                .accountNumber("96247VYDE5")
                .createdAt(LocalDateTime.now())
                .order(order)
                .build();
            transactionRepository.save(transaction);
            System.out.println("✅ Lưu transaction thành công!");

            // Cập nhật trạng thái thanh toán
            if ("in".equalsIgnoreCase(transferType)) {
                order.setPaymentStatus(Payment_Status_Enum.PAID);
                orderRepository.save(order);
                cartItemRepository.deleteByUser(order.getUser());
                System.out.println("✅ Cập nhật trạng thái PAID thành công cho order: " + order.getId());
            }

            return ResponseEntity.ok("✅ Giao dịch được xử lý thành công!");
        } catch (Exception e) {
            System.out.println("⚠ Lỗi xử lý webhook: " + e.getMessage());
            return ResponseEntity.badRequest().body("⚠ Lỗi xử lý webhook: " + e.getMessage());
        }
    }




    private String extractOrderId(String content) {
        if (content == null || content.isEmpty()) {
            System.out.println("⚠ Nội dung giao dịch trống!");
            return null;
        }

        // Chấp nhận cả hai dạng `ORD-YYYYMMDD-XXXXXX` và `ORDYYYYMMDDXXXXXX`
        Pattern pattern = Pattern.compile("(ORD-?\\d{8}-?[A-Z0-9]+)");
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            String rawOrderId = matcher.group(1);

            // Nếu Order ID không có dấu `-`, thêm vào
            if (!rawOrderId.contains("-")) {
                rawOrderId = rawOrderId.replaceFirst("ORD(\\d{8})([A-Z0-9]+)", "ORD-$1-$2");
            }

            System.out.println("🔹 Order ID sau khi chuẩn hóa: " + rawOrderId);
            return rawOrderId;
        }

        System.out.println("⚠ Không tìm thấy Order ID trong nội dung giao dịch!");
        return null;
    }


}
