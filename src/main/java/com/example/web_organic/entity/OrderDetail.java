package com.example.web_organic.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)

@Entity
@Table(name = "order_details")
public class OrderDetail {
    @Id
    @Column(length = 20) // Đảm bảo độ dài đủ cho "ORD-YYYYMMDD-XXXX"
    String id;
    BigDecimal unitPrice;

    Integer quantity;
    BigDecimal subTotal;

    @ManyToOne
    @JoinColumn(name = "order_id")
    Order order;

    @ManyToOne
    @JoinColumn(name = "product_variant_id")
    ProductVariants productVariant;
}
