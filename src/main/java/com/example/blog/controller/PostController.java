package com.example.blog.controller;

import com.example.blog.dto.ApiResponse;
import com.example.blog.dto.request.PostRequest;
import com.example.blog.dto.response.PostAdminResponse;
import com.example.blog.dto.response.PostListResponse;
import com.example.blog.dto.response.PostUserResponse;
import com.example.blog.service.PostService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
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
    ApiResponse<PostListResponse> getAllPostsForUser(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size){
        return ApiResponse.<PostListResponse>builder()
                .code(1000)
                .result(postService.getAllPostsForUser(page, size))
                .build();
    }

    @GetMapping("/search")
    ApiResponse<PostListResponse> getAllPostsByTitle(@RequestParam String postTitle,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size){
        return ApiResponse.<PostListResponse>builder()
                .code(1000)
                .result(postService.getAllPostsByTitle(postTitle, page, size))
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
