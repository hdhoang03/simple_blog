package com.example.blog.controller;

import com.example.blog.dto.ApiResponse;
import com.example.blog.dto.request.MessageRequest;
import com.example.blog.service.ChatService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    SimpMessagingTemplate simpMessagingTemplate;
    ChatService chatService;

    @MessageMapping("/chat.private")
    public void sendPrivateMessage(@Payload MessageRequest request, Principal principal){
        log.info("Received payload: {}", request);
        if (principal == null) {
            log.error("No authenticated user found");
            return;
        }
        String toUser = request.getTo();
        String fromUser = principal.getName();

        log.info("Username: " + fromUser);
        request.setFrom(fromUser);
        request.setTimestamp(LocalDateTime.now());

        log.info("Received message from {} to {}: {}", fromUser, toUser, request.getContent());

        simpMessagingTemplate.convertAndSendToUser(toUser, "/private", request);
        simpMessagingTemplate.convertAndSendToUser(fromUser, "/private", request); // Gửi lại cho người gửi

        chatService.saveMessage(request);
        log.info("Private message from {} to {}: {}", request.getFrom(), toUser, request.getContent());
//        return request;
    }

    @GetMapping("/chat/history/{user1}/{user2}")
    ApiResponse<List<MessageRequest>> getConversation(@PathVariable String user1, @PathVariable String user2){
        return ApiResponse.<List<MessageRequest>>builder()
                .code(1000)
                .result(chatService.getConversation(user1, user2))
                .build();
    }
}
