package com.example.blog.service;

import com.example.blog.constaint.PredefinedRole;
import com.example.blog.dto.request.*;
import com.example.blog.dto.response.UserResponse;
import com.example.blog.entity.Role;
import com.example.blog.entity.User;
import com.example.blog.exception.AppException;
import com.example.blog.exception.ErrorCode;
import com.example.blog.mapper.UserMapper;
import com.example.blog.repository.RoleRepository;
import com.example.blog.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserMapper userMapper;
    UserRepository userRepository;
    RoleRepository roleReponsitory;
    PasswordEncoder passwordEncoder;
    RedisService redisService;
    EmailService emailService;

    public UserResponse createUser(UserCreationRequest request){
        if(userRepository.existsByUsername(request.getUsername())){
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        String verificationCode = generateVerificationCode();
        String redisKey = "signup:" + request.getUsername();

        //Lưu vào redis
        TemporaryUser userData = TemporaryUser.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .name(request.getName())
                .verificationCode(verificationCode)
                .build();

        redisService.setValue(redisKey, userData, 5);
        //Gửi email
        emailService.sendVerificationCode(SendVerificationEmailRequest.builder()
                .recipientEmail(userData.getEmail())
                .verificationCode(verificationCode)
                .build());

        return UserResponse.builder()
                .username(userData.getUsername())
                .email(userData.getEmail())
                .name(userData.getName())
                .enabled(false)
                .build();
    }

    public UserResponse updateUser(String userId, UserUpdateRequest request){
        User user = userRepository.findById(userId).orElseThrow(()-> new RuntimeException("User not found."));

        userMapper.updateUser(user, request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
//        var roles = roleReponsitory.findAllById(request.getRoles());
//        user.setRoles(new HashSet<>(roles));
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(String userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if(user.getUsername().equalsIgnoreCase("admin")){
            user.setEnabled(false);
            userRepository.save(user);
            log.info("Admin account disabled instead of deleted.");
            return;
        }

        user.getRoles().clear();
        userRepository.save(user);
        userRepository.deleteById(userId);
    }

//    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUser(){
        return userRepository.findAll()
                .stream()//thay vì dùng for dùng stream sẽ gọn hơn
                .map(userMapper::toUserResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void lockOrUnlockUser(LockUserRequest request){
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        user.setEnabled(request.isLock());//true = active, false = inactive
        userRepository.save(user);
    }

    public UserResponse getMyInfo(){
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(userName).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }

    //Tạo ngẫu nhiên xác thực 6 số
    private String generateVerificationCode(){
        return String.format("%06d", new Random().nextInt(1000000));
    }

    public boolean verifyEmail(String username, String code){
        String redisKey = "signup:" + username;
        TemporaryUser tempUser = (TemporaryUser) redisService.getValue(redisKey, TemporaryUser.class);

        if(tempUser == null || !tempUser.getVerificationCode().equals(code)){
            return false;
        }
        //Tạo user thực
        User user = User.builder()
                .username(tempUser.getUsername())
                .password(tempUser.getPassword())
                .email(tempUser.getEmail())
                .name(tempUser.getName())
                .enabled(true)
                .build();

        //Gán role
        HashSet<Role> roles = new HashSet<>();
        roleReponsitory.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);
        user.setRoles(roles);

        userRepository.save(user);
        redisService.deleteValue(redisKey);//Xác minh xong xóa khỏi redis

        return true;
    }
}