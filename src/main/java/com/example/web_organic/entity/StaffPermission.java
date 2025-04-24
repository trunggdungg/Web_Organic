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
@Table(name = "staff_permissions")
public class StaffPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    Boolean canManageOrders;
    Boolean canManageProducts;
    Boolean canManageBlogs;
    Boolean canChat;
    Boolean canManageUsers;
}