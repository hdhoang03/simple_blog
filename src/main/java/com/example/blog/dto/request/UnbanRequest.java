package com.example.blog.dto.request;

import com.example.blog.constaint.BanAction;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UnbanRequest {
    String userId;
    BanAction banAction;
}
