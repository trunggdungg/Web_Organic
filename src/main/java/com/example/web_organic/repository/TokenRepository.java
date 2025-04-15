package com.example.web_organic.repository;

import com.example.web_organic.entity.TokenConfirm;
import com.example.web_organic.entity.User;
import com.example.web_organic.modal.Enum.Token_Type;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<TokenConfirm, Integer> {
    Optional<TokenConfirm> findByToken(String token);
    Optional<TokenConfirm> findByUser(User user);
    void deleteByToken(String token);

    Optional<TokenConfirm> findByTokenAndTokenType(String token, Token_Type tokenType);

    Optional<TokenConfirm> findByUserAndTokenType(User existingUser, Token_Type tokenType);
}
