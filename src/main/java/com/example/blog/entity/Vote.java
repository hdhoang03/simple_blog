package com.example.blog.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "votes", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "post_id"})})//chỉ 1 user 1 post 1 vote
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    UUID id;

    @ManyToOne
//    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey)
    User user;

    @ManyToOne
    Post post;

    int value; //1 = upvote, -1 = downvote, 0 = unvote

    LocalDateTime createAt;
    LocalDateTime updateAt;
}
