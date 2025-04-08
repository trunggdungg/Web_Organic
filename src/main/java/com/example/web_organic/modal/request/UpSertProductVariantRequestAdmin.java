package com.example.web_organic.modal.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpSertProductVariantRequestAdmin {
    Integer productId;
    BigDecimal price;
    Integer stock;
    String weight;
    Boolean isDefault;
}
