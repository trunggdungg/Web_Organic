package com.example.web_organic.modal.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpSertProductRequestAdmin {
//    String imageUrl;
    String name;
    Integer brand;//
    Integer category;//
    String description;
    Boolean status;
    Integer discount;
    Boolean isFeatured;
}
