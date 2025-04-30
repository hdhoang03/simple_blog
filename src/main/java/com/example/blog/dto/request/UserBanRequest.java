package com.example.blog.dto.request;

import com.example.blog.constaint.BanAction;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserBanRequest {
    String userId;
    BanAction banAction;
    LocalDateTime expiredAt; //null = vĩnh viễn
}
