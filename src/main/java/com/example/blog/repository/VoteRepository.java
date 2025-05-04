package com.example.blog.repository;

import com.example.blog.entity.Post;
import com.example.blog.entity.User;
import com.example.blog.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VoteRepository extends JpaRepository<Vote, UUID> {
    Optional<Vote> findByUserAndPost(User user, Post post);
    int countByPostAndValue(Post post, int value); //đến số lượng upvote/downvote
}
