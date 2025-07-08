package com.example.blog.controller;

import com.example.blog.dto.ApiResponse;
import com.example.blog.dto.request.LockUserRequest;
import com.example.blog.dto.request.UserCreationRequest;
import com.example.blog.dto.request.UserUpdateRequest;
import com.example.blog.dto.response.UserResponse;
import com.example.blog.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/users")
@Slf4j
public class UserController {
    UserService userService;

    @PostMapping
    ApiResponse<UserResponse> createUser(@RequestBody UserCreationRequest request){
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }

    @GetMapping("/admin/get-all-users")
    ApiResponse<List<UserResponse>> getAllUser(){
        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getUser())
                .build();
    }

    @GetMapping("/admin/search")
    ApiResponse<List<UserResponse>> searchUserByUsername(@RequestParam String keyword){
        return ApiResponse.<List<UserResponse>>builder()
                .code(1000)
                .result(userService.searchUserByUsername(keyword))
                .build();
    }

    @GetMapping("/my-info")
    ApiResponse<UserResponse> getMyInfo(){
        return ApiResponse.<UserResponse>builder()
                .code(1000)
                .result(userService.getMyInfo())
                .build();
    }

    @PutMapping("/{userId}")
    ApiResponse<UserResponse> updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request){
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(userId, request))
                .build();
    }

    @DeleteMapping("/admin/delete/{userId}")
    ApiResponse<String> deleteUser(@PathVariable String userId){
        userService.deleteUser(userId);
        return ApiResponse.<String>builder()
                .result("User has been deleted.")
                .build();
    }

    @PostMapping("/admin/lock")
    ApiResponse<Void> lockOrUnlockUser(@RequestBody LockUserRequest request){
        userService.lockOrUnlockUser(request);
        String message = request.isLock() ? "User account has been locked." : "User account has been unlocked.";
        return ApiResponse.<Void>builder()
                .message(message)
                .build();
    }
}
