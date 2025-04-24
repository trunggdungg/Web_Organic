package com.example.web_organic.service;

import com.example.web_organic.entity.StaffPermission;
import com.example.web_organic.entity.User;
import com.example.web_organic.modal.Enum.User_Role;
import com.example.web_organic.repository.StaffPermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PermissionService {
    @Autowired
    private StaffPermissionRepository staffPermissionRepository;

    public StaffPermission getPermission(User user) {
        if (user.getRole() == User_Role.ADMIN) {
            // Admin có tất cả quyền
            return StaffPermission.builder()
                .canManageOrders(true)
                .canManageProducts(true)
                .canManageBlogs(true)
                .canChat(true)
                .canManageUsers(true)
                .build();
        } else if (user.getRole() == User_Role.STAFF) {
            // Nếu là STAFF, lấy quyền từ database
            return staffPermissionRepository.findByUser(user).orElse(
                StaffPermission.builder()
                    .user(user)
                    .canManageOrders(false)
                    .canManageProducts(false)
                    .canManageBlogs(false)
                    .canChat(false)
                    .canManageUsers(false)
                    .build()
            );
        }
        // Mặc định không có quyền
        return StaffPermission.builder()
            .canManageOrders(false)
            .canManageProducts(false)
            .canManageBlogs(false)
            .canChat(false)
            .canManageUsers(false)
            .build();
    }

    public StaffPermission updatePermission(User user, StaffPermission permission) {
        // Chỉ cập nhật nếu là STAFF
        if (user.getRole() != User_Role.STAFF) {
            throw new RuntimeException("Chỉ cập nhật quyền cho nhân viên");
        }

        StaffPermission existingPermission = staffPermissionRepository.findByUser(user)
            .orElse(new StaffPermission());

        existingPermission.setUser(user);
        existingPermission.setCanManageOrders(permission.getCanManageOrders());
        existingPermission.setCanManageProducts(permission.getCanManageProducts());
        existingPermission.setCanManageBlogs(permission.getCanManageBlogs());
        existingPermission.setCanChat(permission.getCanChat());
        existingPermission.setCanManageUsers(permission.getCanManageUsers());

        return staffPermissionRepository.save(existingPermission);
    }
}