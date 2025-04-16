package com.example.web_organic.modal.request;

import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class CartStockCheckRequest {
     Integer variantId;
     Integer quantity;
}
