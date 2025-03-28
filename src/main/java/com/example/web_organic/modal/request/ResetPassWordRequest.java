package com.example.web_organic.modal.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ResetPassWordRequest {
    String passwordCurrent;
    String passwordNew;
}
