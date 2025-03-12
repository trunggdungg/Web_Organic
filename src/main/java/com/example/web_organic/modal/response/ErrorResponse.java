package com.example.web_organic.modal.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ErrorResponse {
    HttpStatus status;

    Object message;
    // đổi từ string mess sang object mess để đúng vơis kiểu dữ liệu của message trong các exception
}