package com.example.web_organic.rest.admin;

import com.example.web_organic.entity.Order;
import com.example.web_organic.repository.OrderRepository;
import com.example.web_organic.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")

public class OrderApiAdmin {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;
    // Cập nhật trạng thái đơn hàng COD
    @PutMapping("/confirm/order/{orderId}")
    public ResponseEntity<?> confirmOrder(@PathVariable String orderId, @RequestParam boolean customerReceived) {
        orderService.confirmOrder(orderId, customerReceived);

        // Sau khi cập nhật, lấy danh sách mới từ DB và trả về
        List<Order> updatedOrders = orderRepository.findAll();
        return ResponseEntity.ok(updatedOrders);
    }
}
