package com.example.web_organic.modal.response;

import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class CartStockResponse {
     Integer variantId;
     String productName;
     String variantName;
     Integer requestedQuantity;
     Integer availableStock;
}