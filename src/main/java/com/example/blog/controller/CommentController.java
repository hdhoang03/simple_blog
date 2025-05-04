package com.example.blog.controller;

import com.example.blog.dto.ApiResponse;
import com.example.blog.dto.request.CommentRequest;
import com.example.blog.dto.response.CommentResponse;
import com.example.blog.service.CommentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {
    CommentService commentService;

    @PostMapping("/{postId}")
    ApiResponse<CommentResponse> createComment(@PathVariable String postId, @RequestBody CommentRequest request){
        return ApiResponse.<CommentResponse>builder()
                .code(1000)
                .result(commentService.createComment(postId, request))
                .build();
    }

    @GetMapping("/{postId}")
    ApiResponse<List<CommentResponse>> getAllCommentsFromPost(@PathVariable String postId){
        return ApiResponse.<List<CommentResponse>>builder()
                .code(1000)
                .result(commentService.getCommentsByPost(postId))
                .build();
    }

    @PutMapping("/edit/{commentId}")
    ApiResponse<CommentResponse> editComment(@PathVariable String commentId , @RequestBody CommentRequest request){
        return ApiResponse.<CommentResponse>builder()
                .code(1000)
                .result(commentService.updateComment(commentId, request))
                .build();
    }

    @DeleteMapping("/delete/{commentId}")
    ApiResponse<CommentResponse> deleteComment(@PathVariable String commentId){
        commentService.deleteComment(commentId);
        return ApiResponse.<CommentResponse>builder()
                .code(1000)
                .message("Comment has been deleted!")
                .build();
    }
}
