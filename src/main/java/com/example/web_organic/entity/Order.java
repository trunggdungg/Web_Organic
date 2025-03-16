package com.example.web_organic.entity;

import com.example.web_organic.modal.Enum.Payment_Status_Enum;
import com.example.web_organic.modal.Enum.Status_Enum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @Column(length = 20) // Đảm bảo độ dài đủ cho "ORD-YYYYMMDD-XXXX"
     String id;
    @Column(columnDefinition = "TEXT")
    String addressSelect;
    @Column(columnDefinition = "TEXT")
    String addressDetail;
    String fullName;
    String phone;
    BigDecimal productCost;
    BigDecimal shippingCost;

    @Enumerated(EnumType.STRING)
    Status_Enum status;

    String paymentMethod;

    @Enumerated(EnumType.STRING)
    Payment_Status_Enum paymentStatus;

    BigDecimal total;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    LocalDateTime orderDate;//ngày đặt hàng
    LocalDateTime deliveryStartDate;//ngày bắt đầu giao hàng
    LocalDateTime deliveryEndDate;//ngày ket thuc giao hàng



}
