package com.example.web_organic.controller;

import com.example.web_organic.entity.Order;
import com.example.web_organic.entity.User;
import com.example.web_organic.modal.Enum.Status_Enum;
import com.example.web_organic.service.OrderService;
import com.example.web_organic.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
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
        } else if ("completed".equalsIgnoreCase(type)) {
            orders = orderService.getOrdersByStatus(page, pageSize, Status_Enum.COMPLETED);
        } else if ("canceled".equalsIgnoreCase(type)) {
            // Lấy danh sách đơn bị hủy và trả hàng
            Page<Order> canceledOrders = orderService.getOrdersByStatus(page, pageSize, Status_Enum.CANCELED);
            Page<Order> returnedOrders = orderService.getOrdersByStatus(page, pageSize, Status_Enum.RETURNED);

            // Gộp hai danh sách
            List<Order> mergedOrders = Stream.concat(
                canceledOrders.getContent().stream(),
                returnedOrders.getContent().stream()
            ).collect(Collectors.toList());

            // Tổng số phần tử để hỗ trợ phân trang
            long totalElements = canceledOrders.getTotalElements() + returnedOrders.getTotalElements();

            // Tạo Page mới với dữ liệu đã gộp
            orders = new PageImpl<>(mergedOrders, PageRequest.of(page, pageSize), totalElements);
        }
        else {
            orders = orderService.getAllOrders(page, pageSize); // Lấy tất cả đơn hàng nếu không có type
        }

        model.addAttribute("orders", orders);
        model.addAttribute("type", type); // Truyền vào model để highlight menu
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orders.getTotalPages());
        return "admin/orders-list";
    }


    @GetMapping("/user-list")
    public String userList(@RequestParam(required = false,defaultValue = "1") int page,
                           @RequestParam(required = false,defaultValue = "10") int pageSize,
                           Model model) {
            Page<User> users = userService.getAllUsers(page, pageSize);
            model.addAttribute("users", users);
        return "admin/user-list";
    }

}
