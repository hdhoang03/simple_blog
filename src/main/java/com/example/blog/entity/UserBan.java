package com.example.blog.entity;

import com.example.blog.constaint.BanAction;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserBan {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @Enumerated(EnumType.STRING)
    BanAction banAction;

    @Column(name = "banned_at")
    LocalDateTime bannedAt;

    @Column(name = "expired_at")
    LocalDateTime expiredAt;
}
