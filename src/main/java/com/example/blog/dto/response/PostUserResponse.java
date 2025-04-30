package com.example.blog.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostUserResponse {
    String title;
    String content;
    String username;
    LocalDateTime createAt;
//    List<CommentResponse> comments;
}
