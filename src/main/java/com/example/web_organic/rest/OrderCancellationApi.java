package com.example.web_organic.rest;

import com.example.web_organic.modal.request.CancelOrderRequest;
import com.example.web_organic.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderCancellationApi {

    @Autowired
    private OrderService orderService;

    @PostMapping("/cancel")
    public ResponseEntity<?> cancelOrder(@RequestBody CancelOrderRequest request) {
        try {
            orderService.cancelOrder(request);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đơn hàng đã được hủy thành công"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/cancel-reasons")
    public ResponseEntity<?> getCancelReasons() {
        List<String> reasons = new ArrayList<>();
        reasons.add("Tôi muốn thay đổi địa chỉ giao hàng");
        reasons.add("Tôi muốn thay đổi phương thức thanh toán");
        reasons.add("Tôi đã đặt nhầm sản phẩm");
        reasons.add("Tôi không còn nhu cầu mua sản phẩm này nữa");
        reasons.add("Tôi tìm thấy giá tốt hơn ở nơi khác");
        reasons.add("Thời gian giao hàng quá lâu");
        reasons.add("Lý do khác");

        return ResponseEntity.ok(reasons);
    }
}