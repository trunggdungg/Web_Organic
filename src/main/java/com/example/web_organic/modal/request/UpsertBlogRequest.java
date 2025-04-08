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
public class UpsertBlogRequest {
    String title;
    String content;
    Integer categoryId;
    MultipartFile thumbnail;
    Boolean status;
    String description;
}
