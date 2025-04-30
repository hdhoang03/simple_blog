package com.example.blog.dto.response;

import com.example.blog.constaint.BanAction;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserBanResponse {
    String id;
    String userId;
    BanAction banAction;
    LocalDate bannedAt;
    LocalDate expiredAt;
}
