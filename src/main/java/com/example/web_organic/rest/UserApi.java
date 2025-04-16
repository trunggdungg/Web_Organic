package com.example.web_organic.rest;

import com.example.web_organic.entity.User;
import com.example.web_organic.modal.request.*;
import com.example.web_organic.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserApi {
    @Autowired
    private UserService userService;
    @Autowired
    private HttpSession httpSession;
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        userService.login(loginRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        userService.logout();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody RegisterRequest registerRequest) {
        userService.signup(registerRequest);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/check-login")
    public ResponseEntity<?> checkLogin(HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("CURRENT_USER");
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser() {
        User user = (User) httpSession.getAttribute("CURRENT_USER");

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }

        // Trả về thông tin user, có thể dùng DTO nếu cần
//        UserResponse userResponse = new UserResponse(user.getId(), user.getEmail(), user.getFullName(), user.getAvatar());
        return ResponseEntity.ok(user);
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPassWordRequest resetPassWordRequest) {
        userService.resetPassword(resetPassWordRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgetPasswordRequest forgetPasswordRequest) {
        userService.forgotPassword(forgetPasswordRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetForgotPassword(@RequestBody ResetForgotPasswordRequest resetForgotPasswordRequest) {
        userService.resetForgotPassword(resetForgotPasswordRequest);
        return ResponseEntity.ok().build();
    }

}
