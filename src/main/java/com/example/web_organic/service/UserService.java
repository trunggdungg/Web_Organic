package com.example.web_organic.service;
import com.example.web_organic.entity.Address;
import com.example.web_organic.entity.TokenConfirm;
import com.example.web_organic.entity.User;
import com.example.web_organic.exception.GlobalExceptionHandler;
import com.example.web_organic.exception.MethodNotAllowedException;
import com.example.web_organic.exception.UnauthorizedException;
import com.example.web_organic.modal.Enum.Token_Type;
import com.example.web_organic.modal.Enum.User_Role;
import com.example.web_organic.modal.request.*;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
    // Lưu trữ người dùng trực tuyến: username -> sessionId
    private final Map<String, String> onlineUsers = new ConcurrentHashMap<>();

    // Thêm phương thức để quản lý người dùng trực tuyến
    public void addUser(String username, String sessionId) {
        onlineUsers.put(username, sessionId);
    }

    public void removeUser(String username) {
        onlineUsers.remove(username);
    }

    public List<String> getOnlineUsers() {
        return new ArrayList<>(onlineUsers.keySet());
    }

    public boolean isUserOnline(String username) {
        return onlineUsers.containsKey(username);
    }

    // Phương thức tìm admin nếu cần
    public User findAdminUser() {
        return userRepository.findByRole(User_Role.ADMIN);
    }

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

    public void signup(RegisterRequest registerRequest) {
        Optional<User> userOptional = userRepository.findByEmail(registerRequest.getEmail());
        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();
            if (existingUser.getIsActivated()) {
                throw new RuntimeException("Tài khoản đã được kích hoạt");
            } else {
                // Kiểm tra xem token đã được xác thực hay chưa
                Optional<TokenConfirm> tokenOptional = tokenRepository.findByUserAndTokenType(existingUser, Token_Type.CONFIRM_REGISTER);
                if (tokenOptional.isPresent() && tokenOptional.get().getConfirmedAt() != null) {
                    throw new RuntimeException("Tài khoản đã bị cấm");
                } else {
                    // Tạo token mới và gửi lại email xác thực
                    TokenConfirm tokenConfirm = TokenConfirm.builder()
                        .token(UUID.randomUUID().toString())
                        .tokenType(Token_Type.CONFIRM_REGISTER)
                        .user(existingUser)
                        .createdAt(LocalDateTime.now())
                        .expiredAt(LocalDateTime.now().plusHours(1))
                        .build();
                    tokenRepository.save(tokenConfirm);

                    String link = "http://localhost:8083/xac-thuc-tai-khoan?token=" + tokenConfirm.getToken();
                    System.out.println("Link xac thuc: " + link);

                    mailService.sendMailResigter(existingUser.getEmail(),
                        "Xác thực tài khoản",
                        "Click vào link sau để xác thực tài khoản: " + link);
                    return;
                }
            }
        }

        // tạo mới tài khoản
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

        // tạo token xác thực
        TokenConfirm tokenConfirm = TokenConfirm.builder()
            .token(UUID.randomUUID().toString())
            .tokenType(Token_Type.CONFIRM_REGISTER)
            .user(user)
            .createdAt(LocalDateTime.now())
            .expiredAt(LocalDateTime.now().plusHours(1))
            .build();
        tokenRepository.save(tokenConfirm);

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

    public TokenConfirmMessageResponse verifyResetPasword(String token) {
        //kiem tra token co ton tai hay khong
        Optional<TokenConfirm> tokenConfirmOptional = tokenRepository.findByTokenAndTokenType(token, Token_Type.FORGOT_PASSWORD);
        if (tokenConfirmOptional.isEmpty()) {
            return TokenConfirmMessageResponse.builder()
                .is_success(false)
                .message("Token không tồn tại")
                .build();

        }
        //token da duoc xac thuc hay chua
        TokenConfirm tokenConfirm = tokenConfirmOptional.get();
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


        //xac thuc thanh cong
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
            throw new RuntimeException("Email đã tồn tại!");
        }
        // kiểm tra quyền nếu là Admin thì mới tạo mới user
        User currentUser = (User) httpSession.getAttribute("CURRENT_USER");
        if (currentUser == null) {
            throw new UnauthorizedException("Bạn chưa đăng nhập");
        }
        if (!currentUser.getRole().equals(User_Role.ADMIN)) {
            throw new MethodNotAllowedException("Bạn không có quyền thực hiện chức năng này");
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
    public User update(Integer id, UpSertUserRequestAdmin upSertUserRequestAdmin) {
        User currentUser = (User) httpSession.getAttribute("CURRENT_USER");
        if (currentUser == null) {
            throw new RuntimeException("User not logged in");
        }

        if (currentUser.getRole() != User_Role.ADMIN) {
            throw new RuntimeException("Bạn không có quyền thực hiện chức năng này");
        }

        // Kiểm tra email đã tồn tại
        Optional<User> existingUser = userRepository.findByEmail(upSertUserRequestAdmin.getEmail());
        if (existingUser.isPresent() && !Objects.equals(existingUser.get().getId(), id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email đã tồn tại");
        }

        User user = userRepository.findUserById(id);
        System.out.println("User cần cập nhật: " + user);

        user.setFullName(upSertUserRequestAdmin.getFullName());
        user.setRole(upSertUserRequestAdmin.getRole());
        user.setEmail(upSertUserRequestAdmin.getEmail());

        if (upSertUserRequestAdmin.getPassword() != null && !upSertUserRequestAdmin.getPassword().isEmpty()) {
            user.setPassword(bCryptPasswordEncoder.encode(upSertUserRequestAdmin.getPassword()));
        }

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


    public User findByRole(User_Role userRole) {
        return userRepository.findByRole(userRole);
    }

    public User findById(Integer senderId) {
            return userRepository.findUserById(senderId);
    }



    public void forgotPassword(ForgetPasswordRequest forgetPasswordRequest) {
        Optional<User> userOptional = userRepository.findByEmail(forgetPasswordRequest.getEmail());
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Email không tồn tại");
        }
        User user = userOptional.get();
        TokenConfirm tokenConfirm = TokenConfirm.builder()
            .token(UUID.randomUUID().toString())
            .tokenType(Token_Type.FORGOT_PASSWORD)
            .user(user)
            .createdAt(LocalDateTime.now())
            .expiredAt(LocalDateTime.now().plusHours(1))
            .build();
        tokenRepository.save(tokenConfirm);

        //gui mail xac thuc
        String link = "http://localhost:8083/dat-lai-mat-khau?token=" + tokenConfirm.getToken();
        System.out.println("Link xac thuc: " + link);

        mailService.sendMailResigter(user.getEmail(),
            "Xác thực tài khoản",
            "Click vào link sau để xác thực tài khoản: " + link);
    }

    public void resetForgotPassword(ResetForgotPasswordRequest resetForgotPasswordRequest) {
        //kiem tra token co ton tai hay khong
        Optional<TokenConfirm> tokenConfirmOptional = tokenRepository.findByTokenAndTokenType(resetForgotPasswordRequest.getToken(), Token_Type.FORGOT_PASSWORD);
        if (tokenConfirmOptional.isEmpty()) {
           throw new RuntimeException("Token không tồn tại");
        }
        //token da duoc xac thuc hay chua
        TokenConfirm tokenConfirm = tokenConfirmOptional.get();
        if (tokenConfirm.getConfirmedAt() != null) {
            throw new RuntimeException("Token đã được xác thực");
        }

        //kiem tra xem token da het han chua
        if (tokenConfirm.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token đã hết hạn");
        }

        //xac thuc thanh cong
        User user = tokenConfirm.getUser();
        user.setPassword(bCryptPasswordEncoder.encode(resetForgotPasswordRequest.getNewPassword()));
        userRepository.save(user);

        //cap nhat thoi gian xac thuc
        tokenConfirm.setConfirmedAt(LocalDateTime.now());
        tokenRepository.save(tokenConfirm);



    }
}
