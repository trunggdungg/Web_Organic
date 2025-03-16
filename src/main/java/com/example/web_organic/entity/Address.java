package com.example.web_organic.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)

@Entity
@Table(name = "address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String fullName;
    String phone;

    @Column(columnDefinition = "TEXT")
    String addressSelected;

    @Column(columnDefinition = "TEXT")
    String addressDetail;

    Boolean isDefault;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;
    // Thêm ID địa điểm từ GHN để tính thời gian giao hàng
    Integer provinceId;
    Integer districtId;
    String wardCode;
}
