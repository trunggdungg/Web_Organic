package com.example.web_organic.modal.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class InsertImageProductRequest {
    Integer productId;
    MultipartFile imageUrl;
    String altText;
}
