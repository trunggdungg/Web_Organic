package com.example.web_organic.modal.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateAddressRequest {
    String name;
    String phone;
    Integer districtId;
    String wardId;
    Integer provinceId;
    String addressDetail;
    String addressSelected;
    Boolean isDefault;
}
