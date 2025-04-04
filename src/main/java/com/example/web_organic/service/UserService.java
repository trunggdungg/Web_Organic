package com.example.web_organic.service;
import com.example.web_organic.entity.Address;
import com.example.web_organic.entity.TokenConfirm;
import com.example.web_organic.entity.User;
import com.example.web_organic.exception.GlobalExceptionHandler;
import com.example.web_organic.exception.MethodNotAllowedException;
import com.example.web_organic.exception.UnauthorizedException;
import com.example.web_organic.modal.Enum.Token_Type;
import com.example.web_organic.modal.Enum.User_Role;
import com.example.web_organic.modal.request.LoginRequest;
import com.example.web_organic.modal.request.RegisterRequest;
import com.example.web_organic.modal.request.ResetPassWordRequest;
import com.example.web_organic.modal.request.UpSertUserRequestAdmin;
import com.example.web_organic.modal.response.TokenConfirmMessageResponse;
import com.example.web_organic.repository.TokenRepository;
import com.example.web_organic.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private MailService mailService;
    public void login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));


        if (!user.getIsActivated()) {
            throw new RuntimeException("Account is not verify");
        }

        if (!bCryptPasswordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Password is incorrect");
        }



        httpSession.setAttribute("CURRENT_USER", user);
    }

    public void logout() {
        httpSession.removeAttribute("CURRENT_USER");
    }

    public void signup(RegisterRequest registerRequest)  {
        Optional<User> userOptional = userRepository.findByEmail(registerRequest.getEmail());
        if (userOptional.isPresent()) {
            throw new RuntimeException("Email is already taken");
        }
        User user = User.builder()
            .fullName(registerRequest.getFullName())
            .email(registerRequest.getEmail())
            .password(bCryptPasswordEncoder.encode(registerRequest.getPassword()))
            .isActivated(false)
            .role(User_Role.USER)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        userRepository.save(user);

        //sinh token gui mail xac thuc
        TokenConfirm tokenConfirm = TokenConfirm.builder()
            .token(UUID.randomUUID().toString())
            .tokenType(Token_Type.CONFIRM_REGISTER)
            .user(user)
            .createdAt(LocalDateTime.now())
            .expiredAt(LocalDateTime.now().plusHours(1))
            .build();
        tokenRepository.save(tokenConfirm);

        //gui mail xac thuc
        String link = "http://localhost:8083/xac-thuc-tai-khoan?token=" + tokenConfirm.getToken();
        System.out.println("Link xac thuc: " + link);

        mailService.sendMailResigter(user.getEmail(),
            "Xác thực tài khoản",
            "Click vào link sau để xác thực tài khoản: " + link);
    }

    public TokenConfirmMessageResponse verifyAccount(String token) {
        //kiem tra token co ton tai hay khong
        Optional<TokenConfirm> tokenConfirmOptional = tokenRepository.findByTokenAndTokenType(token, Token_Type.CONFIRM_REGISTER);
        if (tokenConfirmOptional.isEmpty()) {
            return TokenConfirmMessageResponse.builder()
                .is_success(false)
                .message("Token không tồn tại")
                .build();

        }

        TokenConfirm tokenConfirm = tokenConfirmOptional.get();
        // da duoc xac thuc hay chua
        if (tokenConfirm.getConfirmedAt() != null) {
            return TokenConfirmMessageResponse.builder()
                .is_success(false)
                .message("Token đã được xác thực")
                .build();
        }
        //kiem tra xem token da het han chua
        if (tokenConfirm.getExpiredAt().isBefore(LocalDateTime.now())) {
            return TokenConfirmMessageResponse.builder()
                .is_success(false)
                .message("Token đã hết hạn")
                .build();
        }

        //xac thuc tai khoan
        User user = tokenConfirm.getUser();
        user.setIsActivated(true);
        userRepository.save(user);


        //cap nhat thoi gian xac thuc
        tokenConfirm.setConfirmedAt(LocalDateTime.now());
        tokenRepository.save(tokenConfirm);


        return TokenConfirmMessageResponse.builder()
            .is_success(true)
            .message("Xác thực tài khoản thành công")
            .build();
    }


    public void resetPassword(ResetPassWordRequest resetPassWordRequest) {
        User user = (User) httpSession.getAttribute("CURRENT_USER");
        if (user == null) {
            throw new RuntimeException("User not logged in");
        }

        if (!bCryptPasswordEncoder.matches(resetPassWordRequest.getPasswordCurrent(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPassword(bCryptPasswordEncoder.encode(resetPassWordRequest.getPasswordNew()));
        userRepository.save(user);
    }

    public Page<User> getAllUsers(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        return userRepository.findAllUser(pageable);
    }

    public User create(UpSertUserRequestAdmin upSertUserRequestAdmin) {
        Optional<User> userOptional = userRepository.findByEmail(upSertUserRequestAdmin.getEmail());
        if (userOptional.isPresent()) {
            throw new RuntimeException("Email is already taken");
        }
        User user = User.builder()
            .fullName(upSertUserRequestAdmin.getFullName())
            .email(upSertUserRequestAdmin.getEmail())
            .password(bCryptPasswordEncoder.encode(upSertUserRequestAdmin.getPassword()))
            .role(upSertUserRequestAdmin.getRole())
            .isActivated(upSertUserRequestAdmin.getIsActive())
            .avatar("/assets/img/avatar-trang-4.jpg")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
      return userRepository.save(user);

    }
    public User update(UpSertUserRequestAdmin upSertUserRequestAdmin) {
     User user = (User) httpSession.getAttribute("CURRENT_USER");
        if (user == null) {
            throw new RuntimeException("User not logged in");
        }

        // Nếu không phải admin,chi cần kiểm tra người dùng đang đăng nhập có phải là admin hay không
        if (!user.getRole().equals(User_Role.ADMIN)) {
                throw new RuntimeException("Ban khong co quyen thuc hien chuc nang nay");
        }

        user.setFullName(upSertUserRequestAdmin.getFullName());
        user.setRole(upSertUserRequestAdmin.getRole());
        user.setEmail(upSertUserRequestAdmin.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(upSertUserRequestAdmin.getPassword()));
        user.setIsActivated(upSertUserRequestAdmin.getIsActive());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }


    public void delete(int id) {
        User user = (User) httpSession.getAttribute("CURRENT_USER");
        if (user == null) {
            throw new UnauthorizedException("Bạn chưa đăng nhập");
        }
        if (!user.getRole().equals(User_Role.ADMIN)) {
            throw new MethodNotAllowedException("Bạn không có quyền thực hiện chức năng này");
        }
        userRepository.deleteById(id);
    }


}
