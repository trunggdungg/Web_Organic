package com.example.web_organic.service;
import com.example.web_organic.entity.*;
import com.example.web_organic.modal.Enum.Payment_Status_Enum;
import com.example.web_organic.modal.Enum.Status_Enum;
import com.example.web_organic.modal.request.OrderRequest;
import com.example.web_organic.repository.CartItemRepository;
import com.example.web_organic.repository.OrderDetailRepository;
import com.example.web_organic.repository.OrderRepository;
import com.example.web_organic.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private HttpSession httpSession;
    @Autowired
    private OrderDetailService orderDetailService;



    @Transactional
    public Order createOrder(OrderRequest orderRequest) {
        User currentUser = (User) httpSession.getAttribute("CURRENT_USER");

        // Lấy danh sách sản phẩm trong giỏ hàng của user
        List<CartItem> cartItems = cartItemRepository.findByUser(currentUser);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống, không thể tạo đơn hàng.");
        }
        // Create order
        Order order = Order.builder()
            .id(generateOrderCode())
            .addressDetail(orderRequest.getAddressDetail())
            .addressSelect(orderRequest.getAddressSelect())
            .fullName(orderRequest.getFullName())
            .orderDate(LocalDateTime.now())
            .paymentMethod(orderRequest.getPaymentMethod())
            .paymentStatus(Payment_Status_Enum.PENDING)
            .phone(orderRequest.getPhone())
            .productCost(orderRequest.getProductCost())
            .shippingCost(orderRequest.getShippingCost())
            .status(Status_Enum.PENDING)
            .total(orderRequest.getTotal())
            .user(currentUser)
            .deliveryStartDate(orderRequest.getDeliveryStartDate())
            .deliveryEndDate(orderRequest.getDeliveryEndDate())
            .build();
        orderRepository.save(order);


        // Lưu các sản phẩm từ giỏ hàng vào OrderDetail
//        List<OrderDetail> orderDetails = cartItems.stream().map(cartItem -> {
//            BigDecimal unitPrice = cartItem.getProductVariant().getPrice();
//            BigDecimal subTotal = unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
//
//            return OrderDetail.builder()
//                .id(UUID.randomUUID().toString().substring(0, 6).toUpperCase()) // Tạo ID ngẫu nhiên cho OrderDetail
//                .order(order)
//                .productVariant(cartItem.getProductVariant())
//                .unitPrice(unitPrice)
//                .quantity(cartItem.getQuantity())
//                .subTotal(subTotal)
//                .build();
//        }).collect(Collectors.toList());
        List<OrderDetail> orderDetails = cartItems.stream().map(cartItem -> {
            ProductVariants productVariant = cartItem.getProductVariant();
            BigDecimal priceOld = productVariant.getPrice();
            Integer discount = productVariant.getProduct().getDiscount(); // Lấy giảm giá từ Product
            BigDecimal priceAfterDiscount = priceOld.subtract(priceOld.multiply(BigDecimal.valueOf(discount).divide(BigDecimal.valueOf(100))));

            BigDecimal subTotal = priceAfterDiscount.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            return OrderDetail.builder()
                .id(UUID.randomUUID().toString().substring(0, 6).toUpperCase()) // Tạo ID ngẫu nhiên cho OrderDetail
                .order(order)
                .productVariant(productVariant)
                .unitPrice(priceAfterDiscount) // Giá đã giảm
                .quantity(cartItem.getQuantity())
                .subTotal(subTotal) // Tổng tiền sau giảm giá
                .build();
        }).collect(Collectors.toList());


        orderDetailRepository.saveAll(orderDetails); // Lưu toàn bộ OrderDetail

        // Xóa giỏ hàng nếu là COD (Với Bank, chỉ xóa sau khi thanh toán thành công)
        if ("COD".equalsIgnoreCase(orderRequest.getPaymentMethod())) {
            cartItemRepository.deleteByUser(currentUser);
        }
        return order;
    }


    private String generateOrderCode() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = UUID.randomUUID().toString().substring(0, 6).toUpperCase(); // Lấy 8 ký tự đầu từ UUID
        return "ORD-" + date + "-" + randomPart;
    }



    public Page<Order> getOrdersByStatus(int page, int pageSize, Status_Enum statusEnum) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        return orderRepository.findAllByStatus(statusEnum, pageable);
    }

    public Page<Order> getAllOrders(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        return orderRepository.findAll(pageable);
    }

    public void confirmOrder(String orderId, boolean customerReceived) {
        Order order = orderRepository.findOrderById(orderId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng!"));

        if (!"COD".equals(order.getPaymentMethod())) {
            throw new IllegalStateException("Chỉ hỗ trợ xác nhận đơn hàng cho phương thức COD!");
        }

        switch (order.getStatus()) {
            case PENDING -> order.setStatus(Status_Enum.PROCESSING); // Xác nhận đơn hàng
            case PROCESSING -> order.setStatus(Status_Enum.SHIPPED); // Đang giao hàng
            case SHIPPED -> {
                if (customerReceived) {
                    order.setStatus(Status_Enum.COMPLETED);
                    order.setPaymentStatus(Payment_Status_Enum.PAID);
                } else {
                    order.setStatus(Status_Enum.CANCELED);
                    order.setPaymentStatus(Payment_Status_Enum.CANCELED);
                }
            }
            default -> throw new IllegalStateException("Trạng thái không hợp lệ để xác nhận!");
        }

        orderRepository.save(order);
    }

    public List<Order> getOrderByUserAndStatus(User user, Status_Enum statusEnum) {
        return orderRepository.findByUserAndStatus(user, statusEnum);
    }


    public List<OrderDetail> getOrderDetailsByOrderId(String id) {
        return orderDetailService.getOrderDetailsByOrderId(id);
    }
}
