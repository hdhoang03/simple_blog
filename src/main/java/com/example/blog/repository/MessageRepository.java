package com.example.blog.repository;

import com.example.blog.entity.Message;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, String> {
//    List<Message> findByFromUserAndToUserOrToUser(String fromUser, String toUser);
    @Query("SELECT m FROM Message m WHERE (m.fromUser = :fromUser AND m.toUser = :toUser) OR (m.fromUser = :toUser AND m.toUser = :fromUser)")
    List<Message> findByFromUserAndToUserOrToUserAndFromUser(@Param("fromUser") String fromUser, @Param("toUser") String toUser);
}
