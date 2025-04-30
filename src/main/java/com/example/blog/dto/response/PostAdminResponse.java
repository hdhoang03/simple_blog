package com.example.blog.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostAdminResponse {
    String id;
    String title;
    String content;
    String username;
    LocalDate createAt;
    LocalDate updateAt;
    Integer commentCount;
}
