package com.example.blog.repository;

import com.example.blog.constaint.BanAction;
import com.example.blog.entity.UserBan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserBanRepository extends JpaRepository<UserBan, String> {
    List<UserBan> findByUserId(String userId);
    List<UserBan> findByExpiredAtAfter(LocalDateTime now);
    Optional<UserBan> findByUserIdAndBanAction(String userId, BanAction banAction);
}
