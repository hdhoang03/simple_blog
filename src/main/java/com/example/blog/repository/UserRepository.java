package com.example.blog.repository;

import com.example.blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);
//    @Query("SELECT u FROM User u WHERE u.username like %:keyword%")
//    List<User> searchByUsernameLike(@Param("keyword") String keyword);
    List<User> findByUsernameContaining(String keyword);//đơn giản hơn cách trên
    boolean existsByUsername(String username);
}
