package com.example.blog.controller;

import com.example.blog.constaint.BanAction;
import com.example.blog.dto.ApiResponse;
import com.example.blog.dto.request.UnbanRequest;
import com.example.blog.dto.request.UserBanRequest;
import com.example.blog.dto.response.UserBanResponse;
import com.example.blog.service.UserBanService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/banAction")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserBanActionController {
    UserBanService userBanService;

    @PostMapping
    ApiResponse<UserBanResponse> banUser(@RequestBody UserBanRequest request){
        return ApiResponse.<UserBanResponse>builder()
                .code(1000)
                .result(userBanService.banUser(request))
                .build();
    }

    @PostMapping("/unban")
    ApiResponse<Void> unbanUser(@RequestBody UnbanRequest request){
        userBanService.unbanUser(request);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("This user has been unbanned!")
                .build();
    }

    @GetMapping("/{userId}")
    ApiResponse<List<UserBanResponse>> getBanByUserId(@PathVariable String userId){
        return ApiResponse.<List<UserBanResponse>>builder()
                .code(1000)
                .result(userBanService.getBansByUserId(userId))
                .build();
    }

    @GetMapping("/banned-users")
    ApiResponse<List<UserBanResponse>> getAllBannedUsers(){
        return ApiResponse.<List<UserBanResponse>>builder()
                .code(1000)
                .result(userBanService.getAllActiveBannedUsers())
                .build();
    }
}
