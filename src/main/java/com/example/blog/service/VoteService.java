package com.example.blog.service;

import com.example.blog.dto.request.VoteRequest;
import com.example.blog.dto.response.VoteResponse;
import com.example.blog.entity.Post;
import com.example.blog.entity.User;
import com.example.blog.entity.Vote;
import com.example.blog.exception.AppException;
import com.example.blog.exception.ErrorCode;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.UserRepository;
import com.example.blog.repository.VoteRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class VoteService {
    VoteRepository voteRepository;
    UserRepository userRepository;
    PostRepository postRepository;

    public VoteResponse vote(String postId, VoteRequest request){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));

        Vote vote = voteRepository.findByUserAndPost(user, post).orElse(null);

        if(request.getValue() == 0){
            if(vote != null){
                voteRepository.delete(vote);
            }
            return VoteResponse.builder()
                    .value(0) //unvote
                    .postId(postId)
                    .username(username)
                    .build();
        }

        if(vote == null){
            vote = Vote.builder()
                    .user(user)
                    .post(post)
                    .createAt(LocalDateTime.now())
                    .build();
        }

        vote.setValue(request.getValue());
        vote.setUpdateAt(LocalDateTime.now());
        if(vote.getCreateAt() == null){
            vote.setCreateAt(LocalDateTime.now());
        }

        voteRepository.save(vote);
        return VoteResponse.builder()
                .postId(postId)
                .username(username)
                .value(vote.getValue())
                .build();
    }

    public int countUpVotes(String postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));
        return voteRepository.countByPostAndValue(post, 1);
    }

    public int countDownVotes(String postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));
        return voteRepository.countByPostAndValue(post, -1);
    }
}






