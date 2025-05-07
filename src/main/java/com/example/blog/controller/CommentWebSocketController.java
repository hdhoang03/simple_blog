package com.example.blog.controller;

import com.example.blog.dto.request.CommentRequest;
import com.example.blog.dto.response.CommentResponse;
import com.example.blog.service.CommentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CommentWebSocketController {
    CommentService commentService;
    SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/comment.{postId}")
    public void handleComment(CommentRequest request, @DestinationVariable String postId){
        CommentResponse response = commentService.createComment(postId, request);
        messagingTemplate.convertAndSend("/topic/comments." + postId, response);
    }
}
