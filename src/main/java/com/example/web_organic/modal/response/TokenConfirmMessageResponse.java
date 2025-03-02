package com.example.web_organic.modal.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class TokenConfirmMessageResponse {
    Boolean is_success;
    String message;
}
