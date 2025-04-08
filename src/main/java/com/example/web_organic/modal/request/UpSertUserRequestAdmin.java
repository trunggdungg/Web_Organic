package com.example.web_organic.modal.request;

import com.example.web_organic.modal.Enum.User_Role;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpSertUserRequestAdmin {
    String fullName;
    String email;
    String password;
    User_Role role;
    Boolean isActive;
}
