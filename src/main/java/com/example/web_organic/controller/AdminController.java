package com.example.web_organic.controller;

import com.example.web_organic.entity.Order;
import com.example.web_organic.modal.Enum.Status_Enum;
import com.example.web_organic.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private OrderService orderService;
    @GetMapping("/home")
    public String home() {
        return "admin/home";
    }

    @GetMapping("/order")
    public String order(@RequestParam(required = false, defaultValue = "1") int page,
                        @RequestParam(required = false, defaultValue = "10") int pageSize,
                        @RequestParam(required = false) String type, Model model) {

        Page<Order> orders = Page.empty();
        if ("pending".equalsIgnoreCase(type)) {
            orders = orderService.getOrdersByStatus(page, pageSize, Status_Enum.PENDING);
        } else if ("processing".equalsIgnoreCase(type)) {
            orders = orderService.getOrdersByStatus(page, pageSize, Status_Enum.PROCESSING);
        } else if ("shipped".equalsIgnoreCase(type)) {
            orders = orderService.getOrdersByStatus(page, pageSize, Status_Enum.SHIPPED);
        } else if ("delivered".equalsIgnoreCase(type)) {
            orders = orderService.getOrdersByStatus(page, pageSize, Status_Enum.DELIVERED);
        } else if ("canceled".equalsIgnoreCase(type)) {
            orders = orderService.getOrdersByStatus(page, pageSize, Status_Enum.CANCELED);
        }
        else {
            orders = orderService.getAllOrders(page, pageSize); // Lấy tất cả đơn hàng nếu không có type
        }

        model.addAttribute("orders", orders);
        model.addAttribute("type", type); // Truyền vào model để highlight menu
        return "admin/orders-list";
    }

}
