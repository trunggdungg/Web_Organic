package com.example.web_organic.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)

@Entity
@Table(name = "order_cancellations")
public class OrderCancellation {

    @Id
    @Column(length = 20) // Đảm bảo độ dài đủ cho "ORD-YYYYMMDD-XXXX"
    String id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    Order order;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    String reason;
    LocalDateTime cancelledAt;
}
