package com.example.blog.controller;

import com.example.blog.dto.ApiResponse;
import com.example.blog.dto.request.EmailCreationRequest;
import com.example.blog.dto.request.VerifyEmailRequest;
import com.example.blog.dto.response.EmailResponse;
import com.example.blog.service.EmailService;
import com.example.blog.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mail")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailController {
    EmailService emailService;
    UserService userService;

    @PostMapping("/verify-email")
    ApiResponse<Void> verifyEmail(@RequestBody VerifyEmailRequest request){
        boolean isVerified = userService.verifyEmail(request.getUsername(), request.getCode());
        if(isVerified){
            return ApiResponse.<Void>builder()
                    .code(1000)
                    .message("Email has been verified successfully!")
                    .build();
        }
        return ApiResponse.<Void>builder()
                .code(4000)
                .message("Invalid or expired verification code!")
                .build();
    }

    @PostMapping("/send")
    ApiResponse<EmailResponse> sendEmail(@RequestBody EmailCreationRequest request){
        emailService.sendEmail(request);
        return ApiResponse.<EmailResponse>builder()
                .code(1000)
                .message("Email has been sent successfully!")
                .build();
    }
}
