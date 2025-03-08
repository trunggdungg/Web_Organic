package com.example.web_organic.entity;
import com.example.web_organic.modal.Enum.Category_Type;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)

@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String name;
    String slug;
    Boolean status;

    @Enumerated(EnumType.STRING)
    Category_Type type;
    
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
