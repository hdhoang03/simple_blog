package com.example.blog.repository;

import com.example.blog.entity.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvalidedTokenRepository extends JpaRepository<InvalidatedToken, String> {
}
