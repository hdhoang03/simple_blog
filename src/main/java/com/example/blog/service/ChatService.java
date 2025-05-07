package com.example.blog.service;

import com.example.blog.dto.request.MessageRequest;
import com.example.blog.entity.Message;
import com.example.blog.repository.MessageRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatService {
    MessageRepository messageRepository;

    public void saveMessage(MessageRequest request){
        Message message = Message.builder()
                .fromUser(request.getFrom())
                .toUser(request.getTo())
                .content(request.getContent())
                .timestamp(request.getTimestamp() != null
                        ? request.getTimestamp()
                        : LocalDateTime.now())
                .build();
        messageRepository.save(message);
    }

    public List<MessageRequest> getConversation(String user1, String user2){
        List<Message> messages = messageRepository
                .findByFromUserAndToUserOrToUserAndFromUser(user1, user2);
        return messages.stream()
                .map(e -> MessageRequest.builder()
                        .from(e.getFromUser())
                        .to(e.getToUser())
                        .content(e.getContent())
                        .timestamp(e.getTimestamp())
                        .build())
                .toList();
    }
}
