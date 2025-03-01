package com.example.web_organic.entity;

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
@Table(name = "blogs")
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String title;
    String slug;
    @Column(columnDefinition = "TEXT")
    String content;
    String description;
    String thumbnail;
    Boolean status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    LocalDateTime publishedAt;
    String category;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;
}
