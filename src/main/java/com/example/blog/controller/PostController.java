package com.example.blog.controller;

import com.example.blog.dto.ApiResponse;
import com.example.blog.dto.request.PostRequest;
import com.example.blog.dto.response.PostAdminResponse;
import com.example.blog.dto.response.PostUserResponse;
import com.example.blog.service.PostService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostController {
    PostService postService;

    @PostMapping
    ApiResponse<PostUserResponse> createPost(@RequestBody PostRequest request){
        return ApiResponse.<PostUserResponse>builder()
                .code(1000)
                .result(postService.createPost(request))
                .build();
    }

    @GetMapping("/all-posts")
    ApiResponse<List<PostUserResponse>> getAllPostsForUser(){
        return ApiResponse.<List<PostUserResponse>>builder()
                .code(1000)
                .result(postService.getAllPostsForUser())
                .build();
    }

    @GetMapping("/{postId}")
    ApiResponse<PostUserResponse> getPostForUserById(@PathVariable String postId){
        return ApiResponse.<PostUserResponse>builder()
                .code(1000)
                .result(postService.getPostForUserById(postId))
                .build();
    }

    @GetMapping("/admin/{postId}")
    ApiResponse<PostAdminResponse> getPostForAdminById(@PathVariable String postId){
        return ApiResponse.<PostAdminResponse>builder()
                .code(1000)
                .result(postService.getPostForAdminById(postId))
                .build();
    }

    @GetMapping("/admin/all-posts")
    ApiResponse<List<PostAdminResponse>> getAllPostsForAdmin(){
        return ApiResponse.<List<PostAdminResponse>>builder()
                .code(1000)
                .result(postService.getAllPostForAdmin())
                .build();
    }

    @PutMapping("/edit/{postId}")
    ApiResponse<PostUserResponse> editPost(@PathVariable String postId, @RequestBody PostRequest request){
        return ApiResponse.<PostUserResponse>builder()
                .code(1000)
                .result(postService.updatePost(postId, request))
                .build();
    }

    @DeleteMapping("/delete/{postId}")
    ApiResponse<Void> deletePost(@PathVariable String postId){
        postService.deletePost(postId);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("This post has been deleted!")
                .build();
    }
}
