package com.example.web_organic.entity;

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
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(nullable = false)
    String gateway;

    @Column(nullable = false)
    LocalDateTime transactionDate;

    String accountNumber;
    String subAccount;
    BigDecimal amountIn;
    BigDecimal amountOut;
    BigDecimal accumulated;
    String code;

    @Column(columnDefinition = "TEXT")
    String transactionContent;

    @Column(unique = true)
    String referenceNumber;

    @Column(columnDefinition = "TEXT")
    String body;

    @Column(nullable = false)
    LocalDateTime createdAt;

    // Ánh xạ từ Transaction sang Order
    @OneToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    Order order;
}

