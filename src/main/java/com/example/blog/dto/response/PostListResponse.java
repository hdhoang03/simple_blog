package com.example.blog.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostListResponse {
    List<PostUserResponse> posts;
    int currentPage;
    int totalPages;
    int totalElements;
    int pageSize;
}
