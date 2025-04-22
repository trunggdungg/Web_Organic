package com.example.web_organic;

import com.example.web_organic.entity.TokenConfirm;
import com.example.web_organic.entity.User;
import com.example.web_organic.modal.request.LoginRequest;
import com.example.web_organic.modal.request.RegisterRequest;
import com.example.web_organic.repository.TokenRepository;
import com.example.web_organic.repository.UserRepository;
import com.example.web_organic.service.MailService;
import com.example.web_organic.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private HttpSession httpSession;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private MailService mailService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLogin_Success() {
        // Chuẩn bị dữ liệu test
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setIsActivated(true);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        // Thiết lập hành vi cho mock objects
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);

        // Thực thi phương thức cần kiểm thử
        userService.login(loginRequest);

        // Kiểm tra kết quả
        verify(httpSession).setAttribute(eq("CURRENT_USER"), eq(user));

        System.out.println("Test Login Success: Người dùng đã đăng nhập thành công với email: " + loginRequest.getEmail());
    }

    @Test
    public void testLogin_UserNotFound() {
        // Chuẩn bị dữ liệu test
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        // Thiết lập hành vi cho mock objects
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // Kiểm tra xem có ném ra ngoại lệ không
        assertThatThrownBy(() -> userService.login(loginRequest))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("User not found");
        System.out.println("Test Login User Not Found: Không tìm thấy người dùng với email: " + loginRequest.getEmail());
    }

    @Test
    public void testSignup_Success() {
        // Chuẩn bị dữ liệu test
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFullName("Test User");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password");

        // Thiết lập hành vi cho mock objects
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Thực thi phương thức cần kiểm thử
        userService.signup(registerRequest);

        // Kiểm tra kết quả
        verify(userRepository).save(any(User.class));
        verify(tokenRepository).save(any(TokenConfirm.class));
        verify(mailService).sendMailResigter(eq("test@example.com"), anyString(), anyString());
        System.out.println("Test Signup Success: Người dùng đã đăng ký thành công với email: " + registerRequest.getEmail());
    }
}