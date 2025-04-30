package com.example.blog.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "username", unique = true, columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    String username;
    String password;
    String name;
    String email;
    LocalDate dob;
    Boolean enabled; //Chặn tài khoản không cho đăng nhập

    @OneToMany(mappedBy = "user")
    List<Comment> comments;

    @ManyToMany
    Set<Role> roles;

    @ManyToMany
    Set<Permission> permissions;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<UserBan> bans;
}
