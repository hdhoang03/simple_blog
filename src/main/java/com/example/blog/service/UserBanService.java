package com.example.blog.service;

import com.example.blog.constaint.BanAction;
import com.example.blog.dto.request.UnbanRequest;
import com.example.blog.dto.request.UserBanRequest;
import com.example.blog.dto.response.UserBanResponse;
import com.example.blog.entity.User;
import com.example.blog.entity.UserBan;
import com.example.blog.exception.AppException;
import com.example.blog.exception.ErrorCode;
import com.example.blog.mapper.UserBanMapper;
import com.example.blog.repository.UserBanRepository;
import com.example.blog.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserBanService {
    UserBanRepository userBanRepository;
    UserRepository userRepository;
    UserBanMapper userBanMapper;

    public UserBanResponse banUser(UserBanRequest request){
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        userBanRepository.findByUserIdAndBanAction(user.getId(), request.getBanAction())
                .ifPresent(ban -> {
                    throw new AppException(ErrorCode.USER_ALREADY_BANNED);
                });

        UserBan userBan = UserBan.builder()
                .user(user)
                .banAction(request.getBanAction())
                .bannedAt(LocalDateTime.now())
                .expiredAt(request.getExpiredAt())
                .build();

        return userBanMapper.toUserBanResponse(userBanRepository.save(userBan));
    }

    public void unbanUser(UnbanRequest request){
        UserBan userBan = userBanRepository.findByUserIdAndBanAction(request.getUserId(), request.getBanAction())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        userBanRepository.delete(userBan);
    }

    public List<UserBanResponse> getBansByUserId(String userId){
        return userBanRepository.findByUserId(userId).stream()
                .map(userBanMapper::toUserBanResponse)
                .collect(Collectors.toList());
    }

    public List<UserBanResponse> getAllActiveBannedUsers(){
        LocalDateTime now = LocalDateTime.now();
        return userBanRepository.findByExpiredAtAfter(now).stream()
                .map(userBanMapper::toUserBanResponse)
                .collect(Collectors.toList());
    }
}
