package com.example.web_organic.modal.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequest {
   @NotEmpty(message = "Tên không được để trống!")
    String fullName;
    @NotEmpty(message = "Email không được để trống!")
        @Email(message = "Email không đúng định dạng!")
    String email;
    @NotEmpty(message = "Mật khẩu không được để trống!")
    String password;
}
