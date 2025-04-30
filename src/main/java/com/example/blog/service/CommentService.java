package com.example.blog.service;

import com.example.blog.constaint.BanAction;
import com.example.blog.dto.request.CommentRequest;
import com.example.blog.dto.response.CommentResponse;
import com.example.blog.entity.Comment;
import com.example.blog.entity.Post;
import com.example.blog.entity.User;
import com.example.blog.exception.AppException;
import com.example.blog.exception.ErrorCode;
import com.example.blog.mapper.CommentMapper;
import com.example.blog.repository.CommentRepository;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CommentService {
    UserRepository userRepository;
    CommentMapper commentMapper;
    CommentRepository commentRepository;
    PostRepository postRepository;

    public CommentResponse createComment(CommentRequest request){
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        user.getBans().stream()
                .filter(ban -> ban.getBanAction() == BanAction.COMMENT)
                .filter(ban -> ban.getExpiredAt() == null || ban.getExpiredAt().isAfter(LocalDateTime.now()))
                .findAny()
                .ifPresent(ban -> {
                    throw new AppException(ErrorCode.USER_BANNED_FROM_COMMENTING);
                });

        Comment comment = commentMapper.toComment(request);
        comment.setUser(user);
        comment.setPost(post);
        comment.setCreateAt(LocalDateTime.now());
        comment.setUpdateAt(null);

        comment = commentRepository.save(comment);
        return commentMapper.toCommentResponse(comment);
    }

    public List<CommentResponse> getCommentsByPost(String postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));

        List<Comment> comments = commentRepository.findByPost(post);
        return comments.stream()
                .map(commentMapper::toCommentResponse)
                .toList();
    }

    public void deleteComment(String commentId){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        boolean isOwner = comment.getUser().getUsername().equals(username);
        boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN"));

        if(!isOwner && !isAdmin){
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

        commentRepository.delete(comment);
    }

    public CommentResponse updateComment(String commentId, CommentRequest request){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!comment.getUser().getUsername().equals(username)){
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

        User user = comment.getUser();
        user.getBans().stream()
                .filter(ban -> ban.getBanAction() == BanAction.COMMENT)
                .filter(ban -> ban.getExpiredAt() == null || ban.getExpiredAt().isAfter(LocalDateTime.now()))
                .findAny()
                .ifPresent(ban -> {
                    throw new AppException(ErrorCode.USER_BANNED_FROM_COMMENTING);
                });

        comment.setContent(request.getContent());
        comment.setUpdateAt(LocalDateTime.now());

        return commentMapper.toCommentResponse(commentRepository.save(comment));
    }
}
