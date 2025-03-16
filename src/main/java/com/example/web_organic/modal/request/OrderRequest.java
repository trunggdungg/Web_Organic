package com.example.web_organic.modal.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderRequest {
    String addressDetail;
    String addressSelect;
    String fullName;
//    LocalDateTime orderDate;
    String paymentMethod;
//    String paymentStatus;
    String phone;
    BigDecimal productCost;
    BigDecimal shippingCost;
//    String status;
    BigDecimal total;
//    Integer userId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime deliveryStartDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime deliveryEndDate;
}
