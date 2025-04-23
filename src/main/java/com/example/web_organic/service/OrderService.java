package com.example.web_organic.service;
import com.example.web_organic.entity.*;
import com.example.web_organic.modal.Enum.Payment_Status_Enum;
import com.example.web_organic.modal.Enum.Status_Enum;
import com.example.web_organic.modal.request.CancelOrderRequest;
import com.example.web_organic.modal.request.OrderRequest;
import com.example.web_organic.repository.*;
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
    @Autowired
    private ProductVariantRepository productVariantRepository;
    @Autowired
    private OrderCanCellationRepository orderCancellationRepository;


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

        // Tạo danh sách OrderDetail từ giỏ hàng
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

        switch (order.getPaymentMethod()) {
            case "COD" -> handleCODOrder(order, customerReceived);
            case "Bank" -> handleBankOrder(order, customerReceived);
            default -> throw new IllegalStateException("Phương thức thanh toán không được hỗ trợ!");
        }

        orderRepository.save(order);
    }

    private void handleCODOrder(Order order, boolean customerReceived) {
        switch (order.getStatus()) {
            case PENDING -> {
                // Giảm số lượng sản phẩm trong kho
                List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(order.getId());
                for (OrderDetail detail : orderDetails) {
                    ProductVariants variant = detail.getProductVariant();
                    if (variant.getStock() < detail.getQuantity()) {
                        throw new IllegalStateException("Không đủ hàng trong kho cho sản phẩm: " + variant.getProduct().getName());
                    }
                    variant.setStock(variant.getStock() - detail.getQuantity());
                    productVariantRepository.save(variant);
                }
                order.setStatus(Status_Enum.PROCESSING);
            }
            case PROCESSING -> order.setStatus(Status_Enum.SHIPPED);
            case SHIPPED -> updateOrderAfterShipping(order, customerReceived, false);
            default -> throw new IllegalStateException("Trạng thái không hợp lệ để xác nhận!");
        }
    }

    private void handleBankOrder(Order order, boolean customerReceived) {
        if (order.getPaymentStatus() != Payment_Status_Enum.PAID) {
            throw new IllegalStateException("Đơn hàng chưa được thanh toán!");
        }

        switch (order.getStatus()) {
            case PENDING -> {
                // Giảm số lượng sản phẩm trong kho
                List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(order.getId());
                for (OrderDetail detail : orderDetails) {
                    ProductVariants variant = detail.getProductVariant();
                    if (variant.getStock() < detail.getQuantity()) {
                        throw new IllegalStateException("Không đủ hàng trong kho cho sản phẩm: " + variant.getProduct().getName());
                    }
                    variant.setStock(variant.getStock() - detail.getQuantity());
                    productVariantRepository.save(variant);
                }
                order.setStatus(Status_Enum.PROCESSING);
            }
            case PROCESSING -> order.setStatus(Status_Enum.SHIPPED);
            case SHIPPED -> updateOrderAfterShipping(order, customerReceived, true);
            default -> throw new IllegalStateException("Trạng thái không hợp lệ để xác nhận!");
        }
    }

    private void updateOrderAfterShipping(Order order, boolean customerReceived, boolean isBankPayment) {
        if (customerReceived) {
            order.setStatus(Status_Enum.COMPLETED);
            if (!isBankPayment) {
                order.setPaymentStatus(Payment_Status_Enum.PAID);
            }
        } else {
            order.setStatus(Status_Enum.RETURNED);
            if (isBankPayment) {
                order.setPaymentStatus(Payment_Status_Enum.REFUNDED); // Hoàn tiền cho khách Bank
            } else {
                order.setPaymentStatus(Payment_Status_Enum.CANCELED); // Hủy đơn với COD
            }
        }
    }


    @Transactional
    public void cancelOrder(CancelOrderRequest cancelOrderRequest) {
        User currentUser = (User) httpSession.getAttribute("CURRENT_USER");
        if (currentUser == null) {
            throw new RuntimeException("Người dùng chưa đăng nhập");
        }

        // Kiểm tra xem đơn hàng có tồn tại và thuộc về người dùng hiện tại không
        Order order = orderRepository.findOrderById(cancelOrderRequest.getOrderId())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Bạn không có quyền hủy đơn hàng này");
        }

        // Kiểm tra xem đơn hàng có đang ở trạng thái PENDING không
        if (order.getStatus() != Status_Enum.PENDING) {
            throw new RuntimeException("Chỉ có thể hủy đơn hàng ở trạng thái chờ xác nhận");
        }

        // Cập nhật trạng thái đơn hàng
        order.setStatus(Status_Enum.CANCELED);
        order.setPaymentStatus(Payment_Status_Enum.CANCELED);
        orderRepository.save(order);

        // Tạo OrderCancellation
        String cancellationId = "CAN-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        OrderCancellation cancellation = OrderCancellation.builder()
            .id(cancellationId)
            .order(order)
            .user(currentUser)
            .reason(cancelOrderRequest.getReason())
            .cancelledAt(LocalDateTime.now())
            .build();

        orderCancellationRepository.save(cancellation);
    }


    public List<Order> getOrderByUserAndStatus(User user, Status_Enum statusEnum) {
        return orderRepository.findByUserAndStatus(user, statusEnum);
    }


    public List<OrderDetail> getOrderDetailsByOrderId(String id) {
        return orderDetailService.getOrderDetailsByOrderId(id);
    }


    public List<Order> getAllOrdersByStatus(Status_Enum status) {
        return orderRepository.findByStatus(status);
    }

    public long countAll() {
        return  orderRepository.count();
    }

    public long countByStatus(String status) {
        Status_Enum statusEnum = Status_Enum.valueOf(status.toUpperCase());
        return (long) orderRepository.countByStatus(statusEnum);
    }

    public List<Order> findOrderByNameUser(String name) {
        return orderRepository.findOrderByFullName(name);
    }
}
