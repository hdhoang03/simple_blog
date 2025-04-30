package com.example.blog.mapper;

import com.example.blog.dto.request.UserBanRequest;
import com.example.blog.dto.response.UserBanResponse;
import com.example.blog.entity.User;
import com.example.blog.entity.UserBan;
import com.example.blog.service.UserService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserService.class})
public interface UserBanMapper {
    @Mapping(source = "userId", target = "user")
    UserBan toUserBan(UserBanRequest request);

    @Mapping(source = "user.id", target = "userId")
    UserBanResponse toUserBanResponse(UserBan userBan);

    default User map(String userId){
        User user = new User();
        user.setId(userId);
        return user;
    }
}
