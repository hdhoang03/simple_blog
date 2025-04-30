package com.example.blog.service;

import com.example.blog.constaint.BanAction;
import com.example.blog.dto.request.PostRequest;
import com.example.blog.dto.response.PostAdminResponse;
import com.example.blog.dto.response.PostUserResponse;
import com.example.blog.entity.Post;
import com.example.blog.entity.User;
import com.example.blog.exception.AppException;
import com.example.blog.exception.ErrorCode;
import com.example.blog.mapper.PostMapper;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostService {
    PostRepository postRepository;
    PostMapper postMapper;
    UserRepository userRepository;

    public PostUserResponse createPost(PostRequest request){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        user.getBans().stream()
                .filter(ban -> ban.getBanAction() == BanAction.POST)
                .filter(ban -> ban.getExpiredAt() == null || ban.getExpiredAt().isAfter(LocalDateTime.now()))
                .findAny()
                .ifPresent(ban -> {
                    throw new AppException(ErrorCode.USER_BANNED_FROM_POSTING);
                });

        Post post = postMapper.toPost(request);
        post.setUser(user);
        post.setCreateAt(LocalDateTime.now());
        post.setUpdateAt(null);

        post = postRepository.save(post);
        return postMapper.toPostUserResponse(post);
    }

    public List<PostUserResponse> getAllPostsForUser(){
        return postRepository.findAll().stream()
                .map(postMapper::toPostUserResponse)
                .toList();
    }

    public List<PostAdminResponse> getAllPostForAdmin(){
        return postRepository.findAll().stream()
                .map(postMapper::toPostAdminResponse)
                .toList();
    }

    public PostUserResponse getPostForUserById(String id){
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));
        return postMapper.toPostUserResponse(post);
    }

    public PostAdminResponse getPostForAdminById(String id){
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));
        return postMapper.toPostAdminResponse(post);
    }

    public PostUserResponse updatePost(String id, PostRequest request){
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!post.getUser().getUsername().equals(username)){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        User user =post.getUser();
        user.getBans().stream()
                .filter(ban -> ban.getBanAction() == BanAction.POST)
                .filter(ban -> ban.getExpiredAt() == null || ban.getExpiredAt().isAfter(LocalDateTime.now()))
                .findAny()
                .ifPresent(ban -> {
                    throw new AppException(ErrorCode.USER_BANNED_FROM_POSTING);
                });

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setUpdateAt(LocalDateTime.now());

        return postMapper.toPostUserResponse(postRepository.save(post));
    }

    public void deletePost(String id){
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        boolean isOwner = post.getUser().getUsername().equals(username);
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ADMIN"));

        if(!isOwner && !isAdmin){
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
        postRepository.delete(post);
    }
}
