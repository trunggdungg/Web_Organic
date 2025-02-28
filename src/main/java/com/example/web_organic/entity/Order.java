package com.example.web_organic.entity;

import com.example.web_organic.modal.Enum.Payment_Status_Enum;
import com.example.web_organic.modal.Enum.Status_Enum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.text.DecimalFormat;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @Column(columnDefinition = "TEXT")
    String addressSelect;
    @Column(columnDefinition = "TEXT")
    String addressDetail;
    String fullName;
    String phone;
    DecimalFormat productCost;
    DecimalFormat shippingCost;
    Status_Enum status;
    String paymentMethod;
    Payment_Status_Enum paymentStatus;
    DecimalFormat total;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    LocalDateTime orderDate;

}
