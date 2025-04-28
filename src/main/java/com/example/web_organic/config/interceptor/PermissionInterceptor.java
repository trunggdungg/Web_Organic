package com.example.web_organic.config.interceptor;

import com.example.web_organic.entity.StaffPermission;
import com.example.web_organic.entity.User;
import com.example.web_organic.modal.Enum.User_Role;
import com.example.web_organic.service.PermissionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class PermissionInterceptor implements HandlerInterceptor {

    @Autowired
    private PermissionService permissionService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        User user = (User) request.getSession().getAttribute("CURRENT_USER");

        if (user == null) {
            response.sendRedirect("/login");
            return false;
        }

        if (user.getRole() != User_Role.ADMIN && user.getRole() != User_Role.STAFF) {
            response.sendRedirect("/"); // Chuyển hướng về trang chủ
            return false;
        }

        if (request.getSession().getAttribute("STAFF_PERMISSION") == null) {
            if (user.getRole() == User_Role.ADMIN) {
                StaffPermission adminPermission = StaffPermission.builder()
                    .canManageOrders(true)
                    .canManageProducts(true)
                    .canManageBlogs(true)
                    .canChat(true)
                    .canManageUsers(true)
                    .build();
                request.getSession().setAttribute("STAFF_PERMISSION", adminPermission);
            } else if (user.getRole() == User_Role.STAFF) {
                StaffPermission permission = permissionService.getPermission(user);
                request.getSession().setAttribute("STAFF_PERMISSION", permission);
            } else {
                StaffPermission defaultPermission = StaffPermission.builder()
                    .canManageOrders(false)
                    .canManageProducts(false)
                    .canManageBlogs(false)
                    .canChat(false)
                    .canManageUsers(false)
                    .build();
                request.getSession().setAttribute("STAFF_PERMISSION", defaultPermission);
            }
        }

        // Admin có tất cả quyền
        if (user.getRole() == User_Role.ADMIN) {
            return true;
        }

        // Kiểm tra quyền của STAFF
        if (user.getRole() == User_Role.STAFF) {
            StaffPermission permission = permissionService.getPermission(user);

            if (path.contains("/admin/order") && !permission.getCanManageOrders()) {
                response.sendRedirect("/admin/home");
                return false;
            }

            if (path.contains("/admin/product") && !permission.getCanManageProducts()) {
                response.sendRedirect("/admin/home");
                return false;
            }

            if (path.contains("/admin/blog") && !permission.getCanManageBlogs()) {
                response.sendRedirect("/admin/home");
                return false;
            }

            if (path.contains("/admin/chat") && !permission.getCanChat()) {
                response.sendRedirect("/admin/home");
                return false;
            }

            if (path.contains("/admin/user") && !permission.getCanManageUsers()) {
                response.sendRedirect("/admin/home");
                return false;
            }
        }

        return true;
    }
}