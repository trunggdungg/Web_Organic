package com.example.web_organic.entity;

import com.example.web_organic.modal.Enum.Token_Type;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "token_confirm")
public class TokenConfirm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    private String token; // chuỗi token
    @Enumerated(EnumType.STRING)
    private Token_Type tokenType; // loại token

    private LocalDateTime createdAt; // thời gian tạo
    private LocalDateTime expiredAt; // thời gian hết hạn
    private LocalDateTime confirmedAt; // thời gian xác thực

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // token này của user nào
}
